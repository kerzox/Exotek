package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.IndustrialBlastFurnaceEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class IndustrialBlastFurnaceMenu extends DefaultMenu<IndustrialBlastFurnaceEntity> {

    public IndustrialBlastFurnaceMenu(int pContainerId, Inventory playerInventory, Player player, IndustrialBlastFurnaceEntity blockEntity) {
        super(ExotekRegistry.Menus.INDUSTRIAL_BLAST_FURNACE_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 52, 34);
            addSlot(cap, 1, 136, 34);
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
