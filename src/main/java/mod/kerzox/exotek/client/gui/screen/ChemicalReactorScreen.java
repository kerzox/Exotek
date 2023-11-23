package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.ChemicalReactorMenu;
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

    private EnergyBarComponent energyBar = new EnergyBarComponent(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17);
    private RecipeProgressComponent chemProgress = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            71, 49, 14, 19, 85, 24, 99, 24,
            Component.literal("Chemical Progress"), ProgressComponent.Direction.RIGHT);

    private TankComponent inputTank1 = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(0), 28, 22, 18, 18, 110, 67, 110, 49);
    private TankComponent inputTank2 = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(1),28, 22+18, 18, 18, 110, 67, 110, 49);
    private TankComponent inputTank3 = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(2),46, 46, 18, 18, 110, 67, 110, 49);

    private TankComponent outputTank1 = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getOutputHandler().getStorageTank(0),113, 55, 18, 18, 110, 67, 110, 49);
    private TankComponent outputTank2 = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getOutputHandler().getStorageTank(1), 113+18, 55, 18, 18, 110, 67, 110, 49);


    public ChemicalReactorScreen(ChemicalReactorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "chemical_reactor.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        addRenderableWidget(chemProgress);
        addRenderableWidget(inputTank1);
        addRenderableWidget(inputTank2);
        addRenderableWidget(inputTank3);
        addRenderableWidget(outputTank1);
        addRenderableWidget(outputTank2);

        getMenu().getUpdateTag();

    }


    @Override
    protected void menuTick() {
        getMenu().getUpdateTag();
//        energyBar.updateWithDirection(
//                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
//                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
//
//
        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            chemProgress.updateWithDirection(totalDuration - duration, totalDuration, ProgressComponent.Direction.UP);
        } else {
            chemProgress.updateWithDirection(0, 0, ProgressComponent.Direction.UP);
        }

    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
