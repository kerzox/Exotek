package mod.kerzox.exotek.common.capability.energy.cable_impl;

import mod.kerzox.exotek.common.block.transport.EnergyCableBlock;
import mod.kerzox.exotek.common.blockentities.transport.IPipe;
import mod.kerzox.exotek.common.blockentities.transport.PipeNetwork;
import mod.kerzox.exotek.common.blockentities.transport.PipeTiers;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.fluid.FluidPipeHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.nio.channels.Pipe;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergySingleNetwork {

    protected PipeTiers tier;
    protected LevelEnergyNetwork levelNetwork;
    private HashSet<BlockPos> network = new HashSet<>();
    private HashSet<BlockPos> connectedInventories = new HashSet<>();
    protected int individualCableCapacity = 512;

    private int waitOnTick = 0;

    // energy handlers
    private LevelEnergyTransferHandler storage = new LevelEnergyTransferHandler(individualCableCapacity);
    private LazyOptional<IEnergyStorage> handler = LazyOptional.of(() -> storage);

    public EnergySingleNetwork(PipeTiers tier, LevelEnergyNetwork entireLevel) {
        levelNetwork = entireLevel;
        this.tier = tier;
    }

    public void tick() {

        AtomicInteger currentEnergy = new AtomicInteger(this.storage.getEnergyStored());
        if (currentEnergy.get() <= 0) return;

        Set<IEnergyStorage> inventories = getAllOutputs();
        if (inventories.size() == 0) return;

        int amount = currentEnergy.get() / inventories.size();
        for (IEnergyStorage capability : inventories) {
            if (capability != null) {
                if (capability.canReceive()) {
                    int received = capability.receiveEnergy(amount, false);
                    storage.extractEnergy(received, false);
                }
            }
        }
    }

    public Set<IEnergyStorage> getAllOutputs() {
        Set<IEnergyStorage> allInventories = new HashSet<>();

        for (BlockPos pos : connectedInventories) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbouringPosition = pos.relative(direction);
                BlockEntity blockEntity = getLevel().getBlockEntity(neighbouringPosition);
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
                LazyOptional<ILevelNetwork> energyCapability = getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY);
                if (!energyCapability.isPresent()) continue;
                if (energyCapability.resolve().isEmpty()) continue;

                if (energyCapability.resolve().get() instanceof LevelEnergyNetwork energyNetwork) {
                    EnergySingleNetwork single = energyNetwork.getNetworkFromPosition(neighbouringPosition);
                    if (single != null && single.storage == this.storage) {
                        continue;
                    }
                    if (single != null) allInventories.add(single.storage);
                }

            }
        }
        return allInventories;
    }



    public void addInventory(BlockPos cablePos, LazyOptional<IEnergyStorage> lazyOptional) {
        connectedInventories.add(cablePos);
    }

    private void updateEnergyCapacity() {
        this.storage.setCapacity(this.network.size() * individualCableCapacity);
    }

    public boolean isInNetwork(BlockPos pos) {
        return network.contains(pos);
    }

    public void attach(BlockPos chosenPosition) {
        this.network.add(chosenPosition);

        if (!getLevel().isClientSide) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbouringPosition = chosenPosition.relative(direction);
                findBlockEntityInventory(chosenPosition, direction, neighbouringPosition);
                findLevelPositionInventories(chosenPosition, direction, neighbouringPosition);
            }
        }

        updateEnergyCapacity();
    }

    private void findLevelPositionInventories(BlockPos chosenPosition, Direction direction, BlockPos neighbouringPosition) {
        getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof LevelEnergyNetwork network) {
                EnergySingleNetwork single = network.getNetworkFromPosition(neighbouringPosition);
                if (single != null) {
                    if (!getLevel().isClientSide)
                        System.out.println("Found a level position that has an energy capability next to cable at " + chosenPosition.toShortString());
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
                if (!getLevel().isClientSide)
                    System.out.println("Found a block entity that has an energy capability next to cable at " + chosenPosition.toShortString());
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
        this.network.remove(clickedPos);
        this.connectedInventories.remove(clickedPos);
        updateEnergyCapacity();
    }

    public HashSet<BlockPos> getNetwork() {
        return network;
    }

    public int size() {
        return this.network.size();
    }

    public Level getLevel() {
        return this.levelNetwork.level;
    }

    public CompoundTag write() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        getNetwork().forEach((pos -> {
            list.add(NbtUtils.writeBlockPos(pos));
        }));
        tag.put("positions", list);
        tag.putString("tier", this.tier.getName().toLowerCase());
        return tag;
    }

    public void read(CompoundTag tag) {
        readPositionsFromTag(tag);
    }

    private void readPositionsFromTag(CompoundTag tag) {
        if (tag.contains("positions")) {
            ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                attach(NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }
    }

    public static EnergySingleNetwork create(LevelEnergyNetwork network, PipeTiers tier, BlockPos... positions) {
        EnergySingleNetwork singleNetwork = new EnergySingleNetwork(tier, network);
        for (BlockPos position : positions) {
            singleNetwork.attach(position);
        }
        return singleNetwork;
    }


    // this reads from a position array
    public static EnergySingleNetwork create(LevelEnergyNetwork network, CompoundTag tag) {
        EnergySingleNetwork singleNetwork = new EnergySingleNetwork(PipeTiers.valueOf(tag.getString("tier").toUpperCase()), network);
        singleNetwork.read(tag);
        return singleNetwork;
    }

    public void checkForInventories() {
        for (BlockPos pos : this.network) {
            for (Direction direction : Direction.values()) {
                BlockPos neighbouringPosition = pos.relative(direction);
                findBlockEntityInventory(pos, direction, neighbouringPosition);
            }
        }
    }

    public PipeTiers getTier() {
        return tier;
    }
}
