package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.CentrifugeEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class CentrifugeMenu extends DefaultMenu<CentrifugeEntity> {

    public CentrifugeMenu(int pContainerId, Inventory playerInventory, Player player, CentrifugeEntity blockEntity) {
        super(ExotekRegistry.Menus.CENTRIFUGE_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 54, 36);
            addSlot(cap, 1, 115, 26);
            addSlot(cap, 2, 115, 26+18);
        });
    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack returnStack, ItemStack copied, int index) {
        if (!this.moveItemStackTo(copied, 36, 38, false)) {
            return ItemStack.EMPTY;
        }
        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
