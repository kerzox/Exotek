package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ButtonComponent<T extends DefaultMenu<?>> extends WidgetComponent<T> {

    protected IPressable button;
    protected boolean state;
    protected int u1, v1, u2, v2;

    protected int tick;

    public ButtonComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u, int v, int u2, int v2, IPressable btn) {
        super(screen, x, y, width, height, texture);
        this.button = btn;
        this.u2 = u2;
        this.v2 = v2;
        this.u1 = u;
        this.v1 = v;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            this.state = true;
            this.button.onPress(this);
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(),
                    Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER), 1.0F);
            return true;
        }
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!state) {
            setTextureOffset(u1, v1);
            this.draw(graphics, this.x, this.y, this.width, this.height);
        } else {
            setTextureOffset(u2, v2);
            this.draw(graphics, this.x, this.y, this.width, this.height);
        }
        if (tick % 10 == 1) {
            if (state) {
                state = false;
            }
        }
        tick++;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean b) {
        this.state =b;
    }

    public interface IPressable {
        void onPress(ButtonComponent<?> button);
    }

}
