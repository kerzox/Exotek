package mod.kerzox.exotek.common.capability.energy.cable_impl;

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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class EnergySingleNetworkLazyOptionalVersion {

    protected LevelEnergyNetwork levelNetwork;
    private HashSet<BlockPos> network = new HashSet<>();
    private Map<BlockPos, Set<LazyOptional<IEnergyStorage>>> connectedInventories = new HashMap<>();
    protected int individualCableCapacity = 512;

    // energy handlers
    private LevelEnergyTransferHandler storage = new LevelEnergyTransferHandler(individualCableCapacity);
    private LazyOptional<LevelEnergyTransferHandler> handler = LazyOptional.of(() -> storage);

    public EnergySingleNetworkLazyOptionalVersion(LevelEnergyNetwork entireLevel) {
        levelNetwork = entireLevel;
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
        for (Set<LazyOptional<IEnergyStorage>> storages : connectedInventories.values()) {
            for (LazyOptional<IEnergyStorage> lazyOptional : storages) {
                if (!lazyOptional.isPresent()) continue;
                if (lazyOptional.resolve().isEmpty()) continue;
                IEnergyStorage cap = lazyOptional.resolve().get();
                if (cap instanceof LevelEnergyTransferHandler levelEnergyTransferHandler) {
                    if (levelEnergyTransferHandler == this.storage) {
                        continue;
                    }
                }
                allInventories.add(cap);
            }
        }
        return allInventories;
    }

    private IEnergyStorage findSmallestStorage() {
        IEnergyStorage ret = null;
        int min = Integer.MAX_VALUE;

        for (Set<LazyOptional<IEnergyStorage>> storages : connectedInventories.values()) {
            for (LazyOptional<IEnergyStorage> lazyOptional : storages) {
                if (!lazyOptional.isPresent()) continue;
                if (lazyOptional.resolve().isEmpty()) continue;
                IEnergyStorage cap = lazyOptional.resolve().get();
                if (cap instanceof LevelEnergyTransferHandler levelEnergyTransferHandler) {
                    if (levelEnergyTransferHandler == this.storage) {
                        continue;
                    }
                }
                if (cap.getEnergyStored() < min) {
                    ret = cap;
                    min = cap.getEnergyStored();
                    return ret;
                }
            }
        }


        return ret;
    }

    public void addInventory(BlockPos cablePos, LazyOptional<IEnergyStorage> lazyOptional) {
        this.connectedInventories.computeIfAbsent(cablePos, k -> new HashSet<>()).add(lazyOptional);
        lazyOptional.addListener(cap -> this.connectedInventories.get(cablePos).remove(lazyOptional));
    }

    private void updateEnergyCapacity() {
        this.storage.setCapacity(this.network.size() * individualCableCapacity);
    }

    public boolean isInNetwork(BlockPos pos) {
        return network.contains(pos);
    }

    public void attach(BlockPos chosenPosition) {
        this.network.add(chosenPosition);
        this.connectedInventories.put(chosenPosition, new HashSet<>());

        for (Direction direction : Direction.values()) {
            BlockPos neighbouringPosition = chosenPosition.relative(direction);
            findBlockEntityInventory(chosenPosition, direction, neighbouringPosition);
            // find level inventories
        }

        updateEnergyCapacity();
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

    public LazyOptional<LevelEnergyTransferHandler> getHandler() {
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

    public static EnergySingleNetworkLazyOptionalVersion create(LevelEnergyNetwork network, BlockPos... positions) {
        EnergySingleNetworkLazyOptionalVersion singleNetwork = new EnergySingleNetworkLazyOptionalVersion(network);
        singleNetwork.network.addAll(Arrays.asList(positions));
        return singleNetwork;
    }


    // this reads from a position array
    public static EnergySingleNetworkLazyOptionalVersion create(LevelEnergyNetwork network, CompoundTag tag) {
        EnergySingleNetworkLazyOptionalVersion singleNetwork = new EnergySingleNetworkLazyOptionalVersion(network);
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
}
