package mod.kerzox.exotek.common.capability.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class CombinedFluidInventory implements IFluidHandler {

    protected final IFluidHandler[] fluidHandler; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedFluidInventory(IFluidHandler... itemHandler)
    {
        this.fluidHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++)
        {
            index += itemHandler[i].getTanks();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }

    @Override
    public int getTanks() {
        return slotCount;
    }

    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected IFluidHandler getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= fluidHandler.length)
        {
            return null;
        }
        return fluidHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        int index = getIndexForSlot(tank);
        IFluidHandler handler = getHandlerFromIndex(index);
        tank = getSlotFromIndex(tank, index);
        return handler.isFluidValid(tank, stack);
    }

    public IFluidHandler getHandlerFromSlot(int slot) {
        int index = getIndexForSlot(slot);
        return getHandlerFromIndex(index);
    }

    protected boolean canFillTank(int tank) {
        return true;
    }

    protected boolean canDrainTank(int tank) {
        return true;
    }


    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        int ret = 0;
        for (int i = 0; i < slotCount; i++) {
            ret = getHandlerFromSlot(i).fill(resource, action);
            if (ret != 0) {
                return ret;
            }
        }
        return ret;
    }

    @NotNull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        FluidStack ret = FluidStack.EMPTY;
        for (int i = 0; i < slotCount; i++) {
            ret = getHandlerFromSlot(i).drain(resource, action);
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        return ret;
    }

    private void onContentsChanged() {

    }

    @NotNull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        int toDrain = maxDrain;
        FluidStack ret = FluidStack.EMPTY;
        for (int i = 0; i < slotCount; i++) {

            if (getFluidInTank(i).getAmount() < maxDrain) {
                toDrain = getFluidInTank(i).getAmount();
            }

            FluidStack resource = new FluidStack(getFluidInTank(i), toDrain);

            ret = getHandlerFromSlot(i).drain(resource, action);
            if (!ret.isEmpty()) {
                return ret;
            }
        }
        return ret;
    }



}
