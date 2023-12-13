package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.ExotekBlastFurnaceMenu;
import mod.kerzox.exotek.client.gui.menu.FurnaceMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class ExotekBlastFurnaceScreen extends DefaultScreen<ExotekBlastFurnaceMenu> {

    private RecipeProgressComponent cookingProgress = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            88, 35, 10, 14, 36, 48, 46, 48, Component.literal("Cooking Progress"), ProgressComponent.Direction.UP);

    private RecipeProgressComponent burnTime_progress = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            40, 49, 6, 18, 232, 100, 238, 100, Component.literal("BurnTime Progress"), ProgressComponent.Direction.UP) {
        @Override
        protected void onHover(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            if (getMinimum() != 0) {
                drawToolTips(graphics, mouseX, mouseY, Component.literal("Time left: " + String.format("%.1f", Math.abs(((float)((getMaximum() - getMinimum()) - getMaximum()) / 20))) + " seconds"));
            }
        }
    };

    public ExotekBlastFurnaceScreen(ExotekBlastFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "blast_furnace.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(cookingProgress);
        addRenderableWidget(burnTime_progress);
    }


    @Override
    protected void menuTick() {
        int totalDuration = getMenu().getBlockEntity().getMultiblockManager().getMaxDuration();
        int duration = getMenu().getBlockEntity().getMultiblockManager().getDuration();

        int burnTimeMax = getMenu().getBlockEntity().getMultiblockManager().getMaxBurnTime();
        int burnTime = getMenu().getBlockEntity().getMultiblockManager().getBurnTime();

        if (burnTime > 0) {
            burnTime_progress.update(burnTime, burnTimeMax);
        } else {
            burnTime_progress.update(0, 0);
        }

        if (duration > 0) {
            cookingProgress.update(totalDuration - duration, totalDuration);
        } else {
            cookingProgress.update(0, 0);
        }

    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
