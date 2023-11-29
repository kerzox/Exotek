package mod.kerzox.exotek.common.capability.energy.cable_impl;

import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergySubNetwork implements ILevelSubNetwork {

    protected UUID uuid;
    protected CapabilityTiers tier;
    protected LevelEnergyNetwork levelNetwork;
    private NodeList network = new NodeList();
    protected int individualCableCapacity = 250;

    private HashSet<LevelNode> connectedInventories = new HashSet<>();
    private HashSet<LevelNode> forceExtraction = new HashSet<>();
    private HashSet<LevelNode> forceInsertion = new HashSet<>();

    private Queue<LevelNode> positionsAsNewCables = new LinkedList<>();

    // energy handlers
    private LevelEnergyTransferHandler storage = new LevelEnergyTransferHandler(individualCableCapacity) {
        @Override
        protected void onContentsChanged() {
            for (LevelNode node : network.getNodes()) {
                if (getLevel().getBlockEntity(node.getWorldPosition()) instanceof EnergyCableEntity entity) {
                    entity.syncBlockEntity();
                }
            }
        }
    };


    private LazyOptional<IEnergyStorage> handler = LazyOptional.of(() -> storage);

    public EnergySubNetwork(CapabilityTiers tier, LevelEnergyNetwork entireLevel) {
        levelNetwork = entireLevel;
        this.tier = tier;
    }

    public void tick() {

        if (!positionsAsNewCables.isEmpty()) {
            spawnCablesInWorld(positionsAsNewCables.poll());
        }

        AtomicInteger currentEnergy = new AtomicInteger(this.storage.getEnergyStored());

        // try to extract first
        if (!forceExtraction.isEmpty()) {
            tryExtraction(currentEnergy);
        }

        if (currentEnergy.get() <= 0) return;

        // do default function
        Set<IEnergyStorage> inventories = getAllOutputs();
        if (inventories.size() == 0) return;

        for (IEnergyStorage capability : inventories) {
            if (capability != null) {
                if (capability.canReceive()) {
                    // the amount our cable can move at once.
                    int amount = Math.min(currentEnergy.get(), tier.getTransfer()) / inventories.size();

                    // the amount we actually transfered
                    int received = capability.receiveEnergy(amount, false);
                    storage.consumeEnergy(received);
                }
            }
        }

    }


    /**
     * Method loops through all the cables that are on extraction mode and checks if the direction is set to a extract
     * If so then it will attempt to extract energy from this direction.
     */
    public void tryExtraction(AtomicInteger currentEnergy) {
        for (LevelNode node : this.forceExtraction) {
            for (Direction direction : Direction.values()) {
                if (node.getDirectionalIO().get(direction) == IOTypes.EXTRACT) {
                    BlockPos pos = node.getWorldPosition().relative(direction);
                    BlockEntity blockEntity = getLevel().getBlockEntity(pos);
                    if (blockEntity != null) {
                        LazyOptional<IEnergyStorage> energyCapability = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                        energyCapability.ifPresent(cap -> {
                            if (cap.canExtract() && cap.getEnergyStored() != 0) {
                                int simulated = cap.extractEnergy(tier.getTransfer(), true);
                                int toExtract = this.storage.receiveEnergy(simulated, true);
                                this.storage.receiveEnergy(cap.extractEnergy(toExtract, false), false);
                            }
                        });
                    }
                }
            }
        }
    }

    public Set<IEnergyStorage> getAllOutputs() {
        Set<IEnergyStorage> allInventories = new HashSet<>();

        for (LevelNode node : connectedInventories) {
            for (Direction direction : Direction.values()) {
                if (node.getDirectionalIO().get(direction) == IOTypes.NONE ||
                        node.getDirectionalIO().get(direction) == IOTypes.EXTRACT) continue;
                BlockPos pos = node.getWorldPosition();
                BlockPos neighbouringPosition = pos.relative(direction);
                BlockEntity blockEntity = getLevel().getBlockEntity(neighbouringPosition);

                LazyOptional<IEnergyCapabilityLevelNetwork> levelNetworkLazyOptional = getLevel().getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY);
                if (!levelNetworkLazyOptional.isPresent()) continue;
                if (levelNetworkLazyOptional.resolve().isEmpty()) continue;

                if (levelNetworkLazyOptional.resolve().get() instanceof LevelEnergyNetwork energyNetwork) {
                    EnergySubNetwork single = energyNetwork.getNetworkFromPosition(neighbouringPosition);
                    if (single != null && single.storage == this.storage) {
                        continue;
                    }
                    if (single != null) allInventories.add(single.storage);
                }

                if (blockEntity != null) {
                    LazyOptional<IEnergyStorage> energyCapability = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
                    if (!energyCapability.isPresent()) continue;
                    if (energyCapability.resolve().isEmpty()) continue;
                    IEnergyStorage cap = energyCapability.resolve().get();
                    if (cap instanceof LevelEnergyTransferHandler levelEnergyTransferHandler) {
                        if (levelEnergyTransferHandler == this.storage) {
                            continue;
                        }
                    }
                    allInventories.add(cap);
                }

            }
        }
        return allInventories;
    }

    public void addInventory(BlockPos cablePos, LazyOptional<IEnergyStorage> lazyOptional) {
        connectedInventories.add(network.getByPos(cablePos));
    }


    private void updateEnergyCapacity() {
        this.storage.setCapacity(this.network.size() * this.tier.getTransfer());
    }

    public boolean isInNetwork(BlockPos pos) {
        return network.hasPosition(pos);
    }

    public void attach(BlockPos chosenPosition) {
        this.network.addByPosition(chosenPosition);

        if (!getLevel().isClientSide) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbouringPosition = chosenPosition.relative(direction);
                findBlockEntityInventory(chosenPosition, direction, neighbouringPosition);
                findLevelPositionInventories(chosenPosition, direction, neighbouringPosition);
            }
        }

        updateEnergyCapacity();
        levelNetwork.updateClients(chosenPosition);
    }

    public void attach(LevelNode node) {
        this.network.addNode(node);

        if (!getLevel().isClientSide) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbouringPosition = node.getWorldPosition().relative(direction);
                findBlockEntityInventory(node.getWorldPosition(), direction, neighbouringPosition);
                findLevelPositionInventories(node.getWorldPosition(), direction, neighbouringPosition);
            }
        }

        updateEnergyCapacity();
        checkNodeForSpecialType(node);
        levelNetwork.updateClients(node.getWorldPosition());
    }

    private void findLevelPositionInventories(BlockPos chosenPosition, Direction direction, BlockPos neighbouringPosition) {
        getLevel().getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof LevelEnergyNetwork network) {
                EnergySubNetwork single = network.getNetworkFromPosition(neighbouringPosition);
                if (single != null) {
                    addInventory(chosenPosition, single.getHandler());
                }
            }
        });
    }

    private void findBlockEntityInventory(BlockPos chosenPosition, Direction direction, BlockPos neighbouringPosition) {
        BlockEntity blockEntity = getLevel().getBlockEntity(neighbouringPosition);
        if (blockEntity != null) {
            LazyOptional<IEnergyStorage> energyCapability = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction);
            energyCapability.ifPresent(cap -> {
                addInventory(chosenPosition, energyCapability);
            });
        }

    }


    public LazyOptional<IEnergyStorage> getHandler() {
        return handler;
    }

    public LevelEnergyTransferHandler getInternalStorage() {
        return storage;
    }

    public void detach(BlockPos clickedPos) {
        this.network.removeNodeByPosition(clickedPos);
        this.connectedInventories.remove(clickedPos);
        updateEnergyCapacity();
        levelNetwork.updateClients(clickedPos);
    }

    public NodeList getNetwork() {
        return network;
    }

    public int size() {
        return this.network.size();
    }

    public Level getLevel() {
        return this.levelNetwork.level;
    }

    @Override
    public LevelNode getNodeByPosition(BlockPos pos) {
        return network.getByPos(pos);
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        getNetwork().getNodes().forEach(node -> {
            // list.add(NbtUtils.writeBlockPos(node.getWorldPosition()));
            list.add(node.serialize());
        });
        tag.put("nodes", list);
        tag.putString("tier", this.tier.getName().toLowerCase());
        CompoundTag tag1 = new CompoundTag();
        tag.put("energyHandler", this.storage.serializeNBT());
        return tag;
    }

    public void read(CompoundTag tag) {
        this.storage.deserializeNBT(tag.getCompound("energyHandler"));
        readPositionsFromTag(tag);
    }

    private void readPositionsFromTag(CompoundTag tag) {
        if (tag.contains("nodes")) {
            ListTag list = tag.getList("nodes", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                LevelNode node = new LevelNode(list.getCompound(i));
                attach(node);
            }
        }
    }

    public void checkNodeForSpecialType(LevelNode node) {
        forceExtraction.remove(node);
        forceInsertion.remove(node);
        for (IOTypes type : node.getDirectionalIO().values()) {
            if (type == IOTypes.EXTRACT) forceExtraction.add(node);
            if (type == IOTypes.PUSH) forceInsertion.add(node);
        }
    }

    public static EnergySubNetwork create(LevelEnergyNetwork network, CapabilityTiers tier, BlockPos... positions) {
        EnergySubNetwork singleNetwork = new EnergySubNetwork(tier, network);
        for (BlockPos position : positions) {
            singleNetwork.attach(position);
        }
        return singleNetwork;
    }


    // this reads from a position array
    public static EnergySubNetwork create(LevelEnergyNetwork network, CompoundTag tag) {
        EnergySubNetwork singleNetwork = new EnergySubNetwork(CapabilityTiers.valueOf(tag.getString("tier").toUpperCase()), network);
        singleNetwork.read(tag);
        return singleNetwork;
    }

    public void checkForInventories() {
        for (LevelNode node : this.network.getNodes()) {
            BlockPos pos = node.getWorldPosition();
            for (Direction direction : Direction.values()) {
                BlockPos neighbouringPosition = pos.relative(direction);
                findBlockEntityInventory(node.getWorldPosition(), direction, neighbouringPosition);
                findLevelPositionInventories(node.getWorldPosition(), direction, neighbouringPosition);
            }
        }
    }

    public CapabilityTiers getTier() {
        return tier;
    }

    public void spawnCablesInWorld(LevelNode pos) {
        if (getLevel().getBlockEntity(pos.getWorldPosition()) == null) {
            if (getLevel().getBlockState(pos.getWorldPosition()).getBlock() == Blocks.AIR) {
                getLevel().setBlockAndUpdate(pos.getWorldPosition(), getCableFromTier(this.tier));
            }
        }
    }

    public void markPositionAsNewCable(LevelNode pos) {
        positionsAsNewCables.add(pos);
    }

    private BlockState getCableFromTier(CapabilityTiers tier) {
        switch (tier) {
            case ADVANCED -> {
                return ExotekRegistry.Blocks.ENERGY_CABLE_2_BLOCK.get().defaultBlockState();
            }
            case HYPER -> {
                return ExotekRegistry.Blocks.ENERGY_CABLE_3_BLOCK.get().defaultBlockState();
            }
            default -> {
                return ExotekRegistry.Blocks.ENERGY_CABLE_BLOCK.get().defaultBlockState();
            }
        }
    };
}
