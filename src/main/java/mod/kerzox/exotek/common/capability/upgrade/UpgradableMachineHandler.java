package mod.kerzox.exotek.common.capability.upgrade;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UpgradableMachineHandler implements IUpgradableMachine {

    public static final int TOTAL_UPGRADES = 4;

    private LazyOptional<UpgradableMachineHandler> handlerLazyOptional = LazyOptional.of(() -> this);

    // index 0, 1 is displayed and allows for input and output respectively
    // indexes 2 - 5 are for internal holding of upgrades
    private ItemStackHandler internalInventory = new ItemStackHandler(1 + 1 + TOTAL_UPGRADES) {

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (stack.isEmpty())
                return ItemStack.EMPTY;

            if (!isItemValid(slot, stack))
                return stack;

            validateSlotIndex(slot);

            ItemStack existing = this.stacks.get(slot);

            int limit = getStackLimit(slot, stack);

            if (!existing.isEmpty()) {
                if (!ItemHandlerHelper.canItemStacksStack(stack, existing))
                    return stack;

                limit -= existing.getCount();
            }

            if (limit <= 0)
                return stack;

            boolean reachedLimit = stack.getCount() > limit;

            if (!simulate) {
                if (existing.isEmpty()) {
                    this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }
                onContentsChanged(slot);
                onInsertion(slot, stack);
            }

            return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
        }

        @Override
        public void setStackInSlot(int slot, @NotNull ItemStack stack) {
            super.setStackInSlot(slot, stack);
            for (int i = 0; i < stack.getCount(); i++) {
                onInsertion(slot, stack);
            }
        }

        private void onInsertion(int slot, ItemStack stack) {
            if (slot == 0) { // we inserted
                for (int i = 2; i < getSlots(); i++) {
                    ItemStack ret = this.insertItem(i, extractItem(0, 1, true), true);
                    if (ret.isEmpty()) {
                        this.insertItem(i, extractItem(0, 1, false), false);
                        break;
                    }
                    if (getStackInSlot(i).getItem().equals(stack.getItem())) {
                        break;
                    }
                }
            }
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            return super.extractItem(slot, amount, simulate);
        }

        @Override
        protected void onContentsChanged(int slot) {
            if (slot != 0 && slot != 1 && getStackInSlot(slot).isEmpty()) {
                for (int i = slot + 1; i < this.getSlots(); i++) {
                    if (!getStackInSlot(i).isEmpty()) {
                        this.setStackInSlot(i - 1, this.getStackInSlot(i));
                        this.setStackInSlot(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    };

    public UpgradableMachineHandler() {

    }

    public <T> LazyOptional<T> getHandler() {
        return handlerLazyOptional.cast();
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("items", internalInventory.serializeNBT());
        return tag;
    }

    public void deserializeNBT(CompoundTag nbt) {
        internalInventory.deserializeNBT(nbt.getCompound("items"));
    }

    @Override
    public ItemStackHandler getInventory() {
        return internalInventory;
    }
}
