package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.recipes.ManufactoryRecipe;
import mod.kerzox.exotek.common.crafting.recipes.WorkstationRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

public class ManufactoryRecipeCategory extends BaseRecipeCategory<ManufactoryRecipe> {

    public static final RecipeType<ManufactoryRecipe> recipeType
            = RecipeType.create(Exotek.MODID, "manufactory_recipe", ManufactoryRecipe.class);

    public ManufactoryRecipeCategory(IGuiHelper helper) {
        super(recipeType, helper, ExotekRegistry.Blocks.MANUFACTORY_BLOCK.get());
        this.background = helper.createBlankDrawable(130, 60);
        this.foreground = helper.createDrawable(new ResourceLocation(Exotek.MODID, "textures/gui/manufactory.png"), 24,17, 130, 54);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Manufactory Recipe");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, ManufactoryRecipe recipe, IFocusGroup iFocusGroup) {

        int x1 = 1;
        int yf1 = 1;

        for (int i = 0; i < 3; i++) {
            if (recipe.getFluidIngredients().size() - 1 < i) {
                iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, x1, yf1)
                        .setFluidRenderer(16000, false, 16, 16)
                        .addIngredients(ForgeTypes.FLUID_STACK, FluidIngredient.of(FluidStack.EMPTY).getFluidStacks());
            } else {
                iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, x1, yf1)
                        .setFluidRenderer(16000, false, 16, 16)
                        .addIngredients(ForgeTypes.FLUID_STACK, recipe.getFluidIngredients().get(i).getFluidStacks());
            }

            yf1 += 18;
        }

        x1 = 21;
        int y1 = 1;


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int x = (j * 18) + x1;
                int y = (i * 18) + y1;
                int index = (i * 3) + j;
                iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addIngredients(recipe.getSizeIngredients().get(index));
            }
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 106, 19)
                .addItemStack(recipe.getItemResult());
    }
}
