package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public abstract class BaseRecipeCategory<T extends AbstractRecipe<RecipeInventoryWrapper>> implements IRecipeCategory<T> {

    protected IDrawableStatic background;
    protected IDrawableStatic foreground;
    private IDrawable icon;
    private RecipeType<T> recipeType;

    public BaseRecipeCategory(RecipeType<T> recipeType, IGuiHelper helper, Block block) {
        this.background = helper.createBlankDrawable(125, 60);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(block));
        this.recipeType = recipeType;
    }


    @Override
    public void draw(T recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        foreground.draw(guiGraphics);
    }

    @Override
    public RecipeType<T> getRecipeType() {
        return recipeType;
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }
}
