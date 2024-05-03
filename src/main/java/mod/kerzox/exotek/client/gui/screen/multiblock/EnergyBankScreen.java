package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
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

    private EnergyBarComponent energyBar = EnergyBarComponent.large(
                    this,
                    this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(),
                    28, 17, ProgressComponent.Direction.UP);

    public EnergyBankScreen(EnergyBankMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "energy_bank.png", false);
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
