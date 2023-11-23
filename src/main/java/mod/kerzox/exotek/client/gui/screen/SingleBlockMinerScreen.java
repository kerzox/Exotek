package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.menu.SingleBlockMinerMenu;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.StartRecipePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class SingleBlockMinerScreen extends DefaultScreen<SingleBlockMinerMenu> {

    private EnergyBarComponent energyBar = new EnergyBarComponent(this, getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17);

    private ButtonComponent resetButton = new ButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            104, 59, 12, 12, 84, 217, 72, 217+12, Component.literal("Reset Button"), button -> reset());

    private ToggleButtonComponent startButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            118, 59, 12, 12, 12, 217, 12, 217+12, Component.literal("Start Button"), button -> toggleRunning());

    private ToggleButtonComponent radiusButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            90, 59, 12, 12, 96, 217, 96, 217+12, Component.literal("Radius Button"), button ->
            getMenu().getBlockEntity().setShowRadius(!getMenu().getBlockEntity().showRadius()));

    private void toggleRunning() {
        PacketHandler.sendToServer(new StartRecipePacket());
    }

    private void reset() {
        CompoundTag tag = new CompoundTag();
        tag.putString("reset", "reset");
        PacketHandler.sendToServer(new CompoundTagPacket(tag));
    }

    public SingleBlockMinerScreen(SingleBlockMinerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "single_block_miner.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        addRenderableWidget(startButton);
        addRenderableWidget(resetButton);
        addRenderableWidget(radiusButton);
        startButton.setState(getMenu().getBlockEntity().isRunning());
        radiusButton.setState(getMenu().getBlockEntity().showRadius());
    }

    @Override
    protected void menuTick() {
        getMenu().getUpdateTag();
        startButton.setState(getMenu().getBlockEntity().isRunning());

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {


    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        SingleBlockMinerEntity blockEntity = getMenu().getBlockEntity();
        graphics.pose().pushPose();
        graphics.pose().scale(0.8f, 0.8f, 0.8f);

        int moveY = 0;
        float scaled = .5f / 0.8f;

        FormattedCharSequence energy = Component.literal("Ores Found: " + blockEntity.getOreCount()).getVisualOrderText();
        FormattedCharSequence yLevel = Component.literal("At level: " + blockEntity.getyLevel()).getVisualOrderText();
        int xPos = this.getGuiLeft() + 25;
        int yPos = this.getGuiTop() + 22;
        graphics.pose().translate(xPos * scaled, yPos * scaled, 0);

        if (!blockEntity.isRunning()) {
            graphics.drawString(this.font, Component.literal("Machine disabled").getVisualOrderText(), xPos * scaled, moveY + yPos * scaled, 0xf84646, false);
            moveY += 12;
        }
        if (blockEntity.isStalled()) {
            graphics.drawString(this.font, Component.literal("Inventory is full").getVisualOrderText(), xPos * scaled, moveY +yPos * scaled, 0xf84646, false);
            moveY += 12;
        }
        if (blockEntity.isAtBottom()) {
            graphics.drawString(this.font, Component.literal("Finished running").getVisualOrderText(),   xPos * scaled, moveY + yPos * scaled, 0xf84646, false);
            moveY += 12;
        }

        graphics.drawString(this.font, energy, xPos * scaled, moveY + yPos * scaled, 0x46f89a, false);
        graphics.drawString(this.font, yLevel, xPos * scaled, moveY + 12 + yPos * scaled, 0x46f89a, false);
        if (blockEntity.getProgress() > 0) {
            double percentage =  ((double) blockEntity.getProgress() / blockEntity.getDuration()) * 100;
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
