package mod.kerzox.exotek.client.gui.menu.transfer;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.CokeOvenEntity;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnergyCableMenu extends DefaultMenu<EnergyCableEntity> {

    public EnergyCableMenu(int pContainerId, Inventory playerInventory, Player player, EnergyCableEntity blockEntity) {
        super(Registry.Menus.ENERGY_CABLE_MENU.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
     //   layoutPlayerInventorySlots(8, 84);
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
