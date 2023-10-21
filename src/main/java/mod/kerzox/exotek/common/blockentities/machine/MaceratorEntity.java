package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.MaceratorMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.MaceratorRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class MaceratorEntity extends RecipeWorkingBlockEntity<MaceratorRecipe> {

    private int feTick = 20;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000) {
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };

    private ItemStackInventory itemHandler = new ItemStackInventory(1, 1) {
        @Override
        protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
            if (handler instanceof InputHandler inputHandler) {
                ItemStack stack = inputHandler.getStackInSlot(0);
                getWorkingRecipeOptional().ifPresent(recipeInteraction -> {
                    if (recipeInteraction.getRecipe() instanceof MaceratorRecipe recipe) {
                        if (!recipe.matches(getRecipeInventoryWrapper(), level)) {
                            finishRecipe();
                        }
                    }
                });
            }
        }
    };

    public MaceratorEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.MACERATOR_ENTITY.get(), Registry.MACERATOR_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(itemHandler));
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.getHandler(side);
        } else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemHandler.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    protected boolean hasAResult(MaceratorRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(MaceratorRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);
        if (hasEnoughItemSlots(new ItemStack[] {result}, itemHandler.getOutputHandler()).size() != 1) return;
        useIngredients(workingRecipe.getIngredients(), itemHandler.getInputHandler(), 1);
        transferItemResults(new ItemStack[]{result}, itemHandler.getOutputHandler());
        finishRecipe();
    }

    @Override
    protected boolean checkConditionForRecipeTick(RecipeInteraction recipe) {
        if (energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(feTick);
            return true;
        }
        else return false;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("itemHandler", this.itemHandler.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.itemHandler.deserialize(pTag.getCompound("itemHandler"));
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        super.addToUpdateTag(tag);
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
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


}
