package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.menu.multiblock.EnergyBankMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.SolarPanelScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class EnergyBankScreen extends DefaultScreen<EnergyBankMenu> {

    private ProgressComponent<EnergyBankMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            28, 22, 141, 49, 0, 119, 0, 168) {
        @Override
        public void doHover(GuiGraphics graphics, int pMouseX, int pMouseY) {
            Component text = Component.literal("Energy: " +
                    SolarPanelScreen.abbreviateNumber(energyBar.getMinimum(), true) + "/"
                    + SolarPanelScreen.abbreviateNumber(energyBar.getMaximum(), true));

            graphics.renderTooltip(font, List.of(text),
                    Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    };

    public EnergyBankScreen(EnergyBankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "burnable_generator.png");
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        energyBar.updateWithDirection(getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0), getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }

    @Override
    protected void menuTick() {
        energyBar.updateWithDirection(getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0), getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
