package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.CompressorEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class CompressorMenu extends DefaultMenu<CompressorEntity> {

    public CompressorMenu(int pContainerId, Inventory playerInventory, Player player, CompressorEntity blockEntity) {
        super(ExotekRegistry.Menus.COMPRESSOR_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 80, 16);
            addSlot(cap, 1, 60, 16);
            addSlot(cap, 2, 80, 52);
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
