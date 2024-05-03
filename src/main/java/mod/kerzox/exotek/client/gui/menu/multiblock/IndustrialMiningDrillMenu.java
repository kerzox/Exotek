package mod.kerzox.exotek.client.gui.menu.multiblock;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.BoilerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MinerEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class IndustrialMiningDrillMenu extends DefaultMenu<MinerEntity> {

    public IndustrialMiningDrillMenu(int pContainerId, Inventory playerInventory, Player player, MinerEntity blockEntity) {
        super(ExotekRegistry.Menus.INDUSTRIAL_MINING_DRILL_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
//        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
//            addSlot(cap, 0, 8, 54);
//        });
        addUpgradeSlots();
    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack returnStack, ItemStack copied, int index) {
        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
