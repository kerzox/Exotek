package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.recipes.BlastFurnaceRecipe;
import mod.kerzox.exotek.common.crafting.recipes.CompressorRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class BlastFurnaceCategory extends BaseRecipeCategory<BlastFurnaceRecipe> {

    public static final RecipeType<BlastFurnaceRecipe> recipeType
            = RecipeType.create(Exotek.MODID, "blast_furnace_recipe", BlastFurnaceRecipe.class);

    public BlastFurnaceCategory(IGuiHelper helper) {
        super(recipeType, helper, ExotekRegistry.Blocks.BLAST_FURNACE_MANAGER_BLOCK.get());
        this.foreground = helper.createDrawable(new ResourceLocation(Exotek.MODID, "textures/gui/blast_furnace.png"), 26,15, 125, 56);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Compressor Recipe");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, BlastFurnaceRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 54, 1)
                .addIngredients(recipe.getIngredients().get(0));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 34, 1)
                .addIngredients(recipe.getIngredients().get(1));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 54, 37)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }
}
