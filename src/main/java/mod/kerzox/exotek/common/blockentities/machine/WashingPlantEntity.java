package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.WashingPlantMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.recipes.WashingPlantRecipe;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class WashingPlantEntity extends RecipeWorkingBlockEntity<WashingPlantRecipe> implements ICustomCollisionShape {

    private int feTick = 5;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };
    private final SidedSingleFluidTank singleFluidTank = new SidedSingleFluidTank(32000);
    private final ItemStackInventory itemStackHandler = new ItemStackInventory(1, 2);
    private float byProductModifier = 1;

    public WashingPlantEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.WASHING_PLANT_ENTITY.get(), ExotekRegistry.WASHING_PLANT_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(singleFluidTank, itemStackHandler));
        addCapabilities(singleFluidTank, itemStackHandler, energyHandler);
    }

    @Override
    protected boolean hasAResult(WashingPlantRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        if (tag.contains("empty")) {
            this.getSingleFluidTank().setFluidInTank(FluidStack.EMPTY);
        }
    }

    @Override
    protected void onRecipeFinish(WashingPlantRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        for (FluidIngredient ingredient : workingRecipe.getFluidIngredients()) {
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

        // handle byproduct stuff TODO


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
    protected void read(CompoundTag pTag) {
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
    }

    @Override
    protected void write(CompoundTag tag) {
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Washing Plant");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new WashingPlantMenu(p_39954_, p_39955_, p_39956_, this);
    }

    @Override
    public VoxelShape getShape() {
        return Stream.of(
                Block.box(1, 2, 1, 15, 15, 8),
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(0, 0, 0, 16, 2, 8)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }
}
