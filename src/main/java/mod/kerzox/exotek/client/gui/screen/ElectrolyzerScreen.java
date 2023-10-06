package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.client.gui.menu.MaceratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class ElectrolyzerScreen extends DefaultScreen<ElectrolyzerMenu> {

    private ProgressComponent<ElectrolyzerMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
    private TankComponent<ElectrolyzerMenu> inputTank = new TankComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultifluidTank().getInputHandler().getStorageTank(0),
            33, 19, 18, 50, 92, 69, 0, 15);
    private TankComponent<ElectrolyzerMenu> outputTank = new TankComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultifluidTank().getOutputHandler().getStorageTank(0),
            88, 16, 18, 34,  110, 85, 18, 31);
    private TankComponent<ElectrolyzerMenu> outputTank2 = new TankComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultifluidTank().getOutputHandler().getStorageTank(1),
            124, 16, 18, 34, 110, 85, 18, 31);

    public ElectrolyzerScreen(ElectrolyzerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "electrolyzer.png");
    }



    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        addWidgetComponent(inputTank);
        addWidgetComponent(outputTank);
        addWidgetComponent(outputTank2);
        getMenu().getUpdateTag();
        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);

    }

    @Override
    protected void menuTick() {
        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }

    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (energyBar.isMouseOver(pMouseX, pMouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.literal("Stored Energy: " + this.energyBar.getMinimum())), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
