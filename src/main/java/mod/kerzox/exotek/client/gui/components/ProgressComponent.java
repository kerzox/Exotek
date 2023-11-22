package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

public class ProgressComponent<T extends DefaultMenu<?>> extends WidgetComponent<T> {

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
    }

    private int u1, u2, v1, v2;
    private int minimum, maximum;
    private Direction direction;

    public ProgressComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
        super(screen, x, y, width, height, texture);
        this.u1 = u1;
        this.u2 = u2;
        this.v1 = v1;
        this.v2 = v2;
    }

    public ProgressComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Direction direction) {
        this(screen, texture, x, y, width, height, u1, v1, u2, v2);
        this.direction = direction;
    }

    public void updateWithDirection(int min, int max, Direction direction) {
        this.minimum = min;
        this.maximum = max;
        this.direction = direction;
    }

    public void update(int min, int max) {
        this.minimum = min;
        this.maximum = max;
    }


    @Override
    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return ((pMouseX > this.x) && (pMouseX < this.x + this.width)) &&
                ((pMouseY > this.y) && (pMouseY < this.y + this.height));
    }

    public int getMinimum() {
        return minimum;
    }

    public int getMaximum() {
        return maximum;
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (widgetTexture == null) return;

        int size = 0;

        this.setTextureOffset(this.u1, this.v1);
        this.draw(graphics, this.x, this.y, this.width, this.height);

        if (this.maximum != 0) {
            switch (direction) {
                case UP -> {
                    size = this.height * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(graphics, this.x, this.y, this.width, this.height);
                    this.setTextureOffset(this.u1, this.v1);
                    this.draw(graphics, this.x, this.y, this.width, this.height - size);
                }
                case DOWN -> {
                    size = this.height * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(graphics, this.x, this.y, this.width, size);
                }
                case LEFT -> {
                    size = this.width * minimum / maximum;
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(graphics, this.x, this.y, this.width - size, this.height);
                }
                case RIGHT -> {
                    size = this.width * minimum / maximum;
                    this.setTextureOffset(this.u1, this.v1);
                    this.draw(graphics, this.x, this.y, this.width, this.height);
                    this.setTextureOffset(this.u2, this.v2);
                    this.draw(graphics, this.x, this.y, size, this.height);
                }
            }
        }

    }
}
