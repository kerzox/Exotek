package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class SingleBlockMinerMenu extends DefaultMenu<SingleBlockMinerEntity> {

    public SingleBlockMinerMenu(int pContainerId, Inventory playerInventory, Player player, SingleBlockMinerEntity blockEntity) {
        super(ExotekRegistry.Menus.SINGLE_BLOCK_MINER_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlotBox(cap, 0, 133, 18, 2, 18, 3, 18);
        });
        addUpgradeSlots();
    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack returnStack, ItemStack copied, int index) {
        return ItemStack.EMPTY;
    }



    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
