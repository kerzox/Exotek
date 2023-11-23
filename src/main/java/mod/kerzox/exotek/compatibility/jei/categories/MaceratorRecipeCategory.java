package mod.kerzox.exotek.compatibility.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.forge.ForgeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.recipes.CircuitAssemblyRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class MaceratorRecipeCategory implements IRecipeCategory<CircuitAssemblyRecipe> {

    private IDrawableStatic background;
    private IDrawableStatic foreground;
    private IDrawable icon;

    public static final RecipeType<CircuitAssemblyRecipe> recipeType
            = RecipeType.create(Exotek.MODID, "circuit_assembly_recipe", CircuitAssemblyRecipe.class);

    public MaceratorRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(125, 60);
        this.foreground = helper.createDrawable(new ResourceLocation(Exotek.MODID, "textures/gui/circuit_assembly.png"), 30,15,
                125, 56);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(Registry.Blocks.CIRCUIT_ASSEMBLY_BLOCK.get()));
    }

    @Override
    public void draw(CircuitAssemblyRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        foreground.draw(guiGraphics);
    }

    @Override
    public RecipeType<CircuitAssemblyRecipe> getRecipeType() {
        return recipeType;
    }

    @Override
    public Component getTitle() {
        return Component.literal("Circuit Assembly Recipe");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }


    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder iRecipeLayoutBuilder, CircuitAssemblyRecipe recipe, IFocusGroup iFocusGroup) {

        int x1 = 4;
        int y1 = 3;


        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 2; j++) {
                int x = (j * 18) + x1;
                int y = (i * 18) + y1;
                int index = (i * 2) + j;
                iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, x, y)
                        .addIngredients(recipe.getSizeIngredients().get(index));
            }
        }

        for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
            if (!ingredient.isEmpty()) {
                iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.INPUT, 42, 3)
                        .setFluidRenderer(16000, false, 16, 16)
                        .addIngredients(ForgeTypes.FLUID_STACK, ingredient.getFluidStacks());
            }
        }

        iRecipeLayoutBuilder.addSlot(RecipeIngredientRole.OUTPUT, 94, 38)
                .addItemStack(recipe.getResultItem(RegistryAccess.EMPTY));
        


    }
}
