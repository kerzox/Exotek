package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.storage.FluidTankSingleEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class FluidTankMenu extends DefaultMenu<FluidTankSingleEntity> {

    public FluidTankMenu(int pContainerId, Inventory playerInventory, Player player, FluidTankSingleEntity blockEntity) {
        super(ExotekRegistry.Menus.FLUID_TANK_GUI.get(), pContainerId, playerInventory, player, blockEntity);
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
