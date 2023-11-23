package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.client.gui.menu.multiblock.FluidTankMultiblockMenu;
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

public class FluidTankMultiblockScreen extends DefaultScreen<FluidTankMultiblockMenu> {

    private TankComponent inputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultiblockManager().getTank(),
            45, 20, 98, 50, 158, 0, 158, 50, Component.literal("Input tank"));

    public FluidTankMultiblockScreen(FluidTankMultiblockMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "fluid_tank.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(inputTank);
    }

    @Override
    protected void menuTick() {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
