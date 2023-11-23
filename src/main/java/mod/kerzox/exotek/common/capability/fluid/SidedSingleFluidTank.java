package mod.kerzox.exotek.common.capability.fluid;

import mod.kerzox.exotek.common.capability.ICapabilitySerializer;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Predicate;

public class SidedSingleFluidTank implements IStrictInventory, IFluidTank, IFluidHandler, ICapabilitySerializer {

    private HashSet<Direction> input = new HashSet<>();
    private HashSet<Direction> output = new HashSet<>();

    private final LazyOptional<SidedSingleFluidTank> handler = LazyOptional.of(() -> this);
    private final TankWrapper internal;
    private final CombinedWrapper combined;

    public SidedSingleFluidTank(int capacity) {
        internal = new TankWrapper(this, capacity);
        combined = new CombinedWrapper(internal);
        this.input.addAll(Arrays.asList(Direction.values()));
    }

    public <T> LazyOptional<T> getHandler(Direction side) {
        if (getInputs().contains(side) && getOutputs().contains(side) || side == null) return combined.getHandler().cast();
        else if (getInputs().contains(side)) return this.handler.cast();
        else if (getOutputs().contains(side)) return internal.getHandler();
        return LazyOptional.empty();
    }

    public boolean isEmpty() {
        return this.internal.isEmpty();
    }

    @Override
    public HashSet<Direction> getInputs() {
        return input;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return output;
    }

    @Override
    public @NotNull FluidStack getFluid() {
        return internal.getFluid();
    }

    @Override
    public int getFluidAmount() {
        return internal.getFluidAmount();
    }

    @Override
    public int getCapacity() {
        return internal.getCapacity();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return internal.isFluidValid(stack);
    }

    @Override
    public int getTanks() {
        return internal.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        return internal.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return internal.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return internal.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
        return internal.forceFill(resource, action);
    }

    public @NotNull FluidStack forceDrain(int maxDrain, IFluidHandler.FluidAction action) {
        return this.internal.drain(maxDrain, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
        return resource;
    }

    protected void onContentsChanged(IFluidHandler tankWrapper) {

    }

    @Override
    public CompoundTag serialize() {
        return serializeNBT();
    }

    @Override
    public void deserialize(CompoundTag tag) {
        deserializeNBT(tag);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("strict", serializeInputAndOutput());
        return this.internal.writeToNBT(tag);
    }

    public void deserializeNBT(CompoundTag fluidHandler) {
        deserializeInputAndOutput(fluidHandler.getCompound("strict"));
        this.internal.readFromNBT(fluidHandler);
    }

    public void invalidate() {
        this.handler.invalidate();
        this.combined.getHandler().invalidate();
        this.internal.getHandler().invalidate();
    }

    public void setFluidInTank(FluidStack stack) {
        this.internal.setFluid(stack);
    }

    public static class CombinedWrapper implements IFluidHandler, IFluidTank {

        private TankWrapper internal;
        private LazyOptional<CombinedWrapper> lazyOptional = LazyOptional.of(() -> this);

        public CombinedWrapper(TankWrapper internal) {
            this.internal = internal;
        }

        public LazyOptional<CombinedWrapper> getHandler() {
            return lazyOptional;
        }

        @Override
        public @NotNull FluidStack getFluid() {
            return internal.getFluid();
        }

        @Override
        public int getFluidAmount() {
            return internal.getFluidAmount();
        }

        @Override
        public int getCapacity() {
            return internal.getCapacity();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return internal.isFluidValid(stack);
        }

        @Override
        public int getTanks() {
            return internal.getTanks();
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            return internal.getFluidInTank(tank);
        }

        @Override
        public int getTankCapacity(int tank) {
            return internal.getTankCapacity(tank);
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            return internal.isFluidValid(tank, stack);
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return internal.forceFill(resource, action);
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            return internal.drain(maxDrain, action);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            return internal.drain(resource, action);
        }
    }

    public static class TankWrapper extends FluidTank {

        private LazyOptional<SidedSingleFluidTank.TankWrapper> handler = LazyOptional.of(() -> this);
        private SidedSingleFluidTank main;

        public TankWrapper(SidedSingleFluidTank main, int cap) {
            super(cap);
            this.main = main;
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            return 0;
        }

        public int forceFill(FluidStack resource, IFluidHandler.FluidAction action) {
            return super.fill(resource, action);
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }

        @Override
        protected void onContentsChanged() {
            main.onContentsChanged(this);
        }

    }
}
