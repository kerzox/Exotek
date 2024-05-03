package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.ExotekBlastFurnaceEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ExotekBlastFurnaceMenu extends DefaultMenu<ExotekBlastFurnaceEntity> {

    public ExotekBlastFurnaceMenu(int pContainerId, Inventory playerInventory, Player player, ExotekBlastFurnaceEntity blockEntity) {
        super(ExotekRegistry.Menus.BLAST_FURNACE_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 49, 18);
            addSlot(cap, 1, 49, 50);
            addSlot(cap, 2, 127, 34);
        });
    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack returnStack, ItemStack copied, int index) {
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
