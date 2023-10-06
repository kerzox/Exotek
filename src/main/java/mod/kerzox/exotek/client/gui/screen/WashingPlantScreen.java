package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.WashingPlantMenu;
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

public class WashingPlantScreen extends DefaultScreen<WashingPlantMenu> {

    private ProgressComponent<WashingPlantMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
    private TankComponent<WashingPlantMenu> fluidTank = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSingleFluidTank(),
            71, 23, 18, 18, 110, 67, 110, 49);
    private ProgressComponent<WashingPlantMenu> fluidProgress = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 82, 48, 10, 14, 57, 48, 67, 48);
    private ButtonComponent<WashingPlantMenu> emptyButton =
            new ButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                    54, 26, 12, 12, 0, 217, 0, 217+12, button -> onEmptyPress());

    public WashingPlantScreen(WashingPlantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "washing_plant.png");
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        addWidgetComponent(fluidTank);
        addWidgetComponent(fluidProgress);
        addWidgetComponent(emptyButton);

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
            fluidProgress.update(totalDuration - duration, totalDuration, ProgressComponent.Direction.DOWN);
        } else {
            fluidProgress.update(0, 0, ProgressComponent.Direction.DOWN);
        }
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        emptyButton.mouseClicked(p_97748_, p_97749_, p_97750_);
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }

    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (energyBar.isMouseOver(pMouseX, pMouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.literal("Stored Energy: " + this.energyBar.getMinimum())), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    }

    private void onEmptyPress() {
        //TODO Send a server packet to empty fluid tank
        Minecraft.getInstance().player.sendSystemMessage(Component.literal("Hello"));
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
