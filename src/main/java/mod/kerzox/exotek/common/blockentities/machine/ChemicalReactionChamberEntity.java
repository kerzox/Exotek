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

public class ChemicalReactionChamberEntity extends RecipeWorkingBlockEntity<ChemicalReactorRecipe> {

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
        super(Registry.BlockEntities.CHEMICAL_REACTOR_CHAMBER_ENTITY.get(), Registry.CHEMICAL_REACTOR_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(sidedMultifluidTank, itemStackHandler));
        addCapabilities(energyHandler, itemStackHandler, sidedMultifluidTank);
    }

    @Override
    protected boolean hasAResult(ChemicalReactorRecipe workingRecipe) {
        ItemStack[] result = workingRecipe.assemble(getRecipeInventoryWrapper());
        FluidStack[] fResult = workingRecipe.assembleFluid(getRecipeInventoryWrapper());

        // recipe doesn't have a item result return
        if (result.length == 0) {
            // recipe doesn't have a fluid result
            return fResult.length != 0;
        }
        return true;
    }

    @Override
    protected void onRecipeFinish(ChemicalReactorRecipe workingRecipe) {
        ItemStack[] result = workingRecipe.assemble(getRecipeInventoryWrapper());
        FluidStack[] fResult = workingRecipe.assembleFluid(getRecipeInventoryWrapper());

        if (hasEnoughItemSlots(result, itemStackHandler.getOutputHandler()).size() != result.length) return;
        if (hasEnoughFluidSlots(fResult, sidedMultifluidTank.getOutputHandler()).size() != fResult.length) return;

        for (ItemStack resultItemStack : result) {
            for (int index = 0; index < itemStackHandler.getOutputHandler().getSlots(); index++) {
                if (itemStackHandler.getOutputHandler().forceInsertItem(index, resultItemStack, true).isEmpty()) {
                    itemStackHandler.getOutputHandler().forceInsertItem(index, resultItemStack, false);
                    break;
                }
            }
        }

        for (FluidStack resultFluidStack : fResult) {
            sidedMultifluidTank.getOutputHandler().forceFill(resultFluidStack, IFluidHandler.FluidAction.EXECUTE);
        }

        useIngredients(workingRecipe.getIngredients(), itemStackHandler.getInputHandler(), 1);
        useFluidIngredients(workingRecipe.getFluidIngredients(), sidedMultifluidTank.getInputHandler());

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
