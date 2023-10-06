package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.HashSet;

public class HandlerSlotButtonComponent<T extends DefaultMenu<?>> extends ButtonComponent<T> {

    private int[] inputColour = new int[] {16, 88};
    private int[] outputColour = new int[] {32, 88};
    private int[] combinedColour = new int[] {0, 88};
    private int[] defaultColour = new int[] {48, 88};

    public enum Mode {
        NONE,
        INPUT,
        OUTPUT,
        UNIVERSAL;
    }

    protected Mode mode = Mode.NONE;

    public HandlerSlotButtonComponent(DefaultScreen<T> screen, ResourceLocation texture, int x, int y, int width, int height, int u, int v, IPressable btn) {
        super(screen, texture, x, y, width, height, u, v, u, v, btn);
        setTextureOffset(u, v);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.isMouseOver(mouseX, mouseY)) {
            return false;
        } else {
            this.button.onPress(this);
            this.screen.getMinecraft().player.playSound(SoundEvents.UI_BUTTON_CLICK.value(),
                    this.screen.getMinecraft().options.getSoundSourceVolume(SoundSource.MASTER), 1.0F);
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
