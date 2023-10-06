package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.TabComponent;
import mod.kerzox.exotek.client.gui.components.WidgetComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public abstract class BasicPage<T extends DefaultMenu<?>> extends WidgetComponent<T> {

    protected int u1, v1, u2, v2;

    public BasicPage(DefaultScreen<T> screen, int x, int y, int width, int height, ResourceLocation texture) {
        this(screen, x, y, width, height, 0, 0, 0, 0, texture);
    }

    public BasicPage(DefaultScreen<T> screen, int x, int y, int width, int height, int u, int v, ResourceLocation texture) {
        this(screen, x, y, width, height, u, v, 0, 0, texture);
    }

    public BasicPage(DefaultScreen<T> screen, int x, int y, int width, int height, int u1, int u2, int v1, int v2, ResourceLocation texture) {
        super(screen, x, y, width, height, texture);
        this.u1 = u1;
        this.u2 = u2;
        this.v1 = v1;
        this.v2 = v2;
        this.visible = false;
    }

    protected void setVisible(boolean b) {
        this.visible = b;
    }
}
