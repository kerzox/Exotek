package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.ChemicalReactionChamberEntity;
import mod.kerzox.exotek.common.blockentities.machine.CircuitAssemblyEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class CircuitAssemblyMenu extends DefaultMenu<CircuitAssemblyEntity> {

    public CircuitAssemblyMenu(int pContainerId, Inventory playerInventory, Player player, CircuitAssemblyEntity blockEntity) {
        super(Registry.Menus.CIRCUIT_ASSEMBLY_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 34, 18);
            addSlot(cap, 1, 34, 18*2);
            addSlot(cap, 2, 34, 18*3);
            addSlot(cap, 3, 34+18, 18);
            addSlot(cap, 4, 34+18, 18*2);
            addSlot(cap, 5, 34+18, 18*3);
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
