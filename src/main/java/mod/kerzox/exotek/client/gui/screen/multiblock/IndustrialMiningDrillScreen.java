package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.menu.multiblock.IndustrialMiningDrillMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MinerEntity;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.StartRecipePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

public class IndustrialMiningDrillScreen extends DefaultScreen<IndustrialMiningDrillMenu> {

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);

    private ButtonComponent resetButton = new ButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            142, 59, 12, 12, 84, 217, 72, 217+12, Component.literal("Reset Button"), button -> reset());

    private ToggleButtonComponent startButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            156, 59, 12, 12, 12, 217, 12, 217+12, Component.literal("Start Button"), button -> toggleRunning());

    private ToggleButtonComponent radiusButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            128, 59, 12, 12, 96, 217, 96, 217+12, Component.literal("Radius Button"), button -> System.out.println("word"));

    public IndustrialMiningDrillScreen(IndustrialMiningDrillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "industrial_mining.png", false);
    }

    private void toggleRunning() {
        PacketHandler.sendToServer(new StartRecipePacket());
    }

    private void reset() {
        CompoundTag tag = new CompoundTag();
        tag.putString("reset", "reset");
        PacketHandler.sendToServer(new CompoundTagPacket(tag));
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        addRenderableWidget(energyBar);
        addRenderableWidget(startButton);
        addRenderableWidget(resetButton);
        addRenderableWidget(radiusButton);
        //startButton.setState(getMenu().getBlockEntity().isRunning());
        //radiusButton.setState(getMenu().getBlockEntity().showRadius());
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
