package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.menu.SolarPanelMenu;
import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import mod.kerzox.exotek.client.gui.menu.transfer.EnergyCableMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.DynamicMultiblockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class SolarPanelScreen extends DefaultScreen<SolarPanelMenu> {

    private ProgressComponent<EnergyCableMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 154, 17, 10, 54, 0, 65, 10, 65);

    public SolarPanelScreen(SolarPanelMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "solar_panel.png", false);
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        DynamicMultiblockEntity.Master master = getMenu().getBlockEntity().getMaster();
        energyBar.update(master.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0),
                master.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }


    @Override
    protected void menuTick() {
        DynamicMultiblockEntity.Master master = getMenu().getBlockEntity().getMaster();
        energyBar.update(master.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0),
                master.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }


    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }

    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (energyBar.isMouseOver(pMouseX, pMouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.literal("Energy:"+String.format("%, .0f", Double.parseDouble(String.valueOf(this.energyBar.getMinimum()))) + " FE")),
                    Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        DynamicMultiblockEntity.Master master = getMenu().getBlockEntity().getMaster();
        FormattedCharSequence title = Component.literal("Solar Panel").getVisualOrderText();
        graphics.drawString(this.font, title, this.getGuiLeft() + ((176/2) - this.font.width(title) / 2),
                this.getGuiTop() + 6, 4210752, false);
        graphics.pose().pushPose();
        graphics.pose().scale(0.9f, 0.9f, 0.9f);

        int spacer = 15;
        float scaled = .5f / 0.9f;

        FormattedCharSequence energy = Component.literal("Energy: " +
                abbreviateNumber(master.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0), true) + "/"
        + abbreviateNumber(master.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), true)).getVisualOrderText();

        int xPos = this.getGuiLeft() + 20;
        int yPos = this.getGuiTop() + 25;

        graphics.pose().translate(xPos * scaled, yPos * scaled, 0);

        graphics.drawString(this.font, energy, xPos * scaled, yPos * scaled, 0x46f89a, false);

        FormattedCharSequence energyPerTick = Component.literal("Generation: " + abbreviateNumber(master.getEntities().size() * 5, true) +"/tick").getVisualOrderText();

        graphics.drawString(this.font, energyPerTick, xPos * scaled,
                yPos * scaled + spacer, 0x46f89a, false);

        FormattedCharSequence size = Component.literal("Panels: " + master.getEntities().size()).getVisualOrderText();

        graphics.drawString(this.font, size, xPos * scaled,
                yPos * scaled + spacer + spacer, 0x46f89a, false);

        graphics.pose().popPose();
    }

    public static String abbreviateNumber(int number, boolean notation) {
        if (number < 1000) {
            return String.valueOf(number + (notation ? " FE" : ""));
        } else if (number < 1000000) {
            double abbreviatedValue = number / 1000.0;
            return String.format("%.1f" + (notation ? " kFE" : ""), abbreviatedValue);
        } else {
            double abbreviatedValue = number / 1000000.0;
            return String.format("%.1f" + (notation ? " mFE" : ""), abbreviatedValue);
        }
    }

}
