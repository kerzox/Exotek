package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.FluidStorageTank;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.DistillationRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DistillationTowerManager extends RecipeMultiblockManager<DistillationRecipe> {

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            sync();
        }

    };
    private final SidedMultifluidTank multifluidTank = new SidedMultifluidTank(1, 24000, 3, 8000);
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(3);
    private static final int feTick = 0;


    public DistillationTowerManager() {
        super("distillation_tower");
        setRecipeInventoryWrapper(new RecipeInventoryWrapper(multifluidTank.getInputHandler(), itemStackHandler));
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.getMainHandItem().getItem() == Registry.Items.SILICON_WAFER.get()) {
                this.multifluidTank.getInputHandler().fill(new FluidStack(Registry.Fluids.PETROLEUM.getFluid().get(), 1000), IFluidHandler.FluidAction.EXECUTE);
            }
            else {
                pPlayer.sendSystemMessage(Component.literal("Current Tanks"));
                for (int i = 0; i < this.multifluidTank.getOutputHandler().getTanks(); i++) {
                    pPlayer.sendSystemMessage(Component.literal("Output: " + i + " ")
                            .append(Component.translatable(this.multifluidTank.getOutputHandler().getFluidInTank(i).getTranslationKey()))
                            .append(Component.literal(" Amount : " + this.multifluidTank.getOutputHandler().getFluidInTank(i).getAmount())));
                }
            }
        }
        return false;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    private boolean isCapabilityHatch(BlockPos pos) {
        if (validationDirection == Direction.SOUTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(0, -1, 3));
        else if (validationDirection == Direction.WEST)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-3, -1, 0));
        else if (validationDirection == Direction.NORTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(0, -1, -3));
        else
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(3, -1, 0));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        if (isCapabilityHatch(multiblockEntity.getBlockPos())) {
            if (cap == ForgeCapabilities.FLUID_HANDLER) {
                return multifluidTank.getHandler(side);
            }
            if (cap == ForgeCapabilities.ENERGY) {
                return energyHandler.getHandler(side);
            }
        }
        return LazyOptional.empty();
    }

    @Override
    public void checkForRecipes(Level level) {
        Optional<DistillationRecipe> recipe = level.getRecipeManager().getRecipeFor(Registry.DISTILLATION_RECIPE.get(), getRecipeInventoryWrapper(), level);
        recipe.ifPresent(this::doRecipe);
    }

    @Override
    public void doRecipe(DistillationRecipe recipeInteraction) {
        DistillationRecipe recipe = (DistillationRecipe) recipeInteraction.getRecipe();
        FluidStack[] results = recipe.assembleFluids(getRecipeInventoryWrapper());

        // recipe doesn't have a result return
        if (results.length == 0) return;

        // check if fluid is still there
        if (multifluidTank.getInputHandler().getFluidInTank(0).isEmpty()) {
            running = false;
            return;
        }

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipeInteraction);
        }

        // finish recipe
        if (getDuration() <= 0) {

            boolean drain = false;

            resultsLoop:
            for (int i = 0; i < results.length; i++) {

                for (int j = 0; j < this.multifluidTank.getOutputHandler().getTanks(); j++) {

                    FluidStorageTank tank = this.multifluidTank.getOutputHandler().getStorageTank(j);

                    // check if tank has a fluid
                    if (!tank.isEmpty()) {
                        // check if that fluid in the tank is the same as the fluid we are checking
                        if (!tank.getFluid().isFluidEqual(results[i])) continue;

                        // if the fluid is the same then we want to check if that tank is completely full
                        if (tank.isFull()) {
                            // because this tank is full we want to just skip this fluid as we now know that we have a tank with the output but we dont want
                            // to add this fluid to another tank.
                            continue resultsLoop;
                        }
                    }

                    // make sure we mark this cycle to drain the fluid as we are filling at least one of the outputs with the recipe.
                    drain = true;

                    // fill the chosen output tank with the result fluid.
                    multifluidTank.getOutputHandler().getStorageTank(j).fill(results[i], IFluidHandler.FluidAction.EXECUTE);

                    continue resultsLoop;

                }
            }

            if (drain) {
                // drain our input tank by recipe amount
                recipe.getFluidIngredient().drain(multifluidTank.getInputHandler().getFluidInTank(0), false);
            }

            finishRecipe();

            return;

        }

//        if (!energyHandler.hasEnough(feTick)) {
//            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
//            return;
//        };

        energyHandler.consumeEnergy(feTick);
        duration--;
//
    }
}
