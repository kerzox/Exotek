package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.WashingPlantMenu;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
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
    //
    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);
    private RecipeProgressComponent progressBar = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 82, 48, 10, 14, 57, 48, 67, 48, Component.literal("Compressing Recipe Progress"), ProgressComponent.Direction.RIGHT);

    private TankComponent fluidTank = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSingleFluidTank(),
            71, 23, 18, 18, 110, 67, 110, 49);
    //    private ProgressComponent<WashingPlantMenu> fluidProgress = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 82, 48, 10, 14, 57, 48, 67, 48);
    private ButtonComponent emptyButton =
            new ButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                    54, 26, 12, 12, 0, 217, 0, 217 + 12, Component.literal("Empty Button"), button -> onEmptyPress());

    public WashingPlantScreen(WashingPlantMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "washing_plant.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(fluidTank);
        addRenderableWidget(energyBar);
        addRenderableWidget(progressBar);
        addRenderableWidget(emptyButton);
    }

    @Override
    protected void menuTick() {
        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            progressBar.updateWithDirection(totalDuration - duration, totalDuration, ProgressComponent.Direction.RIGHT);
        } else {
            progressBar.updateWithDirection(0, 0, ProgressComponent.Direction.RIGHT);
        }
    }


    private void onEmptyPress() {
        PacketHandler.sendToServer(new CompoundTagPacket("empty"));
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
