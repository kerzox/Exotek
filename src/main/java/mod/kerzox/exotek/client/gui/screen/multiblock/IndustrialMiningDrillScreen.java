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
import mod.kerzox.exotek.common.blockentities.multiblock.manager.MinerManager;
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

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,
            this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);

    private ButtonComponent modeButton = new ButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            101, 59, 25, 12, 231, 232, 231, 232+12, Component.literal("Mode Button"), button -> mode());

    private ButtonComponent resetButton = new ButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            142, 59, 12, 12, 84, 217, 72, 217+12, Component.literal("Reset Button"), button -> reset());

    private ToggleButtonComponent startButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            156, 59, 12, 12, 12, 217, 12, 217+12, Component.literal("Start Button"), button -> toggleRunning());

    private ToggleButtonComponent radiusButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            128, 59, 12, 12, 96, 217, 96, 217+12, Component.literal("Radius Button"), button ->
            toggleShowRadius());

    public IndustrialMiningDrillScreen(IndustrialMiningDrillMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "industrial_mining.png", false);
    }

    private void toggleShowRadius() {
        CompoundTag tag = new CompoundTag();
        tag.putString("showRadius", "showRadius");
        PacketHandler.sendToServer(new CompoundTagPacket(tag));
    }

    private void toggleRunning() {
        CompoundTag tag = new CompoundTag();
        tag.putString("start", "start");
        PacketHandler.sendToServer(new CompoundTagPacket(tag));
    }

    private void reset() {
        CompoundTag tag = new CompoundTag();
        tag.putString("reset", "reset");
        PacketHandler.sendToServer(new CompoundTagPacket(tag));
    }

    private void mode() {
        CompoundTag tag = new CompoundTag();
        int mode = this.menu.getBlockEntity().getMultiblockManager().getMode();
        if (mode == 0) tag.putInt("mode", 1);
        else tag.putInt("mode", 0);
        PacketHandler.sendToServer(new CompoundTagPacket(tag));
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        addRenderableWidget(startButton);
        addRenderableWidget(resetButton);
        addRenderableWidget(radiusButton);
        addRenderableWidget(modeButton);
        //startButton.setState(getMenu().getBlockEntity().isRunning());
        //radiusButton.setState(getMenu().getBlockEntity().showRadius());
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        MinerManager manager = getMenu().getBlockEntity().getMultiblockManager();
        graphics.pose().pushPose();
        graphics.pose().scale(0.8f, 0.8f, 0.8f);

        int moveY = 0;
        float scaled = .5f / 0.8f;

        FormattedCharSequence energy = Component.literal("Ores Found: " + manager.getOreCount()).getVisualOrderText();
        FormattedCharSequence yLevel = Component.literal("At level: " + manager.getYLevel()).getVisualOrderText();
        int xPos = this.getGuiLeft() + 20;
        int yPos = this.getGuiTop() + 22;
        graphics.pose().translate(xPos * scaled, yPos * scaled, 0);

        if (!manager.canWork()) {
            graphics.drawString(this.font, Component.literal("Machine disabled").getVisualOrderText(), xPos * scaled, moveY + yPos * scaled, 0xf84646, false);
            moveY += 12;
        }
        if (manager.isStalled()) {
            graphics.drawString(this.font, Component.literal("Inventory is full").getVisualOrderText(), xPos * scaled, moveY +yPos * scaled, 0xf84646, false);
            moveY += 12;
        }
        if (manager.isAtBottom()) {
            graphics.drawString(this.font, Component.literal("Finished running").getVisualOrderText(),   xPos * scaled, moveY + yPos * scaled, 0xf84646, false);
            moveY += 12;
        }

        if (manager.getMode() == 0) {
            FormattedCharSequence mode = Component.literal("Current Mode: In World Ores").getVisualOrderText();
            graphics.drawString(this.font, mode, xPos * scaled, moveY + yPos * scaled, 0x46f89a, false);
            moveY += 12;
            graphics.drawString(this.font, energy, xPos * scaled, moveY + yPos * scaled, 0x46f89a, false);
            graphics.drawString(this.font, yLevel, xPos * scaled, moveY + 12 + yPos * scaled, 0x46f89a, false);

        }

        if (manager.getMode() == 1) {
            FormattedCharSequence mode = Component.literal("Current Mode: Chunk Deposits").getVisualOrderText();
            graphics.drawString(this.font, mode, xPos * scaled, moveY + yPos * scaled, 0x46f89a, false);
        }


        if (manager.getProgress() > 0) {
            double percentage =  ((double) manager.getProgress() / manager.getDuration()) * 100;
            int totalDots = (int) (percentage / 10) / 2;
            StringBuilder dots = new StringBuilder();
            for (int i = 0; i < totalDots; i++) {
                dots.append("-");
            }
            graphics.drawString(this.font, Component.literal("Mining: " + dots).getVisualOrderText(), xPos * scaled, moveY + 12 + 12 + yPos * scaled, 0x46f89a, false);
        }
        graphics.pose().popPose();
    }
}
