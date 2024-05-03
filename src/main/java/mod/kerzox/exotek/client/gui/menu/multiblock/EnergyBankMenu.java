package mod.kerzox.exotek.client.gui.menu.multiblock;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.EnergyBankCasingEntity;
import mod.kerzox.exotek.common.util.ColourHex;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class EnergyBankMenu extends DefaultMenu<EnergyBankCasingEntity> {

    public EnergyBankMenu(int pContainerId, Inventory playerInventory, Player player, EnergyBankCasingEntity blockEntity) {
        super(ExotekRegistry.Menus.ENERGY_BANK_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlot(cap, 0, 8, 18);
            addSlot(new SlotComponent(cap, 1, 8, 49) {
                @Override
                public void highlightOnHover(GuiGraphics graphics, int mouseX, int mouseY) {
                    RenderSystem.enableDepthTest();
                    if (isMouseOver(mouseX, mouseY)) {
                        graphics.fill(x1, y1, x1 + width, y1 + height, ColourHex.ENERGY_GREEN.changeOpacity(45));
                    }
                }
            });
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
