package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.SyncContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public abstract class DefaultMenu<T extends BasicBlockEntity> extends AbstractContainerMenu {

    protected final T blockEntity;
    protected final Inventory playerInventory;
    protected final Player player;

    protected DefaultMenu(@Nullable MenuType<?> pMenuType, int pContainerId, Inventory playerInventory, Player player, T blockEntity) {
        super(pMenuType, pContainerId);
        this.blockEntity = blockEntity;
        this.playerInventory = playerInventory;
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getPlayerInventory() {
        return playerInventory;
    }

    public T getBlockEntity() {
        return blockEntity;
    }

    public CompoundTag getUpdateTag() {
        PacketHandler.sendToServer(new SyncContainer());
        return getBlockEntity().getUpdateTag();
    }

    // inventory layout

    public NonNullList<Slot> getSlots() {
        return this.slots;
    }

    public void layoutPlayerInventorySlots(int leftCol, int topRow) {
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }

    public int addSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    public int addSlotBox(Container handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    public int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(new SlotComponent(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    public void addSlot(IItemHandler handler, int index, int x, int y) {
        addSlot(new SlotComponent(handler, index, x, y));
    }

    public int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    protected ItemStack moveToInventory(ItemStack stack) {
        if (!this.moveItemStackTo(stack, 0, 35, false)) {
            return ItemStack.EMPTY;
        }
        return stack;
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            // try to shift back into the inventory+hotbar
            if (slot instanceof SlotItemHandler) {
                if (!this.moveItemStackTo(itemstack1, 0, 35, false)) {
                    return ItemStack.EMPTY;
                }
            }

            // item was in the inventory
            if (pIndex <= 26) {
                if (!attemptToShiftIntoMenu(player, itemstack, itemstack1, pIndex).isEmpty()) { // try the implementations container
                    return itemstack;
                }
                if (!this.moveItemStackTo(itemstack1, 27, 35, false)) { // try the hotbar
                    return ItemStack.EMPTY;
                }
            } else { // item was in the hotbar
                if (!attemptToShiftIntoMenu(player, itemstack, itemstack1, pIndex).isEmpty()) { // try the implementations container
                    return itemstack;
                }
                if (!this.moveItemStackTo(itemstack1, 0, 26, false)) { // try the inventory
                    return ItemStack.EMPTY;
                }
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(pPlayer, itemstack1);
        }
        return itemstack;
    }

    protected abstract ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index);

}
