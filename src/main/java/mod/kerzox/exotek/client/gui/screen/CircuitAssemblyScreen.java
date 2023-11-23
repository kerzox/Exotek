package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
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

public class CircuitAssemblyScreen extends DefaultScreen<CircuitAssemblyMenu> {

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);
    private RecipeProgressComponent circuitProgress = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            85, 55, 20, 12, 64, 24, 64, 36, Component.literal("Progress"), ProgressComponent.Direction.RIGHT);
    private TankComponent tankComponent = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSingleFluidTank(), 71, 17, 18, 18, 110, 67, 110, 49);

    public CircuitAssemblyScreen(CircuitAssemblyMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "circuit_assembly.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(tankComponent);
        addRenderableWidget(circuitProgress);
        addRenderableWidget(energyBar);
    }


    @Override
    protected void menuTick() {
//        energyBar.updateWithDirection(
//                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
//                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
//
//
        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            circuitProgress.updateWithDirection(totalDuration - duration, totalDuration, ProgressComponent.Direction.RIGHT);
        } else {
            circuitProgress.updateWithDirection(0, 0, ProgressComponent.Direction.RIGHT);
        }

    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
