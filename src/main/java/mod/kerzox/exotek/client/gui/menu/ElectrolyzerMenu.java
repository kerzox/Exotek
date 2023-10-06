package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.CompressorEntity;
import mod.kerzox.exotek.common.blockentities.machine.ElectrolyzerEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ElectrolyzerMenu extends DefaultMenu<ElectrolyzerEntity> {

    public ElectrolyzerMenu(int pContainerId, Inventory playerInventory, Player player, ElectrolyzerEntity blockEntity) {
        super(Registry.Menus.ELECTROLYZER_GUI.get(), pContainerId, playerInventory, player, blockEntity);
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
