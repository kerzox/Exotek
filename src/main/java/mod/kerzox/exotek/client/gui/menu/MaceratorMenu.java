package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.MaceratorEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class MaceratorMenu extends DefaultMenu<MaceratorEntity> {

    public MaceratorMenu(int pContainerId, Inventory playerInventory, Player player, MaceratorEntity blockEntity) {
        super(ExotekRegistry.Menus.MACERATOR_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            switch (blockEntity.getTier(getBlockEntity())) {
                case BASIC -> addSlotBox(cap, 0, 60, 18, 3, 20, 2, 18*2);
                case ADVANCED -> addSlotBox(cap, 0, 40, 18, 5, 20, 2, 18*2);
                case SUPERIOR -> addSlotBox(cap, 0, 32, 18, 7, 20, 2, 18*2);
                default -> {
                    addSlot(cap, 0, 44, 34);
                    addSlot(cap, 1, 116, 34);
                }
            }
        });
    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack returnStack, ItemStack copied, int index) {

        if (index == 36) return copied;

        if (!this.moveItemStackTo(copied, 36, 37, false)) {
            return ItemStack.EMPTY;
        }

        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
