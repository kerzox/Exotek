package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.menu.CokeOvenMenu;
import mod.kerzox.exotek.client.gui.menu.ExotekBlastFurnaceMenu;
import mod.kerzox.exotek.client.gui.menu.WashingPlantMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class CokeOvenScreen extends DefaultScreen<CokeOvenMenu> {

//    private ProgressComponent<CokeOvenMenu> cookingProgress = new ProgressComponent<>(
//            this,
//            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
//            79, 36, 10, 14, 36, 48, 46, 48);

    public CokeOvenScreen(CokeOvenMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "coke_oven.png");
    }

    @Override
    protected void menuTick() {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
