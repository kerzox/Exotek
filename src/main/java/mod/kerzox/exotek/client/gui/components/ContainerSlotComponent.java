package mod.kerzox.exotek.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerSlotComponent extends Slot {

    protected int x1;
    protected int y1;
    protected int width = 16;
    protected int height = 16;

    private boolean blockPlace = false;
    private boolean blockPickup = false;
    private boolean active = true;

    public ContainerSlotComponent(Container p_40223_, int p_40224_, int p_40225_, int p_40226_) {
        super(p_40223_, p_40224_, p_40225_, p_40226_);
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

    public void setPositionProper(int guiLeft, int guiTop) {
        x1 = guiLeft + this.x;
        y1 = guiTop + this.y;
    }

    public void setActive(boolean b) {
        this.active = b;
    }
}
