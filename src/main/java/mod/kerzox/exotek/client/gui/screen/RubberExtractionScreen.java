package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.RubberExtractionMenu;
import mod.kerzox.exotek.common.blockentities.machine.RubberExtractionEntity;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.event.TickUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class RubberExtractionScreen extends DefaultScreen<RubberExtractionMenu> {

//    private ProgressComponent<RubberExtractionMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
//    private ProgressComponent<RubberExtractionMenu> fluidProgress = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 83, 51, 10, 14, 57, 48, 67, 48);

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);
    private RecipeProgressComponent manuBar = new RecipeProgressComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            150, 34, 10, 14, 57, 48, 67, 48, Component.literal("Processing"), ProgressComponent.Direction.RIGHT) {
        @Override
        protected void onHover(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
            if (getMinimum() != 0) {
                drawToolTips(graphics, mouseX, mouseY, Component.literal("Time left on the log: " + TickUtils.readableTime((int) (getMaximum() - getMinimum()))));
            }
        }
    };
    private TankComponent inputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            (IFluidHandler) getMenu().getBlockEntity().getHolderFrom(1), 0,
            39, 17, 98, 50, 158, 0, 158, 50);
    public RubberExtractionScreen(RubberExtractionMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "rubber_extraction.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        addRenderableWidget(manuBar);
        addRenderableWidget(inputTank);
    }

    @Override
    protected void menuTick() {
        int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
        int duration = getMenu().getUpdateTag().getInt("duration");

        if (duration > 0) {
            manuBar.updateWithDirection(totalDuration - duration, totalDuration, ProgressComponent.Direction.RIGHT);
        } else {
            manuBar.updateWithDirection(0, 0, ProgressComponent.Direction.RIGHT);
        }
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }

}
