package mod.kerzox.exotek.common.capability.item;

import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import mod.kerzox.exotek.common.capability.CapabilityHolder;
import mod.kerzox.exotek.common.capability.ICapabilitySerializer;
import mod.kerzox.exotek.common.capability.IStrictCombinedItemHandler;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;

public class SidedItemStackHandler implements IStrictCombinedItemHandler, ICapabilitySerializer, CapabilityHolder<IItemHandlerModifiable> {

    private int slots;

    private HashSet<Direction> input = new HashSet<>();
    private HashSet<Direction> output = new HashSet<>();
    private final LazyOptional<SidedItemStackHandler> handlerLazyOptional = LazyOptional.of(() -> this);

    private IOTypes currentSetting = IOTypes.DEFAULT;

    // output handler
    private InternalWrapper outputWrapper;
    private final LazyOptional<InternalWrapper> outputHandler = LazyOptional.of(() -> outputWrapper);

    // Combined
    private IStrictCombinedItemHandler combinedWrapper;
    private final LazyOptional<IStrictCombinedItemHandler> combinedHandler = LazyOptional.of(() -> combinedWrapper);

    public SidedItemStackHandler(int slots) {
        this.slots = slots;
        this.outputWrapper = createInternalWrapper(slots);
        this.combinedWrapper = createCombinedWrapper();
        addSides();
    }

    private InternalWrapper createInternalWrapper(int slots) {
        return new InternalWrapper(this, slots);
    }

    private IStrictCombinedItemHandler createCombinedWrapper() {
        return new IStrictCombinedItemHandler() {
            @Override
            public IItemHandlerModifiable getInputHandler() {
                return SidedItemStackHandler.this.getInputHandler();
            }

            @Override
            public IItemHandlerModifiable getOutputHandler() {
                return SidedItemStackHandler.this.getOutputHandler();
            }

            @Override
            public IOTypes getIOSetting() {
                return currentSetting;
            }

            @Override
            public void setIOSetting(IOTypes types) {
                currentSetting = types;
            }

            @Override
            public HashSet<Direction> getInputs() {
                return SidedItemStackHandler.this.getInputs();
            }

            @Override
            public HashSet<Direction> getOutputs() {
                return SidedItemStackHandler.this.getOutputs();
            }

            @Override
            public int getSlots() {
                return SidedItemStackHandler.this.getSlots();
            }

            @Override
            public @NotNull ItemStack getStackInSlot(int slot) {
                return SidedItemStackHandler.this.getStackInSlot(slot);
            }

            @Override
            public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                return outputWrapper.internalInsertItem(slot, stack, simulate);
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                return outputWrapper.extractItem(slot, amount, simulate);
            }

            @Override
            public int getSlotLimit(int slot) {
                return SidedItemStackHandler.this.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return SidedItemStackHandler.this.isItemValid(slot, stack);
            }

            @Override
            public void setStackInSlot(int slot, @NotNull ItemStack stack) {
                SidedItemStackHandler.this.setStackInSlot(slot, stack);
            }
        };
    }

    public void resize(int slots) {

        NonNullList<ItemStack> stacks = NonNullList.withSize(slots, ItemStack.EMPTY);
        for (int i = 0; i < outputWrapper.getStacks().size(); i++) {
            stacks.set(i, outputWrapper.getStackInSlot(0));
        }
        outputWrapper.setItems(stacks);
    }

    public LazyOptional<SidedItemStackHandler> getHandler() {
        return handlerLazyOptional;
    }

    public LazyOptional<InternalWrapper> getOutputLazyHandler() {
        return outputHandler;
    }

    public LazyOptional<IStrictCombinedItemHandler> getCombinedHandler() {
        return combinedHandler;
    }

    protected void addSides() {
    }

    @Override
    public IItemHandlerModifiable getInputHandler() {
        return this;
    }

    @Override
    public IItemHandlerModifiable getOutputHandler() {
        return outputWrapper;
    }

    @Override
    public void invalidate() {
        this.getHandler().invalidate();
        this.getOutputLazyHandler().invalidate();
        this.combinedHandler.invalidate();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("items", this.outputWrapper.serializeNBT());
        tag.put("strict", serializeInputAndOutput());
        tag.put("io", serializeIO());
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        this.outputWrapper.deserializeNBT(nbt.getCompound("items"));
        deserializeInputAndOutput(nbt.getCompound("strict"));
        deserializeIO(nbt.getCompound("io"));
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        this.outputWrapper.setStackInSlot(slot, stack);
    }

    @Override
    public int getSlots() {
        return this.slots;
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        return this.outputWrapper.getStackInSlot(slot);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return this.outputWrapper.internalInsertItem(slot, stack, simulate);
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    public @NotNull ItemStack internalExtractItem(int slot, int amount, boolean simulate) {
        return this.outputWrapper.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }


    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (input.contains(side) && output.contains(side) || side == null) return combinedHandler.cast();
        else if (input.contains(side)) return handlerLazyOptional.cast();
        else if (output.contains(side)) return outputHandler.cast();
        else return LazyOptional.empty();
    }

    public SidedItemStackHandler addInputs(Direction... directions) {
        this.input.addAll(Arrays.asList(directions));
        return this;
    }

    public SidedItemStackHandler addOutputs(Direction... directions) {
        this.output.addAll(Arrays.asList(directions));
        return this;
    }

    @Override
    public HashSet<Direction> getInputs() {
        return this.input;
    }

    @Override
    public HashSet<Direction> getOutputs() {
        return this.output;
    }

    @Override
    public IItemHandlerModifiable getInstance() {
        return this;
    }

    @Override
    public Capability<?> getType() {
        return ForgeCapabilities.ITEM_HANDLER;
    }

    @Override
    public LazyOptional<IItemHandlerModifiable> getCapabilityHandler(Direction direction) {
        return (LazyOptional<IItemHandlerModifiable>) getCapability(getType(), direction);
    }

    @Override
    public CompoundTag serialize() {
        return serializeNBT();
    }

    @Override
    public void deserialize(CompoundTag tag) {
        deserializeNBT(tag);
    }

    @Override
    public IOTypes getIOSetting() {
        return currentSetting;
    }

    @Override
    public void setIOSetting(IOTypes types) {
        this.currentSetting = types;
    }

    public static class InternalWrapper extends ItemStackHandler {

        private SidedItemStackHandler owner;

        public InternalWrapper(SidedItemStackHandler owner, int slots) {
            super(slots);
            this.owner = owner;
        }

        public void setItems(NonNullList<ItemStack> stacks) {
            this.stacks = stacks;
        }

        public NonNullList<ItemStack> getStacks() {
            return stacks;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return owner.isItemValid(slot, stack);
        }

        @Override
        public int getSlotLimit(int slot) {
            return owner.getSlotLimit(slot);
        }

        public @NotNull ItemStack internalInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return super.insertItem(slot, stack, simulate);
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }
    }

}
