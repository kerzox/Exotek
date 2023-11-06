package mod.kerzox.exotek.client.gui.menu.multiblock;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.FluidTankMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.EnergyBankCasingEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class EnergyBankMenu extends DefaultMenu<EnergyBankCasingEntity> {

    public EnergyBankMenu(int pContainerId, Inventory playerInventory, Player player, EnergyBankCasingEntity blockEntity) {
        super(Registry.Menus.ENERGY_BANK_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
//        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
//            addSlot(cap, 0, 80, 16);
//            addSlot(cap, 1, 80, 52);
//        });
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
