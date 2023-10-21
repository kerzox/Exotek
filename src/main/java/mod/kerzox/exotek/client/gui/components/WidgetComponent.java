package mod.kerzox.exotek.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class WidgetComponent<T extends DefaultMenu<?>> implements Renderable, LayoutElement, NarratableEntry, GuiEventListener {

    protected ResourceLocation widgetTexture;
    protected final int x1;
    protected final int y1;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int u;
    protected int v;
    protected boolean isHovered;
    public boolean active = true;
    public boolean visible = true;
    private boolean focused;

    protected ICustomScreen screen;

    public WidgetComponent(ICustomScreen screen, int x, int y, int width, int height, ResourceLocation texture) {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.x1 = x;
        this.y1 = y;
        this.u = 0;
        this.v = 0;
        this.width = width;
        this.height = height;
        this.widgetTexture = texture;
    }

    public ICustomScreen getScreen() {
        return screen;
    }

    public void setTextureOffset(int u, int v) {
        this.u = u;
        this.v = v;
    }

    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return ((pMouseX > this.x) && (pMouseX < this.x + this.width)) &&
                ((pMouseY > this.y) && (pMouseY < this.y + this.height));
    }

    public void updateTexture(ResourceLocation widgetTexture) {
        this.widgetTexture = widgetTexture;
    }

    public void updatePositionToScreen() {
        this.x = screen.getGuiLeft() + x1;
        this.y = screen.getGuiTop() + y1;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ResourceLocation getWidgetTexture() {
        return widgetTexture;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }

    public int getV() {
        return v;
    }

    public int getU() {
        return u;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    @Override
    public ScreenRectangle getRectangle() {
        return LayoutElement.super.getRectangle();
    }

    @Override
    public void setPosition(int p_265617_, int p_265577_) {
        LayoutElement.super.setPosition(p_265617_, p_265577_);
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> p_265082_) {

    }


    public void setColor(float red, float green, float blue, float alpha) {
        RenderSystem.setShaderColor(red, green, blue, alpha);
    }

    public void setColor(int color) {
        float alpha = ((color >> 24) & 0xFF) / 255F;
        float red = ((color >> 16) & 0xFF) / 255F;
        float green = ((color >> 8) & 0xFF) / 255F;
        float blue = ((color) & 0xFF) / 255F;
        setColor(red, green, blue, alpha);
    }


    protected void draw(GuiGraphics graphics, int pX, int pY, int pWidth, int pHeight) {
        this.draw(graphics, pX, pY, pWidth, pHeight, 256, 256);
    }

    protected void draw(GuiGraphics graphics, int pX, int pY, int pWidth, int pHeight, int texHeight, int texWidth) {
        graphics.blit(getWidgetTexture(), pX, pY, this.u, this.v, pWidth, pHeight, texHeight, texWidth);
    }

    public abstract void drawComponent(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick);

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!visible) return;
        this.x = this.screen.getGuiLeft() + this.x1;
        this.y = this.screen.getGuiTop() + this.y1;
        drawComponent(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }


    @Override
    public void mouseMoved(double p_94758_, double p_94759_) {
        GuiEventListener.super.mouseMoved(p_94758_, p_94759_);
    }

    @Override
    public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
        return GuiEventListener.super.mouseClicked(p_94737_, p_94738_, p_94739_);
    }

    @Override
    public boolean mouseReleased(double p_94753_, double p_94754_, int p_94755_) {
        return GuiEventListener.super.mouseReleased(p_94753_, p_94754_, p_94755_);
    }

    @Override
    public boolean mouseDragged(double p_94740_, double p_94741_, int p_94742_, double p_94743_, double p_94744_) {
        return GuiEventListener.super.mouseDragged(p_94740_, p_94741_, p_94742_, p_94743_, p_94744_);
    }

    @Override
    public boolean mouseScrolled(double p_94734_, double p_94735_, double p_94736_) {
        return GuiEventListener.super.mouseScrolled(p_94734_, p_94735_, p_94736_);
    }

    @Override
    public boolean keyPressed(int p_94745_, int p_94746_, int p_94747_) {
        return GuiEventListener.super.keyPressed(p_94745_, p_94746_, p_94747_);
    }

    @Override
    public boolean keyReleased(int p_94750_, int p_94751_, int p_94752_) {
        return GuiEventListener.super.keyReleased(p_94750_, p_94751_, p_94752_);
    }

    @Override
    public boolean charTyped(char p_94732_, int p_94733_) {
        return GuiEventListener.super.charTyped(p_94732_, p_94733_);
    }

    @Nullable
    @Override
    public ComponentPath nextFocusPath(FocusNavigationEvent p_265234_) {
        return GuiEventListener.super.nextFocusPath(p_265234_);
    }

    @Override
    public void setFocused(boolean p_265728_) {

    }

    @Override
    public boolean isFocused() {
        return false;
    }

    @Nullable
    @Override
    public ComponentPath getCurrentFocusPath() {
        return GuiEventListener.super.getCurrentFocusPath();
    }

    @Override
    public NarrationPriority narrationPriority() {
        return NarrationPriority.NONE;
    }

    @Override
    public boolean isActive() {
        return NarratableEntry.super.isActive();
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }

    @Override
    public int getTabOrderGroup() {
        return NarratableEntry.super.getTabOrderGroup();
    }

}
