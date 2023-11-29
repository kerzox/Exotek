package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.recipes.WorkstationRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class WorkstationRecipeCategory extends BaseRecipeCategory<WorkstationRecipe> {

    public static final RecipeType<WorkstationRecipe> recipeType
            = RecipeType.create(Exotek.MODID, "workstation_recipe", WorkstationRecipe.class);

    public WorkstationRecipeCategory(IGuiHelper helper) {
        super(recipeType, helper, ExotekRegistry.Blocks.WORKSTATION_BLOCK.get());
        this.foreground = helper.createDrawable(new ResourceLocation(Exotek.MODID, "textures/gui/workstation_crafting.png"), 27,19, 125, 54);
    }

    @Override
    public Component getTitle() {
        return Component.literal("Workstation Recipe");
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, WorkstationRecipe recipe, IFocusGroup iFocusGroup) {
        int x1 = 1;
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

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 92, 19)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
    }
}
