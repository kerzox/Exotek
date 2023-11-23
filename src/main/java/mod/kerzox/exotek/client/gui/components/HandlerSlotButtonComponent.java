package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.UpdateHandlerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class HandlerSlotButtonComponent extends ToggleButtonComponent {

    private int[] inputColour = new int[] {12, 112};
    private int[] outputColour = new int[] {24, 112};
    private int[] combinedColour = new int[] {0, 112};
    private int[] defaultColour = new int[] {36, 112};

    private Direction direction;
    private IStrictInventory serializer;

    public HandlerSlotButtonComponent(ICustomScreen screen, ResourceLocation texture, Direction direction, int x, int y, int width, int height, int u, int v, int u2, int v2, IPressable btn) {
        super(screen, texture, x, y, width, height, u, v, u2, v2, Component.literal("Handler Slot Button"), btn);
        setTextureOffset(u, v);
        this.direction = direction;
    }

    public HandlerSlotButtonComponent(ICustomScreen screen, ResourceLocation texture, Direction direction, int x, int y, int width, int height, int u, int v, int u2, int v2, IStrictInventory ser) {
        this(screen, texture, direction, x, y, width, height, u, v, u2, v2, button1 -> word());
        this.serializer = ser;
    }

    private static void word() {
        // nothing
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return true;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        HandlerSlotButtonComponent.Mode currentMode = getMode();
        if (button == 0) {
            if (currentMode == HandlerSlotButtonComponent.Mode.NONE) {
                serializer.addInput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.INPUT) {
                serializer.removeInputs(direction);
                serializer.addOutput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.OUTPUT) {
                serializer.addOutput(direction);
                serializer.addInput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.UNIVERSAL) {
                serializer.removeInputs(direction);
                serializer.removeOutputs(direction);
            }
        } else {
            if (currentMode == HandlerSlotButtonComponent.Mode.NONE) {
                serializer.addOutput(direction);
                serializer.addInput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.INPUT) {
                serializer.removeInputs(direction);
                serializer.removeOutputs(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.OUTPUT) {
                serializer.removeOutputs(direction);
                serializer.addInput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.UNIVERSAL) {
                serializer.removeInputs(direction);
                serializer.addOutput(direction);
            }
        }
        CompoundTag tag = getScreen().getMenu().getUpdateTag();
        PacketHandler.sendToServer(new UpdateHandlerPacket(tag));
    }

    public enum Mode {
        NONE,
        INPUT,
        OUTPUT,
        UNIVERSAL;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    protected Mode mode = Mode.NONE;

    @Override
    protected void onHover(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.literal("Mode: " + getMode().toString()), Component.literal("Direction: " + direction)), Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
    }

    public void update(HashSet<Direction> inputs, HashSet<Direction> outputs, Direction direction) {
        if (inputs.contains(direction) && outputs.contains(direction)) {
            setTextureOffset(combinedColour[0], combinedColour[1]);
            setMode(Mode.UNIVERSAL);
        }
        else if (inputs.contains(direction)) {
            setTextureOffset(inputColour[0], inputColour[1]);
            setMode(Mode.INPUT);
        }
        else if (outputs.contains(direction)) {
            setTextureOffset(outputColour[0], outputColour[1]);
            setMode(Mode.OUTPUT);
        }
        else {
            setTextureOffset(defaultColour[0], defaultColour[1]);
            setMode(Mode.NONE);
        }
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.draw(graphics);
    }

}
