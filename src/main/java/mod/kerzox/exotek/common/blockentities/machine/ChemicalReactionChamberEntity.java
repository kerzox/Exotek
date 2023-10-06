package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.ChemicalReactorMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.recipes.ChemicalReactorRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class ChemicalReactionChamberEntity extends RecipeWorkingBlockEntity {

    private int feTick = 5;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };
    private final SidedMultifluidTank sidedMultifluidTank = new SidedMultifluidTank(3, 16000, 2, 16000) {

        @Override
        protected void onContentsChanged(IFluidHandler handlerAffected) {
            if (!(handlerAffected instanceof OutputWrapper)) { // ignore output handler
                running = false;
                doRecipeCheck();
            }
        }

    };
    private final ItemStackInventory itemStackHandler = new ItemStackInventory(2, 2) {
        @Override
        protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
            if (!(handler instanceof OutputHandler)) { // ignore output handler
                running = false;
                doRecipeCheck();
            }
        }
    };

    public ChemicalReactionChamberEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CHEMICAL_REACTOR_CHAMBER_ENTITY.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(sidedMultifluidTank, itemStackHandler));
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemStackHandler.invalidate();
        sidedMultifluidTank.invalidate();
        super.invalidateCaps();
    }

    @Override
    public void doRecipeCheck() {
        Optional<ChemicalReactorRecipe> recipe = level.getRecipeManager().getRecipeFor(Registry.CHEMICAL_REACTOR_RECIPE.get(), getRecipeInventoryWrapper(), level);
        recipe.ifPresent(this::doRecipe);
        if (recipe.isEmpty() && this.getWorkingRecipeOptional().isPresent()) {
            this.setWorkingRecipe(null);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.getHandler(side);
        }
        else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemStackHandler.getHandler(side);
        }
        else if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.sidedMultifluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void doRecipe(RecipeInteraction workingRecipe) {
        ChemicalReactorRecipe recipe = (ChemicalReactorRecipe) workingRecipe.getRecipe();
        ItemStack[] result = recipe.assemble(getRecipeInventoryWrapper());
        FluidStack[] fResult = recipe.assembleFluid(getRecipeInventoryWrapper());

        // recipe doesn't have a item result return
        if (result.length == 0) {
            // recipe doesn't have a fluid result
            if (fResult.length == 0) {
                return;
            }
        }

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipe);
        }

        // finish recipe
        if (getDuration() <= 0) {
            int size = result.length;
            for (int i = 0; i < result.length; i++) {
                // check if we are still able to finish by placing the item etc. (Might change this) TODO
                for (int j = 0; j < itemStackHandler.getOutputHandler().getSlots(); j++) {
                    if (this.itemStackHandler.getOutputHandler().forceInsertItem(j, result[i], true).isEmpty()) {
                        this.itemStackHandler.getOutputHandler().forceInsertItem(j, result[i], false);
                        size--;
                        break;
                    }
                }

            }

            if (size != 0) return;

            size = fResult.length;
            for (int i = 0; i < fResult.length; i++) {
                // check if we are still able to finish by placing the item etc. (Might change this) TODO
                for (int j = 0; j < sidedMultifluidTank.getOutputHandler().getTanks(); j++) {
                    if (this.sidedMultifluidTank.getOutputHandler().forceFill(fResult[i], IFluidHandler.FluidAction.SIMULATE) != 0) {
                        this.sidedMultifluidTank.getOutputHandler().forceFill(fResult[i], IFluidHandler.FluidAction.EXECUTE);
                        size--;
                        break;
                    }
                }

            }

            if (size != 0) return;
            
            // take fluid from tanks

            Iterator<FluidIngredient> fluid_ingredients = new ArrayList<>(recipe.getFluidIngredients()).iterator();

            while(fluid_ingredients.hasNext()) {
                FluidIngredient fluidIngredient = fluid_ingredients.next();
                for (int i = 0; i < this.sidedMultifluidTank.getInputHandler().getTanks(); i++) {
                    if (fluidIngredient.testWithoutAmount(this.sidedMultifluidTank.getInputHandler().getStorageTank(i).getFluid())) {

                        FluidStack fluidStack = new FluidStack(this.sidedMultifluidTank.getInputHandler().getStorageTank(i).getFluid().getFluid(),
                                fluidIngredient.getFluidStacks().get(0).getAmount());

                        sidedMultifluidTank.getInputHandler().forceDrain(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                        // remove from the iterator
                        fluid_ingredients.remove();
                        break;
                    }
                }
            }

            Iterator<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients()).iterator();

            while(ingredients.hasNext()) { //TODO REPLACE WITH SIZE SPECIFIC INGREDIENTS
                Ingredient ingredient = ingredients.next();
                for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                    if (ingredient.test(this.itemStackHandler.getStackInSlot(i))) {
                        this.itemStackHandler.getStackInSlot(i).shrink(1);
                    }
                }
            }

            finishRecipe();

            return;

        }

        // do power consume TODO ideally we want the machine to stop working until energy returns this will be looked into after
        if (!energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
            return;
        };

        energyHandler.consumeEnergy(feTick);
        duration--;
//
//        syncBlockEntity();
    }

    public SidedMultifluidTank getSidedMultifluidTank() {
        return sidedMultifluidTank;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("fluidHandler", this.sidedMultifluidTank.serialize());
        pTag.put("itemHandler", this.itemStackHandler.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.sidedMultifluidTank.deserialize(pTag.getCompound("fluidHandler"));
        this.itemStackHandler.deserialize(pTag.getCompound("itemHandler"));
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        tag.put("energyHandler", this.energyHandler.serialize());
        tag.put("fluidHandler", this.sidedMultifluidTank.serialize());
        tag.put("itemHandler", this.itemStackHandler.serialize());
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
    }


    @Override
    public Component getDisplayName() {
        return Component.literal("Chemical Reaction Chamber");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new ChemicalReactorMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
