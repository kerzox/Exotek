package mod.kerzox.exotek.client.gui.menu.container;

import mod.kerzox.exotek.common.capability.IStrictCombinedItemHandler;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ItemStackCraftingContainer implements CraftingContainer {

    private final IStrictCombinedItemHandler inv;
    private final int width;
    private final int height;
    private final AbstractContainerMenu menu;

    public ItemStackCraftingContainer(IStrictCombinedItemHandler itemHandlerModifiable, int width, int height, AbstractContainerMenu menu) {
        this.inv = itemHandlerModifiable;
        this.width = width;
        this.height = height;
        this.menu = menu;
    }

    @Override
    public int getContainerSize() {
        return inv.getSlots();
    }

    @Override
    public boolean isEmpty() {
        for(int i = 0; i < inv.getSlots(); i++) {
            if (!inv.getStackInSlot(i).isEmpty()) return false;
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int index) {
        return inv.getStackInSlot(index);
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int index) {
        ItemStack s = getItem(index);
        if(s.isEmpty()) return ItemStack.EMPTY;
        setItem(index, ItemStack.EMPTY);
        return s;
    }

    @Override
    public @NotNull ItemStack removeItem(int index, int count) {
        ItemStack stack = inv.getStackInSlot(index);

        if (!stack.isEmpty()) {
            this.menu.slotsChanged(this);
        }

        return stack.isEmpty() ? ItemStack.EMPTY : stack.split(count);
    }

    @Override
    public void setItem(int index, @Nonnull ItemStack stack) {
        inv.setStackInSlot(index, stack);
        this.menu.slotsChanged(this);
    }

    @Override
    public void clearContent() {
        for(int i = 0; i < inv.getSlots(); i++) {
            inv.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void setChanged() {
    }

    public boolean stillValid(Player p_287774_) {
        return true;
    }

    public int getHeight() {
        return this.height;
    }

    public int getWidth() {
        return this.width;
    }

    public List<ItemStack> getItems() {
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inv.getSlots(); i++) {
            items.add(inv.getStackInSlot(i).copy());
        }
        return items;
    }

    public void fillStackedContents(StackedContents p_287653_) {
        for(ItemStack itemstack : this.getItems()) {
            p_287653_.accountSimpleStack(itemstack);
        }

    }

    public IItemHandlerModifiable getItemHandlerModifiable() {
        return inv;
    }
}
