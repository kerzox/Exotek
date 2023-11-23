package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.BoilerMenu;
import mod.kerzox.exotek.client.gui.menu.BurnableGeneratorMenu;
import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.BoilerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.BoilerManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class BoilerScreen extends DefaultScreen<BoilerMenu> {

    private TankComponent inputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultiblockManager().getMultifluidTank().getInputHandler().getStorageTank(0),
            51+20, 21, 18, 50, 92, 69, 0, 15, Component.literal("Input Tank"));
    private TankComponent outputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultiblockManager().getMultifluidTank().getOutputHandler().getStorageTank(0),
            51+20+18, 21, 18, 50, 92, 69, 0, 15, Component.literal("Output Tank"));

    public BoilerScreen(BoilerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "burnable_generator.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(inputTank);
        addRenderableWidget(outputTank);
    }

    @Override
    protected void menuTick() {
        getMenu().getUpdateTag();
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {


    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        int heat = ((BoilerManager) getMenu().getBlockEntity().getMultiblockManager()).getHeat();
        int steamGeneration = heat > 100 ? (int) Math.round(0.002 * Math.pow(heat, 2) + 0) : 0;
        FormattedCharSequence energyPerTick = Component.literal("Heat: " + heat).getVisualOrderText();
        graphics.drawString(this.font, energyPerTick, this.getGuiLeft() + 20,
                this.getGuiTop() + 18, 0xff0000, false);
        FormattedCharSequence steamGeneration2 = Component.literal("Steam Generated Per Tick: " + steamGeneration).getVisualOrderText();
        graphics.drawString(this.font, steamGeneration2, this.getGuiLeft() + 20,
                this.getGuiTop() + 18 + 12, 0xff0000, false);
    }
}
