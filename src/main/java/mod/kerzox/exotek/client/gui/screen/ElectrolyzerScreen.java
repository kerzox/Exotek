package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class ElectrolyzerScreen extends DefaultScreen<ElectrolyzerMenu> {
//
private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);

    private TankComponent inputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultifluidTank(), 0,
            33, 19, 18, 50, 92, 69, 0, 15);
    private TankComponent outputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultifluidTank(), 1,
            88, 16, 18, 34,  110, 85, 18, 31);
    private TankComponent outputTank2 = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultifluidTank(), 2,
            124, 16, 18, 34, 110, 85, 18, 31);

    public ElectrolyzerScreen(ElectrolyzerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "electrolyzer.png");
    }



    @Override
    protected void onOpen() {
        addRenderableWidget(inputTank);
        addRenderableWidget(outputTank);
        addRenderableWidget(outputTank2);
        addRenderableWidget(energyBar);
    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
