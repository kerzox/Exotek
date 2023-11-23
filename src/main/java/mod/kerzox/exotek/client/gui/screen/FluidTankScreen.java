package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.client.gui.menu.FluidTankMenu;
import mod.kerzox.exotek.client.gui.menu.multiblock.FluidTankMultiblockMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class FluidTankScreen extends DefaultScreen<FluidTankMenu> {

    private TankComponent inputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getTank(),
            45, 20, 98, 50, 158, 0, 158, 50);

    public FluidTankScreen(FluidTankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "fluid_tank.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(inputTank);
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
