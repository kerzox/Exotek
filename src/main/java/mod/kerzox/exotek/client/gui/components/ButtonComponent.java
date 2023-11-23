package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.event.TickUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class ButtonComponent extends TexturedWidgetComponent {

    protected IPressable button;
    protected boolean state;
    protected int u1, v1, u2, v2;

    protected int tick;

    public ButtonComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u, int v, int u2, int v2, Component component, IPressable btn) {
        super(screen, x, y, width, height, u, v, texture, component);
        this.button = btn;
        this.u2 = u2;
        this.v2 = v2;
        this.u1 = u;
        this.v1 = v;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        playDownSound();
        this.button.onPress(this);
        tick = 1 + TickUtils.getClientTick();
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

    @Override
    public void tick() {
        if (tick >= TickUtils.getClientTick()) {
            state = true;
        } else {
            state = false;
            tick++;
        }
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean b) {
        this.state =b;
    }

    public interface IPressable {
        void onPress(ButtonComponent button);
    }

}
