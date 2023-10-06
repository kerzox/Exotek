package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.IRecipeMultiblockHandler;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class RecipeMultiblockManager extends AbstractMultiblockManager implements IRecipeMultiblockHandler {

    private RecipeInventoryWrapper recipeInventoryWrapper;
    protected Optional<RecipeInteraction> workingRecipe = Optional.empty();
    protected boolean running = false;
    protected int duration;
    protected int maxDuration;

    public RecipeMultiblockManager(String blueprint) {
        super(blueprint);
    }

    public void setRecipeInventoryWrapper(RecipeInventoryWrapper recipeInventoryWrapper) {
        this.recipeInventoryWrapper = recipeInventoryWrapper;
    }

    @Override
    public void tickManager() {
        super.tickManager();
        if (getManagingBlockEntity() != null) checkForRecipes(getManagingBlockEntity().getSecond().getLevel());
    }

    public RecipeInventoryWrapper getRecipeInventoryWrapper() {
        return recipeInventoryWrapper;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public Optional<RecipeInteraction> getWorking() {
        return this.workingRecipe;
    }

    @Override
    public void setWorkingRecipe(RecipeInteraction recipe) {

    }

    public int getDuration() {
        return duration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    protected void finishRecipe() {
        running = false;
        this.workingRecipe = Optional.empty();
        this.duration = 0;
        sync();
    }

    @Override
    public void setRunning(RecipeInteraction RecipeInteraction) {
        this.workingRecipe = Optional.of(RecipeInteraction);
        this.duration = RecipeInteraction.getRecipe().getDuration();
        this.running = true;
        this.maxDuration = RecipeInteraction.getRecipe().getDuration();
    }
}
