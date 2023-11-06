package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ScrollableComponent<T extends DefaultMenu<?>> extends WidgetComponent<T>  {

    int prevX, prevY;

    public ScrollableComponent(ICustomScreen screen, int x, int y, int width, int height, ResourceLocation texture) {
        super(screen, x, y, width, height, texture);
    }

    @Override
    public void drawComponent(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (isDragging()) {
            scroll(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);

    }

    protected void scroll(double mouseX, double mouseY) {

    }

}
