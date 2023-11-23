package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.CompressorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class CompressorScreen extends DefaultScreen<CompressorMenu> {

    private EnergyBarComponent energyBar = new EnergyBarComponent(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17);
    private RecipeProgressComponent compressingBar = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 82, 36, 12, 12, 40, 92, 40, 104,Component.literal("Compressing Recipe Progress"), ProgressComponent.Direction.RIGHT);


    public CompressorScreen(CompressorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "compressor.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        addRenderableWidget(compressingBar);
    }

    @Override
    protected void menuTick() {
//        energyBar.updateWithDirection(
//                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
//                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            compressingBar.updateWithDirection(totalDuration - duration, totalDuration, ProgressComponent.Direction.RIGHT);
        } else {
            compressingBar.updateWithDirection(0, 0, ProgressComponent.Direction.RIGHT);
        }
    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
