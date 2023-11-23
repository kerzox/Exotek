package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.library.plugins.vanilla.cooking.AbstractCookingCategory;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.recipes.CircuitAssemblyRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class ExotekSmeltingCategory extends AbstractCookingCategory<SmeltingRecipe> {

    public static final RecipeType<SmeltingRecipe> recipeType
            = RecipeType.create(Exotek.MODID, "smelting_recipe", SmeltingRecipe.class);

    public ExotekSmeltingCategory(IGuiHelper guiHelper) {
        super(guiHelper, Registry.Blocks.FURNACE_BLOCK.get(), "gui.jei.category.smelting", 200);
    }

    @Override
    public RecipeType<SmeltingRecipe> getRecipeType() {
        return RecipeTypes.SMELTING;
    }
}
