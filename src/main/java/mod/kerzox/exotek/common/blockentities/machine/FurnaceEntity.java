package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.Config;
import mod.kerzox.exotek.client.gui.menu.FurnaceMenu;
import mod.kerzox.exotek.common.blockentities.TieredRecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.IStrictCombinedItemHandler;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.capability.upgrade.UpgradableMachineHandler;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.common.util.MachineTier;
import mod.kerzox.exotek.registry.ConfigConsts;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FurnaceEntity extends TieredRecipeWorkingBlockEntity<SmeltingRecipe> implements IServerTickable, ITieredMachine {

    private boolean itemChanged = false;
    private CompoundTag tag = new CompoundTag();
    private BlockState state;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };

    private final UpgradableMachineHandler upgradableMachineHandler = new UpgradableMachineHandler(3) {

        // Move this to tags
        @Override
        protected boolean isUpgradeValid(int slot, ItemStack stack) {
            if (stack.getItem() == ExotekRegistry.Items.SPEED_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == ExotekRegistry.Items.ENERGY_UPGRADE_ITEM.get()) return true;
            else return stack.getItem() == ExotekRegistry.Items.ANCHOR_UPGRADE_ITEM.get();
        }


    };
    private ItemStackInventory itemHandler = createInventory(1, 1);

    private ItemStackInventory createInventory(int slots, int slots2) {
        return new ItemStackInventory(slots, slots2) {

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                // because we don't use either inventories for this call its safe
                return isValidExtract(FurnaceEntity.this.itemHandler, FurnaceEntity.this.itemHandler, stack);
            }

            @Override
            protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
                if (!(handler instanceof OutputHandler)) { // ignore output handler
                    if (!level.isClientSide)  {
                        itemChanged = true;
                    }
                    if (doRecipeCheck(recipeInventoryWrapper[slot]).isEmpty() && !level.isClientSide) {
                        finishRecipe(slot);
                    }
                }
            }
        };
    }

    protected boolean running;
    protected MachineTier tier;
    protected boolean sorting;


    // add modifiers on tiers or something
    private int feTick = ConfigConsts.FURNACE_FE_USAGE_PER_TICK;

    public FurnaceEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.FURNACE_ENTITY.get(), RecipeType.SMELTING, pos, state);
        setRecipeInventoryWrapper(new RecipeInventoryWrapper[]{ new RecipeInventoryWrapper(this.itemHandler) });
        this.forceConditionCheck = true;
        addCapabilities(itemHandler, energyHandler, upgradableMachineHandler);
    }

    public UpgradableMachineHandler getUpgradableMachineHandler() {
        return upgradableMachineHandler;
    }

    // check if the item we are extracting has a valid recipe in our furnace

    @Override
    protected boolean isValidExtract(IStrictCombinedItemHandler strictHandler, IItemHandler cap, ItemStack stackInSlot) {
        ItemStackHandler stackHandler = new ItemStackHandler(1);
        stackHandler.setStackInSlot(0, stackInSlot);
        return doRecipeCheck(new RecipeInventoryWrapper(stackHandler)).isPresent();
    }

    public boolean isSorting() {
        return sorting;
    }

    @Override
    public void setSorting(boolean sorting) {
        this.sorting = sorting;
    }

    private void attemptSort(int slot) {
        if (!sorting) return;
        itemChanged = false;
        int remaining = itemHandler.getStackFromInputHandler(slot).getCount() / (itemHandler.getInputHandler().getSlots());

        int j = slot;
        int counter = 0;
        while (!itemHandler.getStackFromInputHandler(slot).isEmpty() && itemHandler.getStackFromInputHandler(slot).getCount() >= remaining) {
            if (j != slot) {
                ItemStack sim = itemHandler.getInputHandler().insertItemNoUpdate(j, itemHandler.getStackFromInputHandler(slot).copyWithCount(remaining), true);
                if ((itemHandler.getStackFromInputHandler(j).getCount() + 1) < itemHandler.getStackFromInputHandler(slot).getCount()) {
                    ItemStack ret2 = itemHandler.getInputHandler().insertItemNoUpdate(j, itemHandler.getStackFromInputHandler(slot).copyWithCount(1), false);
                    if (ret2.isEmpty()) itemHandler.getInputHandler().extractItemNoUpdate(slot, 1, false);
                    else counter++;
                } else {
                    counter++;
                }
            }
            if (counter >= itemHandler.getInputHandler().getSlots()) break;
            j = (j + 1) % itemHandler.getInputHandler().getSlots();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (itemChanged) {
            for (int i = 0; i < itemHandler.getInputHandler().getSlots(); i++) {
                ItemStack ret = itemHandler.getStackFromInputHandler(i);
                if (!ret.isEmpty() && ret.getCount() > 1) {
                    attemptSort(i);
                }
            }
        }
    }

    /*
                if (found.isEmpty() || found.getCount() <= 1) {
                for (int i = 0; i < itemHandler.getInputHandler().getSlots(); i++) {
                    if (i == slot) continue;
                    ItemStack ret = itemHandler.getStackFromInputHandler(i);
                    if (!ret.isEmpty() && ret.getCount() > 1) {
                        attemptSort(i);
                    }
                }
                return;
            }
     */

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        if (tag.contains("sort")) {
            // get the first available item
            sorting = !sorting;
        }
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putBoolean("sorting", this.sorting);
        if (state != null) {
            pTag.put("tiered_state", NbtUtils.writeBlockState(state));
        }
        super.write(pTag);
    }

    @Override
    protected void read(CompoundTag pTag) {
        tag = pTag;
        this.sorting = pTag.getBoolean("sorting");
        this.state = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), pTag.getCompound("tiered_state"));
        if (pTag.contains("tier")) this.tier = MachineTier.valueOf(pTag.getString("tier").toUpperCase());
        super.read(pTag);
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("Furnace Menu");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new FurnaceMenu(p_39954_, p_39955_, p_39956_, this);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (state != null) {
            level.setBlockAndUpdate(worldPosition, state);
        }
    }

    @Override
    public void onTierChanged(MachineTier newTier) {
        tier = newTier;
        state = getBlockState();
        switch (newTier) {
            case BASIC -> {

                CompoundTag tag = itemHandler.serializeWithSpecificSizes(3, 3);
                itemHandler = createInventory(3, 3);
                workingRecipes = NonNullList.withSize(3, Optional.empty());

                RecipeInventoryWrapper[] tempInventories = new RecipeInventoryWrapper[3];
                // this is so the vanilla recipe can get the correct item from a larger inventory
                for (int i = 0; i < 3; i++) {
                    int finalI = i;
                    tempInventories[i] = new RecipeInventoryWrapper(itemHandler) {
                        @Override
                        public ItemStack getItem(int slot) {
                            return itemHandler.getStackInSlot(finalI);
                        }
                    };
                }

                setRecipeInventoryWrapper(tempInventories);
                itemHandler.deserialize(tag);
                replaceCapability(0, itemHandler);
            }
            case ADVANCED -> {

                CompoundTag tag = itemHandler.serializeWithSpecificSizes(5, 5);
                itemHandler = createInventory(5, 5);
                workingRecipes = NonNullList.withSize(5, Optional.empty());

                RecipeInventoryWrapper[] tempInventories = new RecipeInventoryWrapper[5];
                // this is so the vanilla recipe can get the correct item from a larger inventory
                for (int i = 0; i < 5; i++) {
                    int finalI = i;
                    tempInventories[i] = new RecipeInventoryWrapper(itemHandler) {
                        @Override
                        public ItemStack getItem(int slot) {
                            return itemHandler.getStackInSlot(finalI);
                        }
                    };
                }

                setRecipeInventoryWrapper(tempInventories);
                itemHandler.deserialize(tag);
                replaceCapability(0, itemHandler);
            }
            case SUPERIOR -> {
                CompoundTag tag = itemHandler.serializeWithSpecificSizes(7, 7);
                workingRecipes = NonNullList.withSize(7, Optional.empty());
                RecipeInventoryWrapper[] tempInventories = new RecipeInventoryWrapper[7];
                // this is so the vanilla recipe can get the correct item from a larger inventory
                for (int i = 0; i < 7; i++) {
                    int finalI = i;
                    tempInventories[i] = new RecipeInventoryWrapper(itemHandler) {
                        @Override
                        public ItemStack getItem(int slot) {
                            return itemHandler.getStackInSlot(finalI);
                        }
                    };
                }

                setRecipeInventoryWrapper(tempInventories);
                itemHandler = createInventory(7, 7);
                itemHandler.deserialize(tag);
                replaceCapability(0, itemHandler);
            }
        }
        syncBlockEntity();
    }

    @Override
    protected boolean checkConditionForRecipeTick(int index, SmeltingRecipe workingRecipe) {
        if (energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(feTick);
            return true;
        }
        else return false;
    }

    @Override
    protected boolean hasAResult(RecipeInventoryWrapper wrapper, SmeltingRecipe workingRecipe) {
        return !workingRecipe.assemble(wrapper, RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(int index, RecipeInventoryWrapper wrapper, SmeltingRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(wrapper, RegistryAccess.EMPTY);
        // get the result

        // check if the output at this slot is available;
        ItemStack ret = this.itemHandler.getOutputHandler().forceInsertItem(index, result.copy(), true);
        if (!ret.isEmpty()) return;

        // transfer items to the output slot
        itemHandler.getOutputHandler().forceInsertItem(index, result.copy(), false);

        // shrink input at index by one
        itemHandler.getInputHandler().getStackInSlot(index).shrink(1);


        finishRecipe(index);
    }
}
