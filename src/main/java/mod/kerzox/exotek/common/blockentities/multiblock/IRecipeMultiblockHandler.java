package mod.kerzox.exotek.common.blockentities.multiblock;

import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IRecipeMultiblockHandler {

    Optional<RecipeInteraction> getWorking();
    void setWorkingRecipe(RecipeInteraction recipe);
    void checkForRecipes(Level level);
    void doRecipe(RecipeInteraction recipeInteraction);
    void setRunning(RecipeInteraction recipeInteraction);

}
