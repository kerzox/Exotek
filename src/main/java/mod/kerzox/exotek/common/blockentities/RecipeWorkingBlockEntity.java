package mod.kerzox.exotek.common.blockentities;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.block.BasicEntityBlock;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class RecipeWorkingBlockEntity extends BasicBlockEntity implements MenuProvider, IServerTickable {

    private RecipeInventoryWrapper recipeInventoryWrapper;
    protected Optional<RecipeInteraction> workingRecipe = Optional.empty();
    protected boolean running;
    protected int duration;
    protected int maxDuration;

    protected boolean stall;

    public RecipeWorkingBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void setRecipeInventory(RecipeInventoryWrapper recipe) {
        this.recipeInventoryWrapper = recipe;
    }

    @Override
    public void tick() {
        if (workingRecipe.isEmpty()) doRecipeCheck();
        else doRecipe(getWorkingRecipeUnsafe());
    }

    public RecipeInventoryWrapper getRecipeInventoryWrapper() {
        return recipeInventoryWrapper;
    }
    public Optional<RecipeInteraction> getWorkingRecipeOptional() {
        return workingRecipe;
    }
    public RecipeInteraction getWorkingRecipeUnsafe() {
        return workingRecipe.orElse(null);
    }

    public boolean isRunning() {
        return running;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int amount) {
        this.duration = amount;
    }

    public void setRunning(RecipeInteraction workingRecipe) {
        this.workingRecipe = Optional.of(workingRecipe);
        this.duration = workingRecipe.getRecipe().getDuration();
        this.running = true;
        this.maxDuration = workingRecipe.getRecipe().getDuration();
    }

    public void setWorkingRecipe(RecipeInteraction workingRecipe) {
        if (workingRecipe == null) {
            this.workingRecipe = Optional.empty();
        }
        else this.workingRecipe = Optional.of(workingRecipe);
    }

    protected void finishRecipe() {
        running = false;
        this.workingRecipe = Optional.empty();
        this.duration = 0;
        syncBlockEntity();
    }

    public boolean isStalled() {
        return stall;
    }

    public void setStall(boolean stall) {
        this.stall = stall;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public abstract void doRecipeCheck();

    public abstract void doRecipe(RecipeInteraction workingRecipe);
}
