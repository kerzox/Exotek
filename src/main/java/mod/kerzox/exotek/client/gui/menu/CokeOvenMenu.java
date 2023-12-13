package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.CokeOvenEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class CokeOvenMenu extends DefaultMenu<CokeOvenEntity> {

    public CokeOvenMenu(int pContainerId, Inventory playerInventory, Player player, CokeOvenEntity blockEntity) {
        super(ExotekRegistry.Menus.COKE_OVEN_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 32, 22);
            addSlot(cap, 1, 32, 54);
            addSlot(cap, 2, 111, 34);
        });
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
