package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.common.blockentities.machine.CentrifugeEntity;
import mod.kerzox.exotek.common.blockentities.storage.StorageCrateBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.util.MachineTier;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StorageCrateMenu extends DefaultMenu<StorageCrateBlockEntity> {

    private MachineTier tier;

    private HashMap<MachineTier, List<SlotComponent>> pageSlots = new HashMap<>(){{
        put(MachineTier.DEFAULT, new ArrayList<>());
        put(MachineTier.BASIC, new ArrayList<>());
        put(MachineTier.ADVANCED, new ArrayList<>());
        put(MachineTier.SUPERIOR, new ArrayList<>());
    }};

    public static int PAGE_TWO_INDEX = 54;
    public static int PAGE_THREE_INDEX = 108;
    public static int PAGE_FOUR_INDEX = 162;

    public StorageCrateMenu(int pContainerId, Inventory playerInventory, Player player, StorageCrateBlockEntity blockEntity) {
        super(ExotekRegistry.Menus.STORAGE_CRATE_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 146);
        // add item slots from capability

        tier = blockEntity.getTier();

        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            addSlotBox(cap, 0, 8, 18, 9, 18, 6, 18, false);
            switch (blockEntity.getTier()) {
                case BASIC -> {
                    addSlotBox(cap, PAGE_TWO_INDEX, 8, 18, 9, 18, 6, 18, true);
                }
                case ADVANCED -> {
                    addSlotBox(cap, PAGE_TWO_INDEX, 8, 18, 9, 18, 6, 18, true);
                    addSlotBox(cap, PAGE_THREE_INDEX, 8, 18, 9, 18, 6, 18, true);
                }
                case SUPERIOR -> {
                    addSlotBox(cap, PAGE_TWO_INDEX, 8, 18, 9, 18, 6, 18, true);
                    addSlotBox(cap, PAGE_THREE_INDEX, 8, 18, 9, 18, 6, 18, true);
                    addSlotBox(cap, PAGE_FOUR_INDEX, 8, 18, 9, 18, 6, 18, true);
                }

            }
        });

    }

    public HashMap<MachineTier, List<SlotComponent>> getPageSlots() {
        return pageSlots;
    }

    @Override
    public void addSlot(IItemHandler handler, int index, int x, int y, boolean hidden) {
        SlotComponent slotComponent = new SlotComponent(handler, index, x, y, hidden);
        super.addSlot(slotComponent);
        if (index < PAGE_TWO_INDEX) {
            pageSlots.get(MachineTier.DEFAULT).add(slotComponent);
        }
        else if (index < PAGE_THREE_INDEX) {
            pageSlots.get(MachineTier.BASIC).add(slotComponent);
        }
        else if (index < PAGE_FOUR_INDEX) {
            pageSlots.get(MachineTier.ADVANCED).add(slotComponent);
        }
        else {
            pageSlots.get(MachineTier.SUPERIOR).add(slotComponent);
        }
    }

    public MachineTier getTier() {
        return tier;
    }

    @Override
    protected ItemStack trySlotShiftClick(Player player, ItemStack returnStack, ItemStack copied, int index) {
        int baseIndex = slots.indexOf(pageSlots.get(MachineTier.DEFAULT).get(0));
        if (getTier() == MachineTier.DEFAULT) {
            if (!this.moveItemStackTo(copied,
                    baseIndex,
                    baseIndex + PAGE_TWO_INDEX, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (getTier() == MachineTier.BASIC) {
            if (!this.moveItemStackTo(copied,
                    baseIndex,
                    baseIndex + PAGE_THREE_INDEX, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (getTier() == MachineTier.ADVANCED) {
            if (!this.moveItemStackTo(copied,
                    baseIndex,
                    baseIndex + PAGE_FOUR_INDEX, false)) {
                return ItemStack.EMPTY;
            }
        }
        else if (getTier() == MachineTier.SUPERIOR) {
            if (!this.moveItemStackTo(copied,
                    baseIndex,
                    baseIndex + PAGE_FOUR_INDEX + 54, false)) {
                return ItemStack.EMPTY;
            }
        }

        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
