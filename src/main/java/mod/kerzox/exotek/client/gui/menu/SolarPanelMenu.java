package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.SolarPanelEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class SolarPanelMenu extends DefaultMenu<SolarPanelEntity> {

    public SolarPanelMenu(int pContainerId, Inventory playerInventory, Player player, SolarPanelEntity blockEntity) {
        super(Registry.Menus.SOLAR_PANEL_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
       // layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
    }

    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
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
