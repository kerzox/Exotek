package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.EngravingEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EngraverMenu extends DefaultMenu<EngravingEntity> {

    public EngraverMenu(int pContainerId, Inventory playerInventory, Player player, EngravingEntity blockEntity) {
        super(ExotekRegistry.Menus.ENGRAVER_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 29, 23);
            addSlot(cap, 1, 49, 34);
            addSlot(cap, 2, 111, 34);
        });
    }

    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
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
