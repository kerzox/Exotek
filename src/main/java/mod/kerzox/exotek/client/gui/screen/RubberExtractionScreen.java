package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.menu.RubberExtractionMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class RubberExtractionScreen extends DefaultScreen<RubberExtractionMenu> {

//    private ProgressComponent<RubberExtractionMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
//    private ProgressComponent<RubberExtractionMenu> fluidProgress = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 83, 51, 10, 14, 57, 48, 67, 48);


    public RubberExtractionScreen(RubberExtractionMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "rubber_extraction.png");
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
