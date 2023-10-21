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

public class CircuitAssemblyEntity extends RecipeWorkingBlockEntity<CircuitAssemblyRecipe> {

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
        super(Registry.BlockEntities.CIRCUIT_ASSEMBLY_ENTITY.get(), Registry.CIRCUIT_ASSEMBLY_RECIPE.get(), pos, state);
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
    protected boolean hasAResult(CircuitAssemblyRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(CircuitAssemblyRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        if (hasEnoughItemSlots(new ItemStack[]{result}, itemStackHandler.getOutputHandler()).size() != 1) return;
        transferItemResults(new ItemStack[]{result}, itemStackHandler.getOutputHandler());

        useFluidIngredients(workingRecipe.getFluidIngredients(), singleFluidTank);
        useSizeSpecificIngredients(workingRecipe.getSizeIngredients(), itemStackHandler.getInputHandler());

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
