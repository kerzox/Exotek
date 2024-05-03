package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.ChemicalReactionChamberEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class ChemicalReactorMenu extends DefaultMenu<ChemicalReactionChamberEntity> {

    public ChemicalReactorMenu(int pContainerId, Inventory playerInventory, Player player, ChemicalReactionChamberEntity blockEntity) {
        super(ExotekRegistry.Menus.CHEMICAL_REACTOR_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 52, 25);
            addSlot(cap, 1, 70, 25);
            addSlot(cap, 2, 114, 38);
            addSlot(cap, 3, 114+18, 38);
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
