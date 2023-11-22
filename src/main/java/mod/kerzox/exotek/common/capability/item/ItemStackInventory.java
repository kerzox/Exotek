package mod.kerzox.exotek.common.capability.item;

import mod.kerzox.exotek.common.capability.ICapabilitySerializer;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class ItemStackInventory extends CombinedInvWrapper implements IStrictInventory, INBTSerializable<CompoundTag>, ICapabilitySerializer {

    private HashSet<Direction> input = new HashSet<>();
    private HashSet<Direction> output = new HashSet<>();
    private PlayerWrapper playerWrapper = new PlayerWrapper(this);

    private final LazyOptional<ItemStackInventory> handler = LazyOptional.of(() -> this);

    public ItemStackInventory(int inputSlots, int outputSlots) {
        super(new InputHandler(inputSlots), new OutputHandler(outputSlots));
        getInputHandler().setInventory(this);
        getOutputHandler().setInventory(this);
    }

    public ItemStackInventory(InputHandler inputHandler, OutputHandler outputHandler) {
        super(inputHandler, outputHandler);
    }

    public void invalidate() {
        this.getInputHandler().getHandler().invalidate();
        this.getOutputHandler().getHandler().invalidate();
        this.playerWrapper.getHandler().invalidate();
    }

    public ItemStack getStackFromInputHandler(int slot) {
        return getInputHandler().getStackInSlot(slot);
    }

    public ItemStack getStackFromOutputHandler(int slot) {
        return getOutputHandler().getStackInSlot(slot);
    }

    public <T> LazyOptional<T> getHandler(Direction side) {
        if (side == null) return playerWrapper.getHandler().cast();
        else if (getInputs().contains(side) && getOutputs().contains(side)) return handler.cast();
        else if (getInputs().contains(side)) return getInputHandler().getHandler();
        else if (getOutputs().contains(side)) return getOutputHandler().getHandler();
        return LazyOptional.empty();
    }

    public InputHandler getInputHandler() {
        return (InputHandler) getHandlerFromIndex(0);
    }

    public OutputHandler getOutputHandler() {
        return (OutputHandler) getHandlerFromIndex(1);
    }

    @Override
    public IItemHandlerModifiable getHandlerFromIndex(int index) {
        return super.getHandlerFromIndex(index);
    }

    @Override
    public int getSlotFromIndex(int slot, int index) {
        return super.getSlotFromIndex(slot, index);
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return output;
    }

    @Override
    public HashSet<Direction> getInputs() {
        return input;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.put("input", this.getInputHandler().serializeNBT());
        nbt.put("output", this.getOutputHandler().serializeNBT());
        nbt.put("strict", serializeInputAndOutput());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        if (nbt.contains("input")) {
            getInputHandler().deserializeNBT(nbt.getCompound("input"));
        }
        if (nbt.contains("output")) {
            getOutputHandler().deserializeNBT(nbt.getCompound("output"));
        }

        deserializeInputAndOutput(nbt.getCompound("strict"));
        onLoad();
    }

    /**
     * Serialize to tags with input & output slots set to chosen amount
     * This is for use with the tiered machine system to recreate a new inventory from saved data but with new sizes
     * @param iSlots input slots
     * @param oSlots output slots
     * @return tag
     */
    public CompoundTag serializeWithSpecificSizes(int iSlots, int oSlots) {
        CompoundTag tag = serialize();
        CompoundTag newInput = getInputHandler().serializeNBT();
        newInput.putInt("Size", iSlots);
        tag.put("input", newInput);
        CompoundTag newOutput = getOutputHandler().serializeNBT();
        newOutput.putInt("Size", oSlots);
        tag.put("output", newOutput);
        return tag;
    }

//    public void setInputSize(int slots) {
//        CompoundTag data = this.getInputHandler().serializeNBT();
//        data.putInt("Size", slots);
//        this.getInputHandler().setSize(slots);
//        this.getInputHandler().deserializeNBT(data);
//    }
//
//    public void setOutputSize(int slots) {
//        CompoundTag data = this.getOutputHandler().serializeNBT();
//        data.putInt("Size", slots);
//        this.getOutputHandler().setSize(slots);
//        this.getOutputHandler().deserializeNBT(data);
//    }

    protected void onLoad() {


    }

    protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {

    }

    @Override
    public CompoundTag serialize() {
        return serializeNBT();
    }

    @Override
    public void deserialize(CompoundTag tag) {
        deserializeNBT(tag);
    }

    public static class InputHandler extends ItemStackHandler {

        private LazyOptional<InputHandler> handler = LazyOptional.of(()-> this);
        private ItemStackInventory inventory;

        public InputHandler(int slots) {
            super(slots);
        }

        public @NotNull ItemStack forceExtractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        public void setInventory(ItemStackInventory inventory) {
            this.inventory = inventory;
        }

        // don't allow extraction

        public void setStackInSlotNoContentUpdate(int slot, @NotNull ItemStack stack) {
            validateSlotIndex(slot);
            this.stacks.set(slot, stack);
        }

        public @NotNull ItemStack insertItemNoUpdate(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (!isItemValid(slot, stack))
                return stack;

            validateSlotIndex(slot);

            ItemStack existing = this.stacks.get(slot);

            int limit = getStackLimit(slot, stack);

            if (!existing.isEmpty())
            {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate)
            {
                if (existing.isEmpty())
                {
                    this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                }
                else
                {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
               // onContentsChanged(slot);
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        public <T> LazyOptional<T> getHandler() {
            return handler.cast();
        }

        public int isFull() {
            for (int i = 0; i < getSlots(); i++) {
                if (!isSlotFull(i)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean isSlotFull(int slot) {
            return this.getStackInSlot(slot).getCount() == this.getSlotLimit(0);
        }

        @Override
        protected void onContentsChanged(int slot) {
            inventory.onContentsChanged(this, slot);
        }
    }

    public static class OutputHandler extends ItemStackHandler {

        private LazyOptional<OutputHandler> handler = LazyOptional.of(()-> this);

        private ItemStackInventory inventory;

        public OutputHandler(int slots) {
            super(slots);
        }

        public void setInventory(ItemStackInventory inventory) {
            this.inventory = inventory;
        }

        public @NotNull ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        // don't allow input

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        public <T> LazyOptional <T> getHandler() {
            return handler.cast();
        }

        public int isFull() {
            for (int i = 0; i < getSlots(); i++) {
                if (!isSlotFull(i)) {
                    return i;
                }
            }
            return -1;
        }

        public boolean isSlotFull(int slot) {
            return this.getStackInSlot(slot).getCount() == this.getSlotLimit(0);
        }

        @Override
        protected void onContentsChanged(int slot) {
            inventory.onContentsChanged(this, slot);
        }

    }

    public static class PlayerWrapper implements IItemHandler, IItemHandlerModifiable, IStrictInventory {

        // basically a wrapper over the top of the inventory to allow players to insert and extract from either wrappers without their respective locks

        private ItemStackInventory inventory;
        private LazyOptional<PlayerWrapper> handler = LazyOptional.of(()-> this);

        public PlayerWrapper(ItemStackInventory wrapper) {
            this.inventory = wrapper;
        }

        @Override
        public int getSlots() {
            return inventory.slotCount;
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(slot);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            int index = inventory.getIndexForSlot(slot);
            IItemHandlerModifiable handler = inventory.getHandlerFromIndex(index);
            slot = inventory.getSlotFromIndex(slot, index);
           // if (handler instanceof OutputHandler outputHandler) return outputHandler.forceInsertItem(slot, stack, simulate);
            return handler.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            int index = inventory.getIndexForSlot(slot);
            IItemHandlerModifiable handler = inventory.getHandlerFromIndex(index);
            slot = inventory.getSlotFromIndex(slot, index);
            if (handler instanceof InputHandler inputHandler) return inputHandler.forceExtractItem(slot, amount, simulate);
            return handler.extractItem(slot, amount, simulate);
        }

        public IItemHandlerModifiable getHandlerFromIndex(int slotIndex) {
            int index = inventory.getIndexForSlot(slotIndex);
            return inventory.getHandlerFromIndex(index);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(slot);
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return inventory.isItemValid(slot, stack);
        }

        public LazyOptional<PlayerWrapper> getHandler() {
            return handler;
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            inventory.setStackInSlot(slot, stack);
        }


        protected void onContentsChanged(int slot) {
            this.inventory.onContentsChanged(this, slot);
        }

        @Override
        public HashSet<Direction> getInputs() {
            return this.inventory.getInputs();
        }

        @Override
        public HashSet<Direction> getOutputs() {
            return this.inventory.getOutputs();
        }
    }



}
