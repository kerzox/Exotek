package mod.kerzox.exotek.common.blockentities;

import com.google.common.collect.Lists;
import mod.kerzox.exotek.common.capability.*;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Wrapper over the capability block entity class that handles the IO from the screen,
 * doPush, doExtract are called on each capability and can allow for those functionality (push items into inventories etc)
 */

public abstract class MachineBlockEntity extends CapabilityBlockEntity implements IServerTickable {

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void tick() {
        for (CapabilityHolder<?> holder : getCapabilityHolders()) {
            if (holder.getInstance() instanceof ICapabilityIO io) {
                switch (io.getIOSetting()) {
                    case PUSH -> doPush(holder);
                    case EXTRACT -> doExtract(holder);
                    case ALL -> {
                        doPush(holder);
                        doExtract(holder);
                    }
                }
            }
        }
    }

    /**
     * Is the item handler a valid place to push into
     * @param strictHandler our inventory handler
     * @param cap inventory we are accessing
     * @param stackInSlot item we are going to push.
     * @param slotIndex inventories slot index we are pushing into
     * @return whether the inventory is valid for pushing.
     */

    protected boolean isValidPush(IStrictCombinedItemHandler strictHandler, IItemHandler cap, ItemStack stackInSlot, int slotIndex) {
        return !cap.isItemValid(slotIndex, stackInSlot);
    }

    /**
     * Push items into neighbouring inventories (only works on sides that are either outputs or combined)
     * @param strictHandler item handler.
     */

    protected void pushItemsToNeighbouringInventories(IStrictCombinedItemHandler strictHandler) {

        for (Direction direction : strictHandler.getOutputs()) {

            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));

            if (blockEntity == null) continue;

            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).ifPresent(cap -> {
                for (int i = 0; i < strictHandler.getOutputHandler().getSlots(); i++) {
                    ItemStack stackInSlot = strictHandler.getOutputHandler().extractItem(i, 1, true);
                    if (stackInSlot.isEmpty() || !isValidPush(strictHandler, cap, stackInSlot, i)) continue;
                    ItemStack ret = ItemHandlerHelper.insertItem(cap, stackInSlot, false);
                    if (ret.isEmpty()) strictHandler.getOutputHandler().extractItem(i, 1, false);
                }
            });

        }

    }

    /**
     * Is the item handler a valid place to extract from
     * @param strictHandler our inventory handler
     * @param cap inventory we are accessing
     * @param stackInSlot stack we are getting inserted with
     * @return whether the inventory is valid for extraction.
     */

    protected boolean isValidExtract(IStrictCombinedItemHandler strictHandler, IItemHandler cap, ItemStack stackInSlot) {
        return true;
    }


    /**
     * Extract items from neighbouring inventories
     * @param strictHandler item handler
     */

    protected void extractItemsFromNeighbouringInventories(IStrictCombinedItemHandler strictHandler) {

        for (Direction direction : strictHandler.getInputs()) {

            BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(direction));
            if (blockEntity == null) continue;

            blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).ifPresent(cap -> {
                for (int i = 0; i < cap.getSlots(); i++) {
                    ItemStack stackInSlot = cap.extractItem(i, 1, true);
                    if (stackInSlot.isEmpty() || !isValidExtract(strictHandler, cap, stackInSlot)) continue;
                    ItemStack ret = ItemHandlerHelper.insertItem(strictHandler, stackInSlot, false);
                    if (ret.isEmpty()) cap.extractItem(i, 1, false);
                }
            });

        }

    }

    private void pushEnergyToNeighbouringInventories(SidedEnergyHandler cap) {

    }


    protected void doPush(CapabilityHolder<?> capability) {

        // add default item push
        if (capability.getType() == ForgeCapabilities.ITEM_HANDLER) {
            if (capability.getInstance() instanceof IStrictCombinedItemHandler cap) pushItemsToNeighbouringInventories(cap);
        }

        else if (capability.getType() == ForgeCapabilities.ENERGY) {
            if (capability.getInstance() instanceof SidedEnergyHandler cap) pushEnergyToNeighbouringInventories(cap);
        }

    }

    protected void doExtract(CapabilityHolder<?> capability) {

        if (capability.getType() == ForgeCapabilities.ITEM_HANDLER) {
            if (capability.getInstance() instanceof IStrictCombinedItemHandler cap) extractItemsFromNeighbouringInventories(cap);
        }

    }


}
