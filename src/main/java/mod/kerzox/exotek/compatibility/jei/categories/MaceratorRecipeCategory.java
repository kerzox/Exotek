package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.recipes.MaceratorRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class MaceratorRecipeCategory extends BaseRecipeCategory<MaceratorRecipe> {

    public static final RecipeType<MaceratorRecipe> recipeType
            = RecipeType.create(Exotek.MODID, "macerator_recipe", MaceratorRecipe.class);

    public MaceratorRecipeCategory(IGuiHelper helper) {
        super(recipeType, helper, ExotekRegistry.Blocks.MACERATOR_BLOCK.get());
        this.foreground = helper.createDrawable(new ResourceLocation(Exotek.MODID, "textures/gui/macerator.png"), 26,15, 125, 56);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Macerator Recipe");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, MaceratorRecipe recipe, IFocusGroup iFocusGroup) {
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 18, 19)
                .addIngredients(recipe.getIngredients().get(0));
        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 90, 19)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }
}
