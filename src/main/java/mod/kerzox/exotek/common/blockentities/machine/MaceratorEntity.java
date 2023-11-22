package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.MaceratorMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.blockentities.TieredRecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.MaceratorRecipe;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.common.util.MachineTier;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.Mac;
import java.util.Optional;

public class MaceratorEntity extends TieredRecipeWorkingBlockEntity<MaceratorRecipe> {

    private int feTick = 20;
    private boolean sorting;
    private boolean itemChanged = false;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000) {
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };

    private ItemStackInventory itemHandler = createInventory(1, 1);

    private ItemStackInventory createInventory(int slots, int slots2) {
        return new ItemStackInventory(slots, slots2) {
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

    public MaceratorEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.MACERATOR_ENTITY.get(), Registry.MACERATOR_RECIPE.get(), pos, state);
        setRecipeInventoryWrapper(new RecipeInventoryWrapper[] { new RecipeInventoryWrapper(itemHandler) } );
    }

    private void attemptSort(int slot) {
        if (!sorting) return;
        itemChanged = false;
        System.out.println("sorting");
        ItemStack found = itemHandler.getStackFromInputHandler(slot);
        int remaining = found.getCount() / (itemHandler.getInputHandler().getSlots());

        int j = slot;
        int counter = 0;
        while (!found.isEmpty() && found.getCount() >= remaining) {
            if (j != slot) {
                ItemStack sim = itemHandler.getInputHandler().insertItemNoUpdate(j, found.copyWithCount(remaining), true);
                if ((itemHandler.getStackFromInputHandler(j).getCount() + 1) < found.getCount()) {
                    ItemStack ret2 = itemHandler.getInputHandler().insertItemNoUpdate(j, found.copyWithCount(1), false);
                    found.shrink(1 - ret2.getCount());
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

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        if (tag.contains("sort")) {
            // get the first available item
            sorting = !sorting;
        }
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.getHandler(side);
        }
        else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemHandler.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        onTierChanged(getTier(this));
    }


//    @Override
//    protected void onRecipeFinish(MaceratorRecipe workingRecipe) {
//        ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);
//        if (hasEnoughItemSlots(new ItemStack[] {result}, itemHandler.getOutputHandler()).size() != 1) return;
//        useIngredients(workingRecipe.getIngredients(), itemHandler.getInputHandler(), 1);
//        transferItemResults(new ItemStack[]{result}, itemHandler.getOutputHandler());
//        finishRecipe();
//    }


    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("itemHandler", this.itemHandler.serialize());
        pTag.putBoolean("sorting", this.sorting);
        super.write(pTag);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.itemHandler.deserialize(pTag.getCompound("itemHandler"));
        this.sorting = pTag.getBoolean("sorting");
        super.read(pTag);
    }
    @Override
    protected boolean hasAResult(RecipeInventoryWrapper wrapper, MaceratorRecipe workingRecipe) {
        return !workingRecipe.assemble(wrapper, RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(int index, RecipeInventoryWrapper wrapper, MaceratorRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(wrapper, RegistryAccess.EMPTY);
        // get the result

        // check if the output at this slot is available;
        ItemStack ret = this.itemHandler.getOutputHandler().forceInsertItem(index, result.copy(), true);
        if (!ret.isEmpty()) return;

        // transfer items to the output slot
        itemHandler.getOutputHandler().forceInsertItem(index, result.copy(), false);

        // shrink input at index by one
        itemHandler.getInputHandler().getStackInSlot(index).shrink(1);
        itemHandler.getInputHandler().setStackInSlot(index, itemHandler.getInputHandler().getStackInSlot(index));


        finishRecipe(index);
    }

    @Override
    protected boolean checkConditionForRecipeTick(int index, MaceratorRecipe workingRecipe) {
        if (energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(feTick);
            return true;
        }
        else return false;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Macerator Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new MaceratorMenu(p_39954_, p_39955_, p_39956_, this);
    }



    @Override
    public void onTierChanged(MachineTier newTier) {
        switch (newTier) {
            case BASIC -> {
                itemHandler.invalidate();
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
            }
            case ADVANCED -> {
                itemHandler.invalidate();
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
            }
            case SUPERIOR -> {
                itemHandler.invalidate();
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
            }
        }
    }


    @Override
    public boolean isSorting() {
        return sorting;
    }

    @Override
    public void setSorting(boolean sorting) {
        this.sorting = sorting;
    }
}
