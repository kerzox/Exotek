package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public abstract class NewWidgetComponent extends AbstractWidget {

    protected int u;
    protected int v;
    protected ICustomScreen screen;

    public NewWidgetComponent(ICustomScreen screen, int x, int y, int width, int height, Component message) {
        super(x, y, width, height, message);
        this.screen = screen;
    }

    @Override
    public void render(GuiGraphics p_282421_, int p_93658_, int p_93659_, float p_93660_) {
        super.render(p_282421_, p_93658_, p_93659_, p_93660_);
    }

    @Override
    protected void renderWidget(GuiGraphics p_282139_, int p_268034_, int p_268009_, float p_268085_) {
        drawComponent(p_282139_, p_268034_, p_268009_, p_268085_);
    }

    protected abstract void drawComponent(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks);

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setVisible(boolean active) {
        this.visible = active;
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        boolean xBounds = ((x > getCorrectX()) && (x < getCorrectX() + this.width));
        boolean yBounds = ((y > getCorrectY()) && (y < getCorrectY() + this.height));
        return this.visible && this.active && xBounds && yBounds;
    }

    @Override
    protected boolean clicked(double p_93681_, double p_93682_) {
        return isMouseOver(p_93681_, p_93682_);
    }

    public int getCorrectX() {
        return this.getX() + screen.getGuiLeft();
    }

    public int getCorrectY() {
        return this.getY() + screen.getGuiTop();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput p_259858_) {

    }
}