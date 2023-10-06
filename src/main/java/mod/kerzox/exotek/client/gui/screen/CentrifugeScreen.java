package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.CentrifugeMenu;
import mod.kerzox.exotek.client.gui.menu.CircuitAssemblyMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class CentrifugeScreen extends DefaultScreen<CentrifugeMenu> {

    private ProgressComponent<CentrifugeMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
    private ProgressComponent<CentrifugeMenu> whirlProgress = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            85, 37, 14, 14, 128, 105, 128, 91);
    private TankComponent<CentrifugeMenu> inputTank = new TankComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(0), 32, 19, 18, 50, 92, 69, 0, 15);
    private TankComponent<CentrifugeMenu> outputTank = new TankComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getOutputHandler().getStorageTank(0), 134, 19, 18, 50, 92, 69, 0, 15);

    public CentrifugeScreen(CentrifugeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "centrifuge.png");
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        addWidgetComponent(inputTank);
        addWidgetComponent(outputTank);
        addWidgetComponent(whirlProgress);

        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }


    @Override
    protected void menuTick() {
        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);


        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            whirlProgress.update(totalDuration - duration, totalDuration, ProgressComponent.Direction.RIGHT);
        } else {
            whirlProgress.update(0, 0, ProgressComponent.Direction.RIGHT);
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
