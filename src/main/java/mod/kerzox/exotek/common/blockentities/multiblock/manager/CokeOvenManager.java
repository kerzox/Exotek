package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.BlastFurnaceRecipe;
import mod.kerzox.exotek.common.crafting.recipes.CokeOvenRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class CokeOvenManager extends RecipeMultiblockManager<CokeOvenRecipe> {

    private ItemStackInventory inventory = new ItemStackInventory(2, 1);
    private SidedSingleFluidTank fluidTank = new SidedSingleFluidTank(32000);

    private int burnTime = 0;
    private int maxBurnTime = 0;

    // this is a cached stack so we can pretend we still have a fuel stack

    public CokeOvenManager() {
        super("coke_oven");
        setRecipeInventoryWrapper(new RecipeInventoryWrapper(inventory));
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) if (getManagingBlockEntity() != null && getManagingBlockEntity().getSecond() != null) return this.getManagingBlockEntity().getSecond().onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
        return false;
    }

    public SidedSingleFluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
       if (cap == ForgeCapabilities.ITEM_HANDLER) return inventory.getHandler(side);
       if (cap == ForgeCapabilities.FLUID_HANDLER) return fluidTank.getHandler(side);
       return LazyOptional.empty();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.put("itemHandler", this.inventory.serialize());
        tag.put("fluidHandler", this.fluidTank.serialize());
        tag.putInt("duration", this.duration);
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("burnTime", this.burnTime);
        tag.putInt("max_burnTime", this.maxBurnTime);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.inventory.deserialize(tag.getCompound("itemHandler"));
        this.fluidTank.deserialize(tag.getCompound("fluidHandler"));
        this.duration = tag.getInt("duration");
        this.maxDuration = tag.getInt("max_duration");
        this.burnTime = tag.getInt("burnTime");
        this.maxBurnTime = tag.getInt("max_burnTime");
        super.deserialize(tag);
    }

    @Override
    protected void tick() {
        workingRecipe.ifPresent(this::doRecipe);
        if (burnTime > 0) burnTime--;
    }

    @Override
    public void checkForRecipes(Level level) {
        Optional<CokeOvenRecipe> recipe = level.getRecipeManager().getRecipeFor(ExotekRegistry.COKE_OVEN_RECIPE.get(),
                getRecipeInventoryWrapper(),
                level);
        recipe.ifPresent(this::doRecipe);
    }

    @Override
    protected void finishRecipe() {
        running = false;
        this.workingRecipe = Optional.empty();
        this.duration = 0;
        sync();
    }

    @Override
    public void setRunning(CokeOvenRecipe RecipeInteraction) {
        this.workingRecipe = Optional.of(RecipeInteraction);
        this.duration = RecipeInteraction.getDuration();
        this.running = true;
        this.maxDuration = RecipeInteraction.getDuration();
        if (burnTime <= 0) {
            burnTime = ForgeHooks.getBurnTime(inventory.getStackInSlot(1), RecipeType.SMELTING);
            maxBurnTime = ForgeHooks.getBurnTime(inventory.getStackInSlot(1), RecipeType.SMELTING);
            if (burnTime > 0) inventory.getInputHandler().forceExtractItem(1, 1, false);
        }
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public int getMaxDuration() {
        return super.getMaxDuration();
    }

    public int getBurnTime() {
        return burnTime;
    }

    public int getMaxBurnTime() {
        return maxBurnTime;
    }

    @Override
    public void doRecipe(CokeOvenRecipe recipeInteraction) {
        CokeOvenRecipe recipe = (CokeOvenRecipe) recipeInteraction.getRecipe();
        ItemStack result = recipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        // recipe doesn't have a result return
        if (result.isEmpty()) return;

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipeInteraction);
        }

        ItemStack input = this.inventory.getStackFromInputHandler(0);

        if (input.isEmpty()) {
            finishRecipe();
            return;
        }

        // finish recipe
        if (getDuration() <= 0) {

            ItemStack ret = this.inventory.getOutputHandler().forceInsertItem(0, result.copy(), true);
            if (!ret.isEmpty()) return;

            // transfer items to the output slot
            inventory.getOutputHandler().forceInsertItem(0, result.copy(), false);

            RecipeWorkingBlockEntity.transferFluidResults(new FluidStack[] {recipe.getFluidResults().copy()}, fluidTank);

            // shrink input at index by one
            inventory.getInputHandler().forceExtractItem(0, 1, false);

            finishRecipe();

        }

        // used all fuel
        if (burnTime <= 0) {
            finishRecipe();
            return;
        }

        duration--;

    }
}
