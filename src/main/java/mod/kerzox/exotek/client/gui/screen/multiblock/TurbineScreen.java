package mod.kerzox.exotek.client.gui.screen.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.BoilerMenu;
import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.client.gui.menu.ManufactoryMenu;
import mod.kerzox.exotek.client.gui.menu.TurbineMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.BoilerManager;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.StartRecipePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

public class TurbineScreen extends DefaultScreen<TurbineMenu> {

    private TankComponent inputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultiblockManager().getMultifluidTank().getInputHandler().getStorageTank(0),
            91+20, 21, 18, 50, 92, 69, 0, 15, Component.literal("Input Tank"));
    private TankComponent outputTank = new TankComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getMultiblockManager().getMultifluidTank().getOutputHandler().getStorageTank(0),
            91+20+18, 21, 18, 50, 92, 69, 0, 15, Component.literal("Output Tank"));
    private ToggleButtonComponent startButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            91+20+18+18+4, 59, 12, 12, 12, 217, 12, 217+12, Component.literal("Start Button"), button -> startRecipe());

    private void startRecipe() {
        PacketHandler.sendToServer(new StartRecipePacket());
    }

    public TurbineScreen(TurbineMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "burnable_generator.png", false);
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(inputTank);
        addRenderableWidget(outputTank);
        addRenderableWidget(startButton);
        startButton.setState(getMenu().getBlockEntity().getMultiblockManager().isRunning());
    }

    @Override
    protected void menuTick() {
        getMenu().getUpdateTag();
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {


    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        int rotation = getMenu().getBlockEntity().getMultiblockManager().getRotationSpeed();
        FormattedCharSequence energyPerTick = Component.literal("Rotation: " + rotation).getVisualOrderText();
        graphics.drawString(this.font, energyPerTick, this.getGuiLeft() + 40,
                this.getGuiTop() + 30, 0xff0000, false);
        int burn = getMenu().getBlockEntity().getMultiblockManager().getBurnRemaining();
        FormattedCharSequence burnt = Component.literal("BurnTime: " + burn).getVisualOrderText();
        graphics.drawString(this.font, burnt, this.getGuiLeft() + 40,
                this.getGuiTop() + 30 + 12, 0xff0000, false);
        FormattedCharSequence burnt2 = Component.literal("PowerGen: " + Math.round(0.005 * Math.pow(rotation, 2) + 0)).getVisualOrderText();
        graphics.drawString(this.font, burnt2, this.getGuiLeft() + 40,
                this.getGuiTop() + 30 + 12+12, 0xff0000, false);
    }
}
