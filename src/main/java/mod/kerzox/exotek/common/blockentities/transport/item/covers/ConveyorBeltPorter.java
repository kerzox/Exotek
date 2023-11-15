package mod.kerzox.exotek.common.blockentities.transport.item.covers;

import mod.kerzox.exotek.client.render.transfer.AbstractConveyorCoverRenderer;
import mod.kerzox.exotek.client.render.transfer.ConveyorBeltPorterRenderer;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.capability.item.ItemStackHandlerUtils;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.item.ConveyorBeltCoverItem;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// extraction + insertion
public class ConveyorBeltPorter extends AbstractConveyorCover {

    private static int INPUT_SLOT = 0;
    private static int OUTPUT_SLOT = 0;

    private int movementLimit = 1;
    private boolean insertion;
    private ItemStackInventory inventory = new ItemStackInventory(1, 1);

    public ConveyorBeltPorter() {

    }

    @Override
    public boolean isValid(Direction value, IConveyorBelt<?> conveyorBelt) {
        return value.getAxis() != Direction.Axis.Y;
    }

    @Override
    protected void writeToData(CompoundTag tag) {
        tag.put("itemHandler", this.inventory.serialize());
        tag.putBoolean("insertion", this.insertion);
    }

    @Override
    protected void readData(CompoundTag tag) {
        this.inventory.deserialize(tag.getCompound("itemHandler"));
        this.insertion = tag.getBoolean("insertion");
    }

    @Override
    public void setDirection(Direction facing) {
        super.setDirection(facing);
        inventory.removeInputs(Direction.values());
        inventory.removeOutputs(Direction.values());
        if (insertion) {
            inventory.addOutput(this.getFacing());
        } else {
            inventory.addInput(this.getFacing());
        }
    }

    @Override
    public void onBeltUpdate(IConveyorBelt<?> beltOnTopOf, IConveyorBelt<?> causeOfUpdate, Direction directionFrom) {

    }

    @Override
    public AABB getCollision() {
        if (this.getConveyorImpl() == null) return super.getCollision();
        switch (getFacing()) {
            case EAST -> {
                return new AABB(
                        this.getPos().getX() + 9/16f, this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1);
            }
            case WEST -> {
                return new AABB(
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 7/16f, this.getPos().getY() + 1, this.getPos().getZ() + 1);
            }
            case SOUTH -> {
                return new AABB(
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ() + 9/16f,
                        this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1);
            }
            default -> {
                return new AABB(
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 7/16f);
            }
        }

    }

    @Override
    public IConveyorCover create(ConveyorBeltCoverItem conveyorBeltCoverItem) {
        return new ConveyorBeltPorter();
    }

    public void setToInsert(boolean insertion) {
        this.insertion = insertion;
    }

    public boolean inserting() {
        return insertion;
    }

    private List<IItemHandler> findValidInventorys(Direction ignoreDir) {
        List<IItemHandler> inventories = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (direction == ignoreDir) continue;
            BlockEntity blockEntity = getLevel().getBlockEntity(getPos().relative(direction));
            if (blockEntity != null && !(blockEntity instanceof IConveyorBelt<?>)) {
                blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(inventories::add);
            }
        }
        return inventories;
    }

    private Optional<IItemHandler> getInventory() {
        BlockEntity blockEntity = getLevel().getBlockEntity(getPos().relative(getFacing()));
        if (blockEntity != null && !(blockEntity instanceof IConveyorBelt<?>)) {
            return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        }
        return Optional.empty();
    }

    private IItemHandler getValidConveyor() {
        return getConveyorImpl().getBelt().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();
    }

    @Override
    public void tick(IConveyorBelt<?> belt) {
        if (belt == null) return;

        if (getConveyorImpl() == null) setBelt(belt);

        if (getInventory().isEmpty()) return;

        if (inserting()) {

            // this just checks if it can find the item entity on the conveyor and if its within the bounds of the porter
            for (ConveyorBeltItemStack entity : getLevel().getEntitiesOfClass(ConveyorBeltItemStack.class, getCollision())) {
                 ItemStack ret = getValidConveyor().extractItem(0, 1, true);
                 if (!ret.isEmpty() && ret.is(entity.getTransportedStack().getItem()))
                     ItemStackHandlerUtils.insertAndModifyStack(getInventory().get(), getValidConveyor().extractItem(0, 1, false), movementLimit);
            }

            return;
        }

        ItemStack inputStack = this.inventory.getStackFromInputHandler(INPUT_SLOT);

        if (!inserting()) {
            tryExtraction(getInventory().get());
            ItemStack ret = inventory.getInputHandler().forceExtractItem(INPUT_SLOT, movementLimit, true);
            if (!ret.isEmpty()) {
                // now we need to check if we can insert this item into our inventory.
                ItemStack simulate = ItemHandlerHelper.insertItem(getValidConveyor(), ret, true);

                // this is ideal
                if (simulate.isEmpty()) {
                    ItemStackHandlerUtils.insertAndModifyStack(getValidConveyor(), inventory.getInputHandler().forceExtractItem(INPUT_SLOT, movementLimit, false));
                } else {
                    int amountThatCanBeInserted = ret.getCount() - simulate.getCount();
                    ItemStackHandlerUtils.insertAndModifyStack(getValidConveyor(), inventory.getInputHandler().forceExtractItem(INPUT_SLOT, amountThatCanBeInserted, false));
                }
            }
        }

    }

    private void tryExtraction(IItemHandler itemHandler) {

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            ItemStack ret = itemHandler.extractItem(i, movementLimit, true);
            // we have an item is ret is a non empty stack
            if (!ret.isEmpty()) {

                // now we need to check if we can insert this item into our inventory.
                ItemStack simulate = ItemHandlerHelper.insertItem(this.inventory, ret, true);

                // this is ideal
                if (simulate.isEmpty()) {
                    ItemStackHandlerUtils.insertAndModifyStack(inventory, itemHandler.extractItem(i, movementLimit, false));
                } else {
                    int amountThatCanBeInserted = ret.getCount() - simulate.getCount();
                    ItemStackHandlerUtils.insertAndModifyStack(inventory, itemHandler.extractItem(i, amountThatCanBeInserted, false));
                }
            }
        }

    }

    @Override
    protected AbstractConveyorCoverRenderer instance() {
        return new ConveyorBeltPorterRenderer();
    }
}
