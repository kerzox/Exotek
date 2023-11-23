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

    public SidedEnergyHandler(int capacity, int maxReceive, int maxExtract) {
        outputWrapper = new OutputWrapper(this, capacity, maxReceive, maxExtract);
        inputWrapper = new InputWrapper(this, outputWrapper);
        combinedWrapper = new CombinedWrapper(this, outputWrapper);
    }

    public SidedEnergyHandler(int capacity, int maxExtract, Direction... outputDirections) {
        this(capacity, 0, maxExtract);
        removeInputs(Direction.values());
        addOutput(outputDirections);
    }

    public SidedEnergyHandler(int capacity) {
        this(capacity, capacity, capacity);
        addInput(Direction.values());
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

    public void addEnergy(int amount) {
        this.outputWrapper.internalAddEnergy(amount);
    }

    public void consumeEnergy(int amount) {
        this.outputWrapper.internalRemoveEnergy(amount);
    }

    public int getEnergy() {
        return this.outputWrapper.getEnergyStored();
    }

    public int getMaxEnergy() {
        return this.outputWrapper.getMaxEnergyStored();
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

    public void setCapacity(int transfer) {
        this.outputWrapper.setCapacity(transfer);
    }

    public void addCapacity(int transfer) {
        this.outputWrapper.addCapacity(transfer);
    }

    public void setExtract(int amount) {
        this.outputWrapper.setExtract(amount);
    }

    public void setReceive(int amount) {
        this.outputWrapper.setReceive(amount);
    }

    @Override
    public Capability<?> getType() {
        return ForgeCapabilities.ENERGY;
    }

    @Override
    public LazyOptional<SidedEnergyHandler> getCapabilityHandler(Direction direction) {
        return getHandler(direction);
    }

    public static class CombinedWrapper implements IEnergyStorage, IStrictInventory, ICapabilitySerializer {

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
            return false;
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




    }

    public static class InputWrapper implements IEnergyStorage {

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



    }

    public static class OutputWrapper extends ForgeEnergyStorage {

        // Handler for output wrapper
        private LazyOptional<OutputWrapper> wrapper;

        private SidedEnergyHandler mainHandler;

        public OutputWrapper(SidedEnergyHandler main, int capacity, int maxReceive, int maxExtract) {
            super(capacity, maxReceive, maxExtract);
            this.mainHandler = main;
            createHandler();
        }

        public void setExtract(int amount) {
            this.maxExtract = amount;
        }

        public void setReceive(int amount) {
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

        public LazyOptional<OutputWrapper> getHandler() {
            return wrapper;
        }

        public CompoundTag serialize() {
            CompoundTag tag = new CompoundTag();
            tag.putInt("capacity", this.capacity);
            tag.putInt("extract", this.maxExtract);
            tag.putInt("receive", this.maxReceive);
            tag.putInt("energy", this.energy);
            return tag;
        }

        public void read(CompoundTag tag) {
            this.energy = tag.getInt("energy");
            this.capacity = tag.getInt("capacity");
            this.maxExtract = tag.getInt("extract");
            this.maxReceive = tag.getInt("receive");
        }

        protected void onContentsChanged() {
            this.mainHandler.onContentsChanged();
        }

        public void setCapacity(int amount) {
            this.capacity = amount;
        }

        public void addCapacity(int transfer) {
            this.capacity += transfer;
        }
    }

}
