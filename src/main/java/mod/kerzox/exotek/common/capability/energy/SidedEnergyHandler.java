package mod.kerzox.exotek.common.capability.energy;

import mod.kerzox.exotek.common.capability.CapabilityHolder;
import mod.kerzox.exotek.common.capability.ICapabilitySerializer;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.HashSet;

/**
 * This is a fully fledged handler no need for lazy in the block entities
 *
 */
public class SidedEnergyHandler implements IStrictInventory, ICapabilitySerializer, CapabilityHolder<SidedEnergyHandler> {

    HashSet<Direction> inputs = new HashSet<>();
    HashSet<Direction> outputs = new HashSet<>();

    // input wrapper
    private final InputWrapper inputWrapper;
    // output handler
    private final OutputWrapper outputWrapper;

    private final CombinedWrapper combinedWrapper;

    public SidedEnergyHandler(long capacity, long maxReceive, long maxExtract) {
        outputWrapper = new OutputWrapper(this, capacity, maxReceive, maxExtract);
        inputWrapper = new InputWrapper(this, outputWrapper);
        combinedWrapper = new CombinedWrapper(this, outputWrapper);
    }

    public SidedEnergyHandler(long capacity, long maxExtract, Direction... outputDirections) {
        this(capacity, 0, maxExtract);
        removeInputs(Direction.values());
        addOutput(outputDirections);
    }

    public SidedEnergyHandler(long capacity) {
        this(capacity, capacity, capacity);
        addInput(Direction.values());
    }

    protected void addSides() {

    }

    public void invalidate() {
        inputWrapper.getHandler().invalidate();
        outputWrapper.getHandler().invalidate();
        combinedWrapper.getHandler().invalidate();
    }

    public void revalidate() {
        inputWrapper.createHandler();
        outputWrapper.createHandler();
        combinedWrapper.createHandler();
    }

    public InputWrapper getInputWrapper() {
        return inputWrapper;
    }

    public CombinedWrapper getCombinedWrapper() {
        return combinedWrapper;
    }

    public OutputWrapper getOutputWrapper() {
        return outputWrapper;
    }

    public void addEnergy(long amount) {

        this.outputWrapper.addEnergy(amount);
    }

    public void consumeEnergy(long amount) {
        this.outputWrapper.consumeEnergy(amount);
    }

    public int getEnergy() {
        return this.outputWrapper.getEnergyStored();
    }

    public int getMaxEnergy() {
        return this.outputWrapper.getMaxEnergyStored();
    }

    public long getLargeEnergyStored() {
        return this.outputWrapper.getLargeEnergyStored();
    }

    public long getLargeMaxEnergyStored() {
        return this.outputWrapper.getLargeMaxEnergyStored();
    }

    public boolean hasEnough(int amount) {
        return getEnergy() >= amount;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("output", this.outputWrapper.serialize());
        tag.put("strict", serializeInputAndOutput());
        return tag;
    }

    public void deserialize(CompoundTag tag) {
        if (tag.contains("output")) {
            this.outputWrapper.read(tag.getCompound("output"));

            HashSet<Direction> oldInputs = new HashSet<>(getInputs());
            HashSet<Direction> oldOutputs = new HashSet<>(getOutputs());

            deserializeInputAndOutput(tag.getCompound("strict"));

            if (!oldInputs.equals(getInputs()) || !oldOutputs.equals(getOutputs())) {
//            invalidate();
//            revalidate();
            }
        }
    }

    public long getExtractLimit() {
        return this.outputWrapper.maxExtract;
    }
    public long getReceiveLimit() {
        return this.outputWrapper.maxReceive;
    }


    @Override
    public HashSet<Direction> getInputs() {
        return this.inputs;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return this.outputs;
    }

    public <T> LazyOptional<T> getHandler(Direction side) {
        if (side == null) return combinedWrapper.getHandler().cast();

        else if (hasInput(side) && hasOutput(side)) return combinedWrapper.getHandler().cast();
        // return the input wrapper
        else if (hasInput(side)) return inputWrapper.getHandler().cast();
        // return the output wrapper
        else if (hasOutput(side)) return outputWrapper.getHandler().cast();
        // if nothing is valid return empty
        else return LazyOptional.empty();
    }

    public boolean isFull() {
        return this.getEnergy() == this.getMaxEnergy();
    }

    protected void onContentsChanged() {}

    public void setCapacity(long transfer) {
        this.outputWrapper.setCapacity(transfer);
    }

    public void addCapacity(long transfer) {
        this.outputWrapper.addCapacity(transfer);
    }

    public void setExtract(long amount) {
        this.outputWrapper.setExtract(amount);
    }

    public void setReceive(long amount) {
        this.outputWrapper.setReceive(amount);
    }

    @Override
    public SidedEnergyHandler getInstance() {
        return this;
    }

    @Override
    public Capability<?> getType() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public LazyOptional<SidedEnergyHandler> getCapabilityHandler(Direction direction) {
        return getHandler(direction);
    }

    public void setEnergy(long energy) {
        this.outputWrapper.setEnergy(energy);
    }




    public static class CombinedWrapper implements IEnergyStorage, ILargeEnergyStorage, IStrictInventory, ICapabilitySerializer {

        private SidedEnergyHandler mainHandler;
        private OutputWrapper outputWrapper;
        // Handler for the wrapper
        private LazyOptional<CombinedWrapper> wrapper;

        public CombinedWrapper(SidedEnergyHandler main, OutputWrapper outputWrapper) {
            this.outputWrapper = outputWrapper;
            this.mainHandler = main;
            createHandler();
        }

        public void createHandler() {
            this.wrapper = LazyOptional.of(() -> this);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return outputWrapper.internalReceiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return outputWrapper.extractEnergy(maxExtract, simulate);
        }

        @Override
        public int getEnergyStored() {
            return this.outputWrapper.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return this.outputWrapper.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return true;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        public LazyOptional<CombinedWrapper> getHandler() {
            return wrapper;
        }

        protected void onContentsChanged() {
            this.mainHandler.onContentsChanged();
        }

        @Override
        public HashSet<Direction> getInputs() {
            return this.mainHandler.getInputs();
        }

        @Override
        public HashSet<Direction> getOutputs() {
            return this.mainHandler.getOutputs();
        }

        @Override
        public CompoundTag serialize() {
            return mainHandler.serialize();
        }

        @Override
        public void deserialize(CompoundTag tag) {
            mainHandler.deserialize(tag);
        }

        @Override
        public long getLargeEnergyStored() {
            return this.outputWrapper.getLargeEnergyStored();
        }

        @Override
        public long getLargeMaxEnergyStored() {
            return this.outputWrapper.getLargeMaxEnergyStored();
        }

        @Override
        public long receiveEnergy(long maxReceive, boolean simulate) {
            return outputWrapper.internalReceiveEnergy(maxReceive, simulate);
        }

        @Override
        public long extractEnergy(long maxExtract, boolean simulate) {
            return outputWrapper.extractEnergy(maxExtract, simulate);
        }
    }

    public static class InputWrapper implements IEnergyStorage, ILargeEnergyStorage {

        private SidedEnergyHandler mainHandler;
        private OutputWrapper outputWrapper;
        // Handler for input wrapper
        private LazyOptional<InputWrapper> wrapper;

        public InputWrapper(SidedEnergyHandler main, OutputWrapper outputWrapper) {
            this.outputWrapper = outputWrapper;
            this.mainHandler = main;
            createHandler();
        }

        public void createHandler() {
            this.wrapper = LazyOptional.of(() -> this);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return outputWrapper.internalReceiveEnergy(maxReceive, simulate);
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            return 0;
        }

        @Override
        public int getEnergyStored() {
            return this.outputWrapper.getEnergyStored();
        }

        @Override
        public int getMaxEnergyStored() {
            return this.outputWrapper.getMaxEnergyStored();
        }

        @Override
        public boolean canExtract() {
            return false;
        }

        @Override
        public boolean canReceive() {
            return true;
        }

        public LazyOptional<InputWrapper> getHandler() {
            return wrapper;
        }

        protected void onContentsChanged() {
            this.mainHandler.onContentsChanged();
        }



        @Override
        public long getLargeEnergyStored() {
            return this.outputWrapper.getLargeEnergyStored();
        }

        @Override
        public long getLargeMaxEnergyStored() {
            return this.outputWrapper.getLargeMaxEnergyStored();
        }

        @Override
        public long receiveEnergy(long maxReceive, boolean simulate) {
            return outputWrapper.internalReceiveEnergy(maxReceive, simulate);
        }

        @Override
        public long extractEnergy(long maxExtract, boolean simulate) {
            return 0;
        }
    }

    public static class OutputWrapper extends ForgeEnergyStorage {

        // Handler for output wrapper
        private LazyOptional<OutputWrapper> wrapper;

        private SidedEnergyHandler mainHandler;

        public OutputWrapper(SidedEnergyHandler main, long capacity, long maxReceive, long maxExtract) {
            super(capacity, maxReceive, maxExtract);
            this.mainHandler = main;
            createHandler();
        }

        public void setExtract(long amount) {
            this.maxExtract = amount;
        }

        public void setReceive(long amount) {
            this.maxReceive = amount;
        }

        public void createHandler() {
            this.wrapper = LazyOptional.of(() -> this);
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            return 0;
        }

        private int internalReceiveEnergy(int maxReceive, boolean simulate) {
            return super.receiveEnergy(maxReceive, simulate);
        }

        private long internalReceiveEnergy(long maxReceive, boolean simulate) {
            return super.receiveEnergy(maxReceive, simulate);
        }

        public LazyOptional<OutputWrapper> getHandler() {
            return wrapper;
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putLong("capacity", this.capacity);
            tag.putLong("extract", this.maxExtract);
            tag.putLong("receive", this.maxReceive);
            tag.putLong("energy", this.energy);
            return tag;
        }

        public void read(CompoundTag tag) {
            this.energy = tag.getLong("energy");
            this.capacity = tag.getLong("capacity");
            this.maxExtract = tag.getLong("extract");
            this.maxReceive = tag.getLong("receive");
        }

        protected void onContentsChanged() {
            this.mainHandler.onContentsChanged();
        }

        public void setCapacity(long amount) {
            this.capacity = amount;
        }

        public void addCapacity(long transfer) {
            this.capacity += transfer;
        }


    }

}
