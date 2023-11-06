package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ConveyorBeltInventory extends ItemStackHandler {

    private final IConveyorBelt belt;
    private NonNullList<ConveyorBeltItemStack> entityStacks;

    private CompoundTag tag = new CompoundTag();

    public ConveyorBeltInventory(IConveyorBelt belt, int size) {
        super(size);
        this.belt = belt;
        entityStacks = NonNullList.withSize(size, createEntity(ItemStack.EMPTY, false));
    }

    private Level getLevel() {
        return belt.getBelt().getLevel();
    }

    private Direction getBeltDirection() {
        return belt.getBelt().getBeltDirection();
    }

    public ConveyorBeltItemStack createEntityAt(ItemStack stack, float x, float y, float z, boolean addToWorld) {

        ConveyorBeltItemStack conveyorBeltItemStack = new ConveyorBeltItemStack(
                getLevel(),
                getBeltDirection(),
                x, y, z,
                stack.copy());

        if (addToWorld) getLevel().addFreshEntity(conveyorBeltItemStack);
        return conveyorBeltItemStack;
    }

    public ConveyorBeltItemStack createEntity(ItemStack stack, boolean addToWorld) {

        BlockPos pos = belt.getBelt().getBlockPos();

        double x = pos.getX() + 0.5f;
        double y = pos.getY() + 0.5f;
        double z = pos.getZ() + 0.5f;

        double itemEntitySize = (4/16d);

        if (getBeltDirection() == Direction.NORTH) {
            z = z + (5/16f);
        }

        if (getBeltDirection() == Direction.SOUTH) {
            z = z - (5/16f);
        }

        if (getBeltDirection() == Direction.EAST) {
            x = x - 5/16f;
        }

        if (getBeltDirection() == Direction.WEST) {
            x = x + 5/16f;
        }

        ConveyorBeltItemStack conveyorBeltItemStack = new ConveyorBeltItemStack(
                getLevel(),
                getBeltDirection(),
                x, y, z,
                stack.copy());

        if (addToWorld) {
            System.out.println("new entity");
            getLevel().addFreshEntity(conveyorBeltItemStack);
        }
        conveyorBeltItemStack.setBlockPosCollision(belt.getBelt().getBlockPos());
        conveyorBeltItemStack.setDirection(this.getBeltDirection());
        conveyorBeltItemStack.setDirection2(this.getBeltDirection());
        return conveyorBeltItemStack;
    }

    public ConveyorBeltItemStack getConveyorEntityStackAtSlot(int slot) {
        return this.entityStacks.get(slot);
    }

    public boolean conveyorBeltInsert(int slot, ConveyorBeltItemStack itemStack) {
      //  if (belt.getBelt().isCurrentlyMovingItem()) return false;
        if (itemStack == null) return false;
        ItemStack ret = super.insertItem(slot, itemStack.getTransportedStack().copy(), true);
        if (ret.isEmpty()) {
            super.insertItem(slot, itemStack.getTransportedStack().copy(), false);
            this.entityStacks.set(slot, itemStack);
            itemStack.setBlockPosCollision(belt.getBelt().getBlockPos());
            itemStack.setDirection(this.getBeltDirection());
        }

        return true;
    }

    public ConveyorBeltItemStack conveyorBeltExtract(int slot) {

        ItemStack ret = super.extractItem(slot, this.entityStacks.get(slot).getTransportedStack().getCount(), true);
        if (!ret.isEmpty()) {
            super.extractItem(slot, this.entityStacks.get(slot).getTransportedStack().getCount(), false);
            ConveyorBeltItemStack retStack = this.entityStacks.get(slot);
            this.entityStacks.set(slot, createEntity(ItemStack.EMPTY, false));
         //   this.belt.getBelt().finishMovingItem();
            return retStack;
        }

        return null;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {

        //if (belt.getBelt().isCurrentlyMovingItem()) return stack;

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

                // if this entity stack is not empty then we should just edit the internal itemstack.
                ConveyorBeltItemStack conveyorBeltItemStack = this.entityStacks.get(slot);
                conveyorBeltItemStack.setBlockPosCollision(belt.getBelt().getBlockPos());
                conveyorBeltItemStack.setDirection(this.getBeltDirection());
                if (!conveyorBeltItemStack.isEmpty()) {
                    conveyorBeltItemStack.setItem(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack);
                }
                else this.entityStacks.set(slot, createEntity(reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, limit) : stack, true));
            }
            else
            {
                existing.grow(reachedLimit ? limit : stack.getCount());
                this.entityStacks.get(slot).setItem(existing);
            }
            onContentsChanged(slot);
        }

        return reachedLimit ? ItemHandlerHelper.copyStackWithSize(stack, stack.getCount()- limit) : ItemStack.EMPTY;
    }

    public void setEntityStackAtSlot(int slot, ConveyorBeltItemStack stack) {
        this.entityStacks.set(slot, stack);
    }

    public void setEntityStacks(NonNullList<ConveyorBeltItemStack> entityStacks) {
        this.entityStacks = entityStacks;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = super.serializeNBT();
        ListTag nbtTagList = new ListTag();
        for (int i = 0; i < entityStacks.size(); i++)
        {
            if (!entityStacks.get(i).isEmpty())
            {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                entityStacks.get(i).save(itemTag);
                nbtTagList.add(itemTag);
            }
        }
        CompoundTag nbt = new CompoundTag();
        CompoundTag nbt2 = new CompoundTag();
        nbt.put("entityItems", nbtTagList);
        nbt.putInt("entityItemsSize", stacks.size());
        nbt2.put("stacks", tag);
        nbt2.put("entity", nbt);
        return nbt2;
    }

    public void setEntitySize(int size)
    {
        entityStacks = NonNullList.withSize(size, createEntity(ItemStack.EMPTY, false));
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        tag = nbt.getCompound("entity");
        super.deserializeNBT(nbt.getCompound("stacks"));
    }

    public CompoundTag getTag() {
        return tag;
    }

    @Override
    protected void onContentsChanged(int slot) {
        belt.getBelt().syncBlockEntity();
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        if (amount == 0)
            return ItemStack.EMPTY;

        validateSlotIndex(slot);

        ItemStack existing = this.stacks.get(slot);

        if (existing.isEmpty())
            return ItemStack.EMPTY;

        int toExtract = Math.min(amount, existing.getMaxStackSize());

        if (existing.getCount() <= toExtract)
        {
            if (!simulate)
            {
                this.stacks.set(slot, ItemStack.EMPTY);

                this.entityStacks.get(slot).discard();
                // fake entity;
                //this.entityStacks.set(slot, createEntity(ItemStack.EMPTY, false));
               // this.belt.getBelt().finishMovingItem();
                onContentsChanged(slot);
                return existing;
            }
            else
            {
                return existing.copy();
            }
        }
        else
        {
            if (!simulate)
            {
                this.stacks.set(slot, ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                this.entityStacks.get(slot).setItem(ItemHandlerHelper.copyStackWithSize(existing, existing.getCount() - toExtract));
                onContentsChanged(slot);
            }

            return ItemHandlerHelper.copyStackWithSize(existing, toExtract);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
