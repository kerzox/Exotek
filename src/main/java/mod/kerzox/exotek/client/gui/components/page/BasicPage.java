package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.client.gui.components.NewWidgetComponent;
import mod.kerzox.exotek.client.gui.components.TexturedWidgetComponent;
import mod.kerzox.exotek.client.gui.components.UpgradeComponent;
import mod.kerzox.exotek.client.gui.components.WidgetComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.client.gui.screen.WorkstationScreen;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public abstract class BasicPage extends TexturedWidgetComponent {

    protected int u1, v1, u2, v2;

    public BasicPage(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Component message) {
        super(screen, x, y, width, height, u1, v1, texture, message);
    }

}
