package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;

import java.util.Optional;

public abstract class RecipeMultiblockManager<T extends AbstractRecipe> extends AbstractMultiblockManager implements IRecipeMultiblockHandler<T> {

    private RecipeInventoryWrapper recipeInventoryWrapper;
    protected Optional<T> workingRecipe = Optional.empty();
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
        if (getManagingBlockEntity() != null && getManagingBlockEntity().getSecond() != null) checkForRecipes(getManagingBlockEntity().getSecond().getLevel());
    }

    public RecipeInventoryWrapper getRecipeInventoryWrapper() {
        return recipeInventoryWrapper;
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public Optional<T> getWorking() {
        return this.workingRecipe;
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
    public void setRunning(T RecipeInteraction) {
        this.workingRecipe = Optional.of(RecipeInteraction);
        this.duration = RecipeInteraction.getRecipe().getDuration();
        this.running = true;
        this.maxDuration = RecipeInteraction.getRecipe().getDuration();
    }
}
