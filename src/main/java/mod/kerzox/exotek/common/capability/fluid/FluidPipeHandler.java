package mod.kerzox.exotek.common.capability.fluid;

import mod.kerzox.exotek.common.blockentities.transport.PipeNetwork;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeNetwork;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

public class FluidPipeHandler implements IFluidHandler {

    private FluidPipeEntity entity;
    private LazyOptional<IFluidHandler> handler = LazyOptional.of(() -> this);

    public FluidPipeHandler(FluidPipeEntity entity) {
        this.entity = entity;
    }

    public FluidPipeEntity getEntity() {
        return entity;
    }

    public LazyOptional<IFluidHandler> getHandler() {
        return handler;
    }

    public PipeNetworkFluidInventory getFluidInventory() {
        return entity.getNetwork().getInventory();
    }

    @Override
    public int getTanks() {
        return this.entity.getNetwork().getInventory().getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (this.entity.getNetwork().getInventory() == null) return FluidStack.EMPTY;
        return this.entity.getNetwork().getInventory().getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        if (this.entity.getNetwork().getInventory() == null) return 0;
        return this.entity.getNetwork().getInventory().getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if (this.entity.getNetwork().getInventory() == null) return false;
        return this.entity.getNetwork().getInventory().isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (this.entity.getNetwork().getInventory() == null) return 0;
        return this.entity.getNetwork().getInventory().fill(entity, resource, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (this.entity.getNetwork().getInventory() == null) return FluidStack.EMPTY;
        return this.entity.getNetwork().getInventory().drain(entity, resource, action);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (this.entity.getNetwork().getInventory() == null) return FluidStack.EMPTY;
        return this.entity.getNetwork().getInventory().drain(entity, maxDrain, action);
    }
}
