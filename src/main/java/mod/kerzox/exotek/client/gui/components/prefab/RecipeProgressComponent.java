package mod.kerzox.exotek.client.gui.components.prefab;

import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class RecipeProgressComponent extends ProgressComponent {

    public RecipeProgressComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Component component, Direction direction) {
        super(screen, texture, x, y, width, height, u1, v1, u2, v2, component, direction);
    }

    @Override
    protected void onHover(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (getMinimum() != 0) {
            drawToolTips(graphics, mouseX, mouseY, Component.literal("Time left: " + String.format("%.1f", ((float)(getMaximum() - getMinimum()) / 20)) + " seconds"));
        }
    }
}
