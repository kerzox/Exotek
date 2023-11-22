package mod.kerzox.exotek.compatibility.jei;

import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mod.kerzox.exotek.client.gui.components.page.SettingsPage;
import mod.kerzox.exotek.client.gui.components.page.UpgradePage;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.renderer.Rect2i;

import java.util.ArrayList;
import java.util.List;

public class ExotekJEIGuiHandler implements IGuiContainerHandler<DefaultScreen<?>> {

    @Override
    public List<Rect2i> getGuiExtraAreas(DefaultScreen<?> containerScreen) {
        List<Rect2i> areas = new ArrayList<>();

//        if (containerScreen.isSettingsVisible()) {
//
//            SettingsPage<?> settingsPage = containerScreen.getSettingsPage();
//
//            int x = settingsPage.getSettingsTab().getX();
//            int y = settingsPage.getSettingsTab().getY();
//
//            areas.add(new Rect2i(x, y, settingsPage.getSettingsTab().getWidth(), settingsPage.getSettingsTab().getHeight()));
//        }

        if (containerScreen.getUpgradePage().backgroundShown()) {
            UpgradePage page = containerScreen.getUpgradePage();

            int x = page.getLerpedX();
            int y = page.getCorrectY();

            areas.add(new Rect2i(x, y, page.getWidth(), page.getHeight()));
        }

        return areas;
    }
}
