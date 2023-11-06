package mod.kerzox.exotek.common.capability.item;

import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

public class ConveyorBeltInventoryHandler2 implements IItemHandler, IItemHandlerModifiable, INBTSerializable<CompoundTag> {

    protected ConveyorBeltItemStack[] stacks;
    private ConveyorBeltEntity conveyorBeltEntity;

    public ConveyorBeltInventoryHandler2(ConveyorBeltEntity entity, int size) {
        this.conveyorBeltEntity = entity;
        stacks = new ConveyorBeltItemStack[size];
    }

    public Stream<ConveyorBeltItemStack> getStacks() {
        return Arrays.stream(stacks).filter(Objects::nonNull);
    }

    public ConveyorBeltEntity getConveyorBelt() {
        return conveyorBeltEntity;
    }

    public void setSize(int size) {
        stacks = new ConveyorBeltItemStack[size];
    }

    public ConveyorBeltItemStack createEntity(ItemStack stack) {

        float x = conveyorBeltEntity.getBlockPos().getX() + 0.5f;
        float y = conveyorBeltEntity.getBlockPos().getY() + 0.5f;
        float z =  conveyorBeltEntity.getBlockPos().getZ() + 0.5f;

        ConveyorBeltItemStack conveyorBeltItemStack = new ConveyorBeltItemStack(
                conveyorBeltEntity.getLevel(),
                conveyorBeltEntity.getBeltDirection(),
                x, y, z,
                stack);

//        if (!getConveyorBelt().isStopped()) {
//            conveyorBeltItemStack.setDeltaMovement(x - 1);
//        }

        setDeltaMovementFromConveyorBeltSpeed(conveyorBeltItemStack);
        getConveyorBelt().getLevel().addFreshEntity(conveyorBeltItemStack);
        return conveyorBeltItemStack;
    }

    public void setDeltaMovementFromConveyorBeltSpeed(Entity entity) {
        switch (getConveyorBelt().getBeltDirection()) {
            case SOUTH -> entity.setDeltaMovement(0, 0, getConveyorBelt().getSpeed() * (2 / 16f));
            case NORTH -> entity.setDeltaMovement(0, 0, -getConveyorBelt().getSpeed() * (2 / 16f));
            case WEST ->  entity.setDeltaMovement(-getConveyorBelt().getSpeed() * (2 / 16f), 0, 0);
            case EAST ->  entity.setDeltaMovement(getConveyorBelt().getSpeed() * (2 / 16f), 0, 0);
        }
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        validateSlotIndex(slot);
        this.stacks[slot] = createEntity(stack);
        onContentsChanged(slot);
    }

    @Override
    public int getSlots() {
        return stacks.length;
    }

    @Override
    @NotNull
    public ItemStack getStackInSlot(int slot) {
        validateSlotIndex(slot);
        if (stacks[slot] == null) return ItemStack.EMPTY;
        return this.stacks[slot].getTransportedStack();
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);
        ItemStack existing = this.stacks[slot] == null ? ItemStack.EMPTY : this.stacks[slot].getTransportedStack();

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
                this.stacks[slot] = reachedLimit ?
                        createEntity(ItemHandlerHelper.copyStackWithSize(stack, limit)) :
                        createEntity(stack);
            } else {
                existing.grow(reachedLimit ? limit : stack.getCount());
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount() - limit) : ItemStack.EMPTY;
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        if (this.stacks[slot] == null) return ItemStack.EMPTY;
        ItemStack existing = this.stacks[slot].getTransportedStack();

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract) {
            if (!simulate) {
                this.stacks[slot] = createEntity(ItemStack.EMPTY);
                onContentsChanged(slot);
                return existing;
            } else {
                return existing.copy();
            }
        } else {
            if (!simulate) {
                this.stacks[slot] = createEntity(ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 64;
    }

    protected int getStackLimit(int slot, @NotNull ItemStack stack) {
        return Math.min(getSlotLimit(slot), stack.getMaxStackSize());
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public CompoundTag serializeNBT() {
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < stacks.length; i++) {
            if (stacks[i] == null) continue;
            if (!stacks[i].getTransportedStack().isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                stacks[i].save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        nbt.put("Items", nbtTagList);
        nbt.putInt("Size", stacks.length);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setSize(nbt.contains("Size", Tag.TAG_INT) ? nbt.getInt("Size") : stacks.length);
        ListTag tagList = nbt.getList("Items", Tag.TAG_COMPOUND);
        for (int i = 0; i < tagList.size(); i++) {
            CompoundTag itemTags = tagList.getCompound(i);
            int slot = itemTags.getInt("Slot");

            if (slot >= 0 && slot < stacks.length) {
                stacks[slot] = createEntity(ItemStack.of(itemTags));
            }
        }
        onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= stacks.length)
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + stacks.length + ")");
    }

    protected void onLoad() {

    }

    protected void onContentsChanged(int slot) {
        System.out.println("Changed");
    }

    public ConveyorBeltItemStack getCStackInSlot(int slot) {
        return this.stacks[slot];
    }
}