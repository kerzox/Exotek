package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.client.gui.components.ContainerSlotComponent;
import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.components.UpgradeSlotComponent;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.upgrade.IUpgradableMachine;
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

    public void addUpgradeSlots() {
        blockEntity.getCapability(ExotekCapabilities.UPGRADABLE_MACHINE).ifPresent(cap -> {
            addSlot(cap, 0, 176 + 42, 5 + 6);
            addSlot(cap, 1, 176 + 42, 5 + 6 + 18 + 11);
        });
    }

    public boolean noUpgrades() {
        return !blockEntity.getCapability(ExotekCapabilities.UPGRADABLE_MACHINE).isPresent();
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

    public int addContainerSlotRange(Container handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0; i < amount; i++) {
            addSlot(handler, index, x, y);
            x += dx;
            index++;
        }
        return index;
    }

    public int addContainerSlotBox(Container handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0; j < verAmount; j++) {
            index = addContainerSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
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

    public int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx, boolean hidden) {
        for (int i = 0; i < amount; i++) {
            addSlot(handler, index, x, y, hidden);
            x += dx;
            index++;
        }
        return index;
    }

    public void addSlot(IItemHandler handler, int index, int x, int y, boolean hidden) {
        addSlot(new SlotComponent(handler, index, x, y, hidden));
    }

    public void addSlot(Container handler, int index, int x, int y) {
        addSlot(new ContainerSlotComponent(handler, index, x, y));
    }

    public void addSlot(IItemHandler handler, int index, int x, int y) {
        addSlot(new SlotComponent(handler, index, x, y));
    }

    public void addSlot(IUpgradableMachine handler, int index, int x, int y) {
        addSlot(new UpgradeSlotComponent(handler.getInventory(), index, x, y));
    }

    public int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy, boolean hidden) {
        for (int j = 0; j < verAmount; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx, hidden);
            y += dy;
        }
        return index;
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

            if (pIndex <= 36) { // inventory slots 0 - 26 | 27 - 35 hotbar
                if (!trySlotShiftClick(player, itemstack, itemstack1, pIndex).isEmpty()) { // try menu implementation
                    if (pIndex <= 27) { // inside the inventory we want to move to hotbar
                        if (!this.moveItemStackTo(itemstack1, 27, 36, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else { // inside the hotbar we want to move to inventory
                        if (!this.moveItemStackTo(itemstack1, 0, 27, false)) {
                            return ItemStack.EMPTY;
                        }
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            } else { // inside slots that are not player slots (i.e. item capabilities etc.)
                // move straight into the inventory+hotbar combined
                if (!this.moveItemStackTo(itemstack1, 0, 36, true)) {
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

    protected abstract ItemStack trySlotShiftClick(Player player, ItemStack copied, ItemStack realStack, int index);

}
