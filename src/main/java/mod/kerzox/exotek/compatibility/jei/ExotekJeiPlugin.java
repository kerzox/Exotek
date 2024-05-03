package mod.kerzox.exotek.compatibility.jei;


import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.compatibility.jei.categories.*;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;

@JeiPlugin
public class ExotekJeiPlugin implements IModPlugin {

    public static final ResourceLocation GUI_ELEMENTS = new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png");

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Exotek.MODID, "jei_compat");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CircuitAssemblyRecipeCategory(guiHelper));
        registration.addRecipeCategories(new CompressorRecipeCategory(guiHelper));
        registration.addRecipeCategories(new MaceratorRecipeCategory(guiHelper));
        registration.addRecipeCategories(new WorkstationRecipeCategory(guiHelper));
        registration.addRecipeCategories(new ManufactoryRecipeCategory(guiHelper));
//        registration.addRecipeCategories(new ExotekSmeltingCategory(guiHelper));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addGenericGuiContainerHandler(DefaultScreen.class, new ExotekJEIGuiHandler());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ExotekRegistry.Blocks.CIRCUIT_ASSEMBLY_BLOCK.get()), CircuitAssemblyRecipeCategory.recipeType);
        registration.addRecipeCatalyst(new ItemStack(ExotekRegistry.Blocks.FURNACE_BLOCK.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(ExotekRegistry.Blocks.MACERATOR_BLOCK.get()), MaceratorRecipeCategory.recipeType);
        registration.addRecipeCatalyst(new ItemStack(ExotekRegistry.Blocks.COMPRESSOR_BLOCK.get()), CompressorRecipeCategory.recipeType);
        registration.addRecipeCatalyst(new ItemStack(ExotekRegistry.Blocks.MANUFACTORY_BLOCK.get()), ManufactoryRecipeCategory.recipeType);
        registration.addRecipeCatalyst(new ItemStack(ExotekRegistry.Blocks.WORKSTATION_BLOCK.get()), WorkstationRecipeCategory.recipeType);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(CircuitAssemblyRecipeCategory.recipeType,
                getRecipes(ExotekRegistry.CIRCUIT_ASSEMBLY_RECIPE.get()));
        registration.addRecipes(MaceratorRecipeCategory.recipeType,
                getRecipes(ExotekRegistry.MACERATOR_RECIPE.get()));
        registration.addRecipes(CompressorRecipeCategory.recipeType,
                getRecipes(ExotekRegistry.COMPRESSOR_RECIPE.get()));
        registration.addRecipes(WorkstationRecipeCategory.recipeType,
                getRecipes(ExotekRegistry.WORKSTATION_RECIPE.get()));
        registration.addRecipes(ManufactoryRecipeCategory.recipeType,
                getRecipes(ExotekRegistry.MANUFACTORY_RECIPE.get()));
//        registration.addRecipes(ExotekSmeltingCategory.recipeType,
//                getRecipes(RecipeType.SMELTING));
    }

    public <CON extends Container, T extends Recipe<CON>> List<T> getRecipes(RecipeType<T> type) {
        return Minecraft.getInstance().level.getRecipeManager().getAllRecipesFor(type);
    }

}
