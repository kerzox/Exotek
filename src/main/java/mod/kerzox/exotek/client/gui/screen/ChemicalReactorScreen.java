package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.ChemicalReactorMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class ChemicalReactorScreen extends DefaultScreen<ChemicalReactorMenu> {

    private ProgressComponent<ChemicalReactorMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
    private ProgressComponent<ChemicalReactorMenu> chemProgress = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 71, 49, 14, 19, 85, 24, 99, 24);


    private TankComponent<ChemicalReactorMenu> inputTank1 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(0), 28, 22, 18, 18, 110, 67, 110, 49);
    private TankComponent<ChemicalReactorMenu> inputTank2 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(1),28, 22+18, 18, 18, 110, 67, 110, 49);
    private TankComponent<ChemicalReactorMenu> inputTank3 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(2),46, 46, 18, 18, 110, 67, 110, 49);

    private TankComponent<ChemicalReactorMenu> outputTank1 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getOutputHandler().getStorageTank(0),113, 55, 18, 18, 110, 67, 110, 49);
    private TankComponent<ChemicalReactorMenu> outputTank2 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getOutputHandler().getStorageTank(1), 113+18, 55, 18, 18, 110, 67, 110, 49);


    public ChemicalReactorScreen(ChemicalReactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "chemical_reactor.png");
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        addWidgetComponent(chemProgress);
        addWidgetComponent(inputTank1);
        addWidgetComponent(inputTank2);
        addWidgetComponent(inputTank3);
        addWidgetComponent(outputTank1);
        addWidgetComponent(outputTank2);

        getMenu().getUpdateTag();

        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }


    @Override
    protected void menuTick() {
        getMenu().getUpdateTag();
        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);


        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            chemProgress.update(totalDuration - duration, totalDuration, ProgressComponent.Direction.UP);
        } else {
            chemProgress.update(0, 0, ProgressComponent.Direction.UP);
        }

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
