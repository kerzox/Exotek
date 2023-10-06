package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.MaceratorEntity;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ManufactoryMenu extends DefaultMenu<ManufactoryEntity> {

    public ManufactoryMenu(int pContainerId, Inventory playerInventory, Player player, ManufactoryEntity blockEntity) {
        super(Registry.Menus.MANUFACTORY_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 45, 18);
            addSlot(cap, 1, 45+18, 18);
            addSlot(cap, 2, 45+18+18, 18);


            addSlot(cap, 3, 45, 18+18);
            addSlot(cap, 4, 45+18, 18*2);
            addSlot(cap, 5, 45+18+18, 18*2);

            addSlot(cap, 6, 45, 18*3);
            addSlot(cap, 7, 45+18, 18*3);
            addSlot(cap, 8, 45+18+18, 18*3);

            // output
            addSlot(cap, 9, 130, 36);
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
