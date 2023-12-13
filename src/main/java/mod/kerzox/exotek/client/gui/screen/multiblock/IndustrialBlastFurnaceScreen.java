package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.IndustrialBlastFurnaceMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.registry.ConfigConsts;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;

public class IndustrialBlastFurnaceScreen extends DefaultScreen<IndustrialBlastFurnaceMenu> {

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);

    private RecipeProgressComponent cookingProgress = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            86, 36, 28, 12, 36, 32, 36, 20, Component.literal("Cooking Progress"), ProgressComponent.Direction.RIGHT);

    private ProgressComponent heatProgress = new ProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            33, 25, 36, 6, 196, 100, 196, 106, Component.literal("Heat"), ProgressComponent.Direction.RIGHT) {
        @Override
        protected List<Component> getComponents() {
            return List.of(Component.literal("Heat: " + getMinimum()));
        }
    };

    private TankComponent fluidTank = new TankComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultiblockManager().getFluidTank(), 0,
            33, 33, 18, 18, 110, 67, 110, 49);

    public IndustrialBlastFurnaceScreen(IndustrialBlastFurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "industrial_blast_furnace_gui.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(cookingProgress);
        addRenderableWidget(fluidTank);
        addRenderableWidget(energyBar);
        addRenderableWidget(heatProgress);
    }

    @Override
    protected void menuTick() {
        int totalDuration = getMenu().getBlockEntity().getMultiblockManager().getMaxDuration();
        int duration = getMenu().getBlockEntity().getMultiblockManager().getDuration();

        if (duration > 0) {
            cookingProgress.update(totalDuration - duration, totalDuration);
        } else {
            cookingProgress.update(0, 0);
        }

        int heat = getMenu().getBlockEntity().getMultiblockManager().getHeat();

        if (heat > 0) {
            heatProgress.update(heat, ConfigConsts.INDUSTRIAL_BLAST_FURNACE_MAX_HEAT);
        } else {
            heatProgress.update(0, 0);
        }
    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
