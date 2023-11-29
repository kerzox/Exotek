package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.ElectrolyzerRecipe;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class ElectrolyzerEntity extends RecipeWorkingBlockEntity<ElectrolyzerRecipe> implements ICustomCollisionShape {

    private int feTick = 5;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };
    private final SidedMultifluidTank multifluidTank = new SidedMultifluidTank(1, 16000, 2, 8000);
    private final SidedItemStackHandler itemStackHandler = new SidedItemStackHandler(3);

    public ElectrolyzerEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.ELECTROLYZER_ENTITY.get(), ExotekRegistry.ELECTROLYZER_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(multifluidTank.getInputHandler(), itemStackHandler));
        addCapabilities(multifluidTank, itemStackHandler, energyHandler);
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
    protected boolean hasAResult(ElectrolyzerRecipe workingRecipe) {
        return workingRecipe.assembleFluids(getRecipeInventoryWrapper()).length > 0;
    }

    @Override
    protected void onRecipeFinish(ElectrolyzerRecipe workingRecipe) {
        FluidStack[] results = workingRecipe.assembleFluids(getRecipeInventoryWrapper());
        // check if output tank 1 is valid
        if (!this.multifluidTank.getOutputHandler().getStorageTank(0).isEmpty()) {
            if (!this.multifluidTank.getOutputHandler().getStorageTank(0).getFluid().isFluidEqual(results[0])) return;
        }

        // check if output tank 2 is valid
        if (!this.multifluidTank.getOutputHandler().getStorageTank(1).isEmpty()) {
            if (!this.multifluidTank.getOutputHandler().getStorageTank(1).getFluid().isFluidEqual(results[1])) return;
        }

        // drain our input tank by recipe amount
        workingRecipe.getFluidIngredient().drain(multifluidTank.getInputHandler().getFluidInTank(0), false);

        // now fill both output tanks
        multifluidTank.getOutputHandler().getStorageTank(0).fill(results[0], IFluidHandler.FluidAction.EXECUTE);
        multifluidTank.getOutputHandler().getStorageTank(1).fill(results[1], IFluidHandler.FluidAction.EXECUTE);

        finishRecipe();

    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Electrolyzer Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new ElectrolyzerMenu(p_39954_, p_39955_, p_39956_, this);
    }

    public SidedMultifluidTank getMultifluidTank() {
        return multifluidTank;
    }

    @Override
    public VoxelShape getShape() {
        return Stream.of(
                Block.box(0, 0, 0, 16, 5, 16),
                Block.box(0, 5, 5, 7, 16, 16),
                Block.box(9, 5, 5, 16, 16, 16),
                Block.box(0, 5, 0, 16, 16, 4),
                Block.box(1, 2, 4, 15, 15, 15)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }
}
