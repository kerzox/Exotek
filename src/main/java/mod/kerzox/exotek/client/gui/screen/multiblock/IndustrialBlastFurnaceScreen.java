package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.client.gui.menu.ExotekBlastFurnaceMenu;
import mod.kerzox.exotek.client.gui.menu.IndustrialBlastFurnaceMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class IndustrialBlastFurnaceScreen extends DefaultScreen<IndustrialBlastFurnaceMenu> {

    public IndustrialBlastFurnaceScreen(IndustrialBlastFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "industrial_blast_furnace.png");
    }

    @Override
    protected void onOpen() {

    }


    @Override
    protected void menuTick() {

    }


    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
