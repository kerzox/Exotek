package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.CircuitAssemblyMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.ChemicalReactorRecipe;
import mod.kerzox.exotek.common.crafting.recipes.CircuitAssemblyRecipe;
import mod.kerzox.exotek.common.crafting.recipes.WashingPlantRecipe;
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
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class CircuitAssemblyEntity extends RecipeWorkingBlockEntity {

    private int feTick = 5;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };

    private final SidedSingleFluidTank singleFluidTank = new SidedSingleFluidTank(32000) {
        @Override
        protected void onContentsChanged(IFluidHandler handlerAffected) {
            if (!(handlerAffected instanceof TankWrapper)) { // ignore output handler
                running = false;
                doRecipeCheck();
            }
        }

    };

    private final ItemStackInventory itemStackHandler = new ItemStackInventory(6, 1) {
        @Override
        protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
            if (!(handler instanceof OutputHandler)) { // ignore output handler
                running = false;
                doRecipeCheck();
            }
        }
    };

    public CircuitAssemblyEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CIRCUIT_ASSEMBLY_ENTITY.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(singleFluidTank, itemStackHandler));
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemStackHandler.invalidate();
        singleFluidTank.invalidate();
        super.invalidateCaps();
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
            return this.singleFluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
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
    public void doRecipe(RecipeInteraction workingRecipe) {
        CircuitAssemblyRecipe recipe = (CircuitAssemblyRecipe) workingRecipe.getRecipe();
        ItemStack result = recipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        // recipe doesn't have a result return
        if (result.isEmpty()) return;

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipe);
        }

        // finish recipe
        if (getDuration() <= 0) {

            // drain our input tank by recipe amount

            for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
                if (ingredient.testWithoutAmount(singleFluidTank.getFluid())) {
                    singleFluidTank.forceDrain(ingredient.getFluidStacks().get(0).getAmount(), IFluidHandler.FluidAction.EXECUTE);
                    break;
                }
            }

            // simulate the transfer of items
            ItemStack simulation = this.itemStackHandler.getInputHandler().forceExtractItem(0, 1, true);

            // check if we are still able to finish by placing the item etc. (Might change this) TODO
            if (simulation.isEmpty()) return;
            if (!this.itemStackHandler.getOutputHandler().forceInsertItem(1, result, true).isEmpty()) return;

            this.itemStackHandler.getInputHandler().getStackInSlot(0).shrink(1);
            this.itemStackHandler.getOutputHandler().forceInsertItem(1, result, false);

            Iterator<SizeSpecificIngredient> ingredients = new ArrayList<>(recipe.getSizeIngredients()).iterator();

            while(ingredients.hasNext()) { //TODO REPLACE WITH SIZE SPECIFIC INGREDIENTS
                SizeSpecificIngredient ingredient = ingredients.next();
                for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
                    if (ingredient.test(this.itemStackHandler.getStackInSlot(i))) {
                        this.itemStackHandler.getStackInSlot(i).shrink(ingredient.getSize());
                        ingredients.remove();
                        break;
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
    }

    public SidedSingleFluidTank getSingleFluidTank() {
        return singleFluidTank;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("fluidHandler", this.singleFluidTank.serialize());
        pTag.put("itemHandler", this.itemStackHandler.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.singleFluidTank.deserializeNBT(pTag.getCompound("fluidHandler"));
        this.itemStackHandler.deserializeNBT(pTag.getCompound("itemHandler"));
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
        return Component.literal("Circuit Assembly");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new CircuitAssemblyMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
