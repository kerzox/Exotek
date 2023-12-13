package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.ChemicalReactorMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.ChemicalReactorRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

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
                finishRecipe();
            }
        }

    };
    private final ItemStackInventory itemStackHandler = new ItemStackInventory(2, 2) {
        @Override
        protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
            if (!(handler instanceof OutputHandler)) { // ignore output handler
                running = false;
                finishRecipe();
            }
        }
    };

    public ChemicalReactionChamberEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.CHEMICAL_REACTOR_CHAMBER_ENTITY.get(), ExotekRegistry.CHEMICAL_REACTOR_RECIPE.get(), pos, state);
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
    protected void read(CompoundTag pTag) {
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("max_duration", this.maxDuration);
        pTag.putInt("duration", this.duration);
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
