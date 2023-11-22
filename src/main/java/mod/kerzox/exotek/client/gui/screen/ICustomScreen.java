package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;

public interface ICustomScreen {
    int getGuiLeft();
    int getGuiTop() ;
    DefaultMenu<?> getMenu();
}
