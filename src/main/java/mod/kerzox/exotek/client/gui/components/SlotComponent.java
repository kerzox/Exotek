package mod.kerzox.exotek.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.util.ColourHex;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class SlotComponent extends SlotItemHandler {

    protected int x1;
    protected int y1;
    protected int width = 16;
    protected int height = 16;

    private boolean blockPlace = false;
    private boolean blockPickup = false;
    private boolean active = true;

    public SlotComponent(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }

    public SlotComponent(IItemHandler itemHandler, int index, int xPosition, int yPosition, boolean hidden) {
        super(itemHandler, index, xPosition, yPosition);
        blockPlace = hidden;
        blockPickup = hidden;
        setActive(!hidden);
    }

    public boolean isMouseOver(double pMouseX, double pMouseY) {
        return ((pMouseX > this.x1 - 2) && (pMouseX < this.x1 + this.width + 1)) &&
                ((pMouseY > this.y1 - 2) && (pMouseY < this.y1 + this.height + 1));
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        if (blockPlace) return false;
        return super.mayPlace(stack);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        if (blockPickup) return false;
        return super.mayPickup(playerIn);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void blockPickup(boolean blockPickup) {
        this.blockPickup = blockPickup;
    }

    public void blockPlace(boolean blockPlace) {
        this.blockPlace = blockPlace;
    }

    public void highlightOnHover(GuiGraphics graphics, int mouseX, int mouseY) {
        RenderSystem.enableDepthTest();
        if (isMouseOver(mouseX, mouseY)) {
            if (getItemHandler() instanceof ItemStackInventory.PlayerWrapper inventory) {
                IItemHandlerModifiable handler = inventory.getHandlerFromIndex(this.getSlotIndex());
                if (handler instanceof ItemStackInventory.InputHandler) {
                    graphics.fill(x1, y1, x1 + width, y1 + height, ColourHex.INPUT_BLUE.changeOpacity(45));
                } else {
                    graphics.fill(x1, y1, x1 + width, y1 + height, ColourHex.OUTPUT_RED.changeOpacity(45));
                }
            }
        }
        RenderSystem.disableDepthTest();
    }



    public void setPositionProper(int guiLeft, int guiTop) {
        x1 = guiLeft + this.x;
        y1 = guiTop + this.y;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
