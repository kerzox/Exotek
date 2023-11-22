package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.HashSet;
import java.util.Locale;

public class HandlerSlotButtonComponent<T extends DefaultMenu<?>> extends ToggleButtonComponent<T> {

    private int[] inputColour = new int[] {12, 112};
    private int[] outputColour = new int[] {24, 112};
    private int[] combinedColour = new int[] {0, 112};
    private int[] defaultColour = new int[] {36, 112};

    public HandlerSlotButtonComponent(ICustomScreen screen, ResourceLocation texture, int x, int y, int width, int height, int u, int v, int u2, int v2, IPressable btn) {
        super(screen, texture, x, y, width, height, u, v, u2, v2, btn);
        setTextureOffset(u, v);
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            this.button.onPress(this);
            Minecraft.getInstance().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(),
                    Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER), 1.0F);
            return true;
        }
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
        this.draw(graphics, this.x, this.y, this.width, this.height);
    }

}
