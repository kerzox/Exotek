package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.menu.BurnableGeneratorMenu;
import mod.kerzox.exotek.client.gui.menu.FurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Optional;

public class BurnableGeneratorScreen extends DefaultScreen<BurnableGeneratorMenu> {

    private ProgressComponent<BurnableGeneratorMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            28, 22, 141, 49, 0, 119, 0, 168);

    public BurnableGeneratorScreen(BurnableGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "burnable_generator.png");
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
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
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {


    }

    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (energyBar.isMouseOver(pMouseX, pMouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.literal("Stored Energy: " + this.energyBar.getMinimum())), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
