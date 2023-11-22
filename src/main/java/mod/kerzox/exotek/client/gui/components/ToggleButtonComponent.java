package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ToggleButtonComponent<T extends DefaultMenu<?>> extends ButtonComponent<T> {

    public ToggleButtonComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u, int v, int u2, int v2, IPressable btn) {
        super(screen, texture, x, y, width, height, u, v, u2, v2, btn);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            this.state = !state;
            this.button.onPress(this);
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(),
                    Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER), 1.0F);
            return true;
        }
    }

    public void setState(boolean state) {
        this.state = state;
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
    }

}
