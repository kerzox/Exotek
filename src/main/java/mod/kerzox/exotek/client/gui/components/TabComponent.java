package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.page.BasicPage;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

public class TabComponent<T extends DefaultMenu<?>> extends ToggleButtonComponent<T> {

    public TabComponent(DefaultScreen<T> screen, int x, int y, int width, int height, int u, int v, int u2, int v2, IPressable btn) {
        super(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), x, y, width, height, u, v, u2, v2, btn);
        setTextureOffset(u, v);
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        if (state) {
            this.draw(graphics, this.x, this.y, this.width + 3, this.height);
        } else {
            this.draw(graphics, this.x, this.y, this.width, this.height);
        }

    }


}
