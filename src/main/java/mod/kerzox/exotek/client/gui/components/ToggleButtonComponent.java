package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ToggleButtonComponent extends ButtonComponent{

    public ToggleButtonComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u, int v, int u2, int v2, Component component, IPressable btn) {
        super(screen, texture, x, y, width, height, u, v, u2, v2, component, btn);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        this.state = !state;
        playDownSound();
        this.button.onPress(this);
    }

    @Override
    public void tick() {

    }

    public void setState(boolean state) {
        this.state = state;
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (!state) {
            setTextureOffset(u1, v1);
            this.draw(graphics);
        } else {
            setTextureOffset(u2, v2);
            this.draw(graphics);
        }
    }

}
