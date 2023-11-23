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

    protected BasicPage colourTab = new BasicPage(this, new ResourceLocation(Exotek.MODID, "textures/gui/workstation_booklet.png"), 143, 31, 24, 33, 0, 0, 0, 0, Component.literal("Colour Tabs")) {

        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            setTextureOffset(0, tab);
            this.draw(graphics, getCorrectX(), getCorrectY(), this.width, this.height, 24, 66);
        }

    };

    private ButtonComponent lockButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            150, 31, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Blueprint"), (button -> switchTabs(button, 33))) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };

    private ButtonComponent lockButton2 = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            153, 31 + 8, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"), buttonComponent -> switchTabs(buttonComponent, 0)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };

    private ButtonComponent lockButton3 = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            156, 31 + 8 + 8, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"), buttonComponent -> switchTabs(buttonComponent, 2)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };


    private ButtonComponent lockButton4 = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            159, 31 + 8 + 8 + 8, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"), buttonComponent -> switchTabs(buttonComponent, 3)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };


    public WorkstationScreen(WorkstationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "workstation.png", false);
    }

    private void switchTabs(ButtonComponent buttonComponent, int i) {
        this.tab = i;
        if (tab == 33) {
            setTexture(blueprint);
        } else {
            setTexture(withOutBlueprint);
        }
    }

    ;

    @Override
    protected void onOpen() {
        addRenderableWidget(lockButton);
        addRenderableWidget(lockButton2);
        addRenderableWidget(lockButton3);
        addRenderableWidget(lockButton4);
    }


    @Override
    protected void menuTick() {


    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }


    protected void renderBeforeBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        colourTab.render(graphics, pMouseX, pMouseY, partialTick);
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        //colourTab.drawComponent(graphics, pMouseX, pMouseY, partialTick);
    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
