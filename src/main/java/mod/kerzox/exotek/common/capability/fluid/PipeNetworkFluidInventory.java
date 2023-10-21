package mod.kerzox.exotek.common.capability.fluid;

import mod.kerzox.exotek.common.blockentities.transport.GraphUtil;
import mod.kerzox.exotek.common.blockentities.transport.IPipe;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeNetwork;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class PipeNetworkFluidInventory extends FluidStorageTank {

    /*
        TODO
         - Change capacity from size of network
         - Take tiers into account during the capacity calculation

     */

    private FluidPipeNetwork network;

//    private List<IFluidHandler> outputs = new ArrayList<>();
    private FluidPipeEntity focused;

    public PipeNetworkFluidInventory(FluidPipeNetwork network, int capacity) {
        super(capacity);
        this.network = network;
    }

    public void changeCapacity(int amount) {
        this.capacity = amount;
    }

    @Override
    public FluidTank readFromNBT(CompoundTag nbt) {
        return super.readFromNBT(nbt);
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag nbt) {
        return super.writeToNBT(nbt);
    }

    // on fill action we have to traverse the network and any consumers we can reach we fill them
    public int fill(FluidPipeEntity fluidPipeEntity, FluidStack resource, FluidAction action) {
        focused = fluidPipeEntity;
//
//        AtomicInteger currentResourceAmount = new AtomicInteger(Math.min(resource.getAmount(), fluidPipeEntity.getTier().getTransfer()));
//        if (this.network.getCachedTraversed().get(fluidPipeEntity) == null) return 0;
//
//        IFluidHandler smallest = this.network.getSmallestHandlerAmount(fluidPipeEntity);
//
//        if (smallest != null) {
//            return smallest.fill(new FluidStack(resource.getFluid(), currentResourceAmount.get()),
//                    action);
//        }
        return fill(new FluidStack(resource.getFluid(), Math.min(resource.getAmount(), fluidPipeEntity.getTier().getTransfer())), action);
    }

    public FluidPipeEntity getFocused() {
        return focused;
    }

    public @NotNull FluidStack drain(FluidPipeEntity fluidPipeEntity, FluidStack resource, FluidAction action) {
        return super.drain(resource, action);
    }

    public @NotNull FluidStack drain(FluidPipeEntity fluidPipeEntity, int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        return super.drain(maxDrain, action);
    }
}
