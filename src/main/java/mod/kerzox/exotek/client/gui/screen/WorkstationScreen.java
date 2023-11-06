package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.page.BasicPage;
import mod.kerzox.exotek.client.gui.menu.ManufactoryMenu;
import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WorkstationScreen extends DefaultScreen<WorkstationMenu> {

    private ResourceLocation blueprint = new ResourceLocation(Exotek.MODID, "textures/gui/workstation.png");
    private ResourceLocation withOutBlueprint = new ResourceLocation(Exotek.MODID, "textures/gui/workstation_without_blueprint.png");
    private int tab = 33;

    protected BasicPage<WorkstationMenu> colourTab = new BasicPage<>(this, 143, 31, 24, 33, 0, 0,
            new ResourceLocation(Exotek.MODID, "textures/gui/workstation_booklet.png")) {

        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            setTextureOffset(0, tab);
            this.draw(graphics, getGuiLeft() + this.x, getGuiTop() + this.y, this.width, this.height, 24,66);
        }

    };

    private ButtonComponent<ManufactoryMenu> lockButton = new ToggleButtonComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            150, 31, 9, 8, 0, 217, 0, 217, (button -> switchTabs(button, 33))) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };

    private ButtonComponent<ManufactoryMenu> lockButton2 = new ToggleButtonComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            153, 31+8, 9, 8, 0, 217, 0, 217, buttonComponent -> switchTabs(buttonComponent, 0)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };

    private ButtonComponent<ManufactoryMenu> lockButton3 = new ToggleButtonComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            156, 31+8+8, 9, 8, 0, 217, 0, 217, buttonComponent -> switchTabs(buttonComponent, 2)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };


    private ButtonComponent<ManufactoryMenu> lockButton4 = new ToggleButtonComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            159, 31+8+8+8, 9, 8, 0, 217, 0, 217, buttonComponent -> switchTabs(buttonComponent, 3)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };


    public WorkstationScreen(WorkstationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "workstation.png", false);
    }

    private void switchTabs(ButtonComponent<?> buttonComponent, int i) {
        this.tab = i;
        if (tab == 33) {
            setTexture(blueprint);
        } else {
            setTexture(withOutBlueprint);
        }
    };

    @Override
    protected void onOpen() {
        addWidgetComponent(lockButton);
        addWidgetComponent(lockButton2);
        addWidgetComponent(lockButton3);
        addWidgetComponent(lockButton4);
    }


    @Override
    protected void menuTick() {


    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }

    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {

    }

    protected void renderBeforeBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        colourTab.drawComponent(graphics, pMouseX, pMouseY, partialTick);
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        //colourTab.drawComponent(graphics, pMouseX, pMouseY, partialTick);
    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
