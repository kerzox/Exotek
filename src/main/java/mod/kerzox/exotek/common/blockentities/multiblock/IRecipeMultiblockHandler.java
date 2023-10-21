package mod.kerzox.exotek.common.blockentities.multiblock;

import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;

import java.util.Optional;

public interface IRecipeMultiblockHandler<T extends AbstractRecipe> {

    Optional<T> getWorking();
    void checkForRecipes(Level level);
    void doRecipe(T recipeInteraction);
    void setRunning(T recipeInteraction);

}
