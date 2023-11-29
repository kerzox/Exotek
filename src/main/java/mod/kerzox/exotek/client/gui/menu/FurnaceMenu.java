package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.FurnaceEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class FurnaceMenu extends DefaultMenu<FurnaceEntity> {

    public FurnaceMenu(int pContainerId, Inventory playerInventory, Player player, FurnaceEntity blockEntity) {
        super(ExotekRegistry.Menus.FURNACE_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            switch (blockEntity.getTier(getBlockEntity())) {
                case BASIC -> addSlotBox(cap, 0, 60, 18, 3, 20, 2, 18*2);
                case ADVANCED -> addSlotBox(cap, 0, 40, 18, 5, 20, 2, 18*2);
                case SUPERIOR -> addSlotBox(cap, 0, 32, 18, 7, 20, 2, 18*2);
                default -> {
                    addSlot(cap, 0, 49, 34);
                    addSlot(cap, 1, 111, 34);
                }
            }
        });
        addUpgradeSlots();
    }


    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
        switch (blockEntity.getTier(getBlockEntity())) {
            case BASIC -> {
                if (this.moveItemStackTo(copied, 36, 36+3, false)) {
                    return ItemStack.EMPTY;
                }
            }
            case ADVANCED -> {
                if (this.moveItemStackTo(copied, 36, 36+5, false)) {
                    return ItemStack.EMPTY;
                }
            }
            case SUPERIOR -> {
                if (this.moveItemStackTo(copied, 36, 36+7, false)) {
                    return ItemStack.EMPTY;
                }
            }
            default -> {
                if (this.moveItemStackTo(copied, 36, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
