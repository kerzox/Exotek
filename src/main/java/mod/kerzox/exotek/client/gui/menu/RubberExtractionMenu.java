package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.MaceratorEntity;
import mod.kerzox.exotek.common.blockentities.machine.RubberExtractionEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class RubberExtractionMenu extends DefaultMenu<RubberExtractionEntity> {

    public RubberExtractionMenu(int pContainerId, Inventory playerInventory, Player player, RubberExtractionEntity blockEntity) {
        super(Registry.Menus.RUBBER_EXTRACTION_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);

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
