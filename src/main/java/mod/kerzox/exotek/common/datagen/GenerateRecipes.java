package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.*;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.WaterFluid;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;

import java.util.function.Consumer;

public class GenerateRecipes extends RecipeProvider {

    public GenerateRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
        ElectrolyzerRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "electrolyze_water"),
                FluidIngredient.of(new FluidStack(Fluids.WATER, 1)),
                1,
                new FluidStack(Fluids.LAVA, 1), new FluidStack(Fluids.LAVA, 1)).build(consumer);

        EngraverRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "debug_engrave_recipe"),
                new ItemStack(Items.GOLD_INGOT), 1, Ingredient.of(new ItemStack(Items.IRON_INGOT)), Ingredient.of(Items.COAL)).build(consumer);

        WashingPlantRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "debug_ore_washing_recipe"),
                new ItemStack(Items.IRON_INGOT), 100,
                new Ingredient[] {Ingredient.of(new ItemStack(Items.IRON_INGOT))},
                new FluidIngredient[] {FluidIngredient.of(new FluidStack(Fluids.WATER, 1000))}).build(consumer);

        ChemicalReactorRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "debug_chemical_reactor_recipe"),
                new ItemStack[] {new ItemStack(Items.REDSTONE), new ItemStack(Items.IRON_INGOT)},
                new FluidStack[]{new FluidStack(Fluids.WATER, 1000)}, 100,

                new Ingredient[] {Ingredient.of(new ItemStack(Items.COAL)), Ingredient.of(new ItemStack(Items.GOLD_INGOT))},
                new FluidIngredient[] {FluidIngredient.of(new FluidStack(Fluids.LAVA, 1000))}).build(consumer);

        CentrifugeRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "debug_centrifuge_recipe"),
                new ItemStack[] {new ItemStack(Items.REDSTONE, 4)},
                new FluidStack(Fluids.WATER, 1000), 100,
                SizeSpecificIngredient.of(new ItemStack(Items.COAL, 2)),
                FluidIngredient.of(new FluidStack(Fluids.LAVA, 1000))).build(consumer);

        RubberExtractionRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "rubber_from_oak_log"),
                new FluidStack(Fluids.WATER, 1), Ingredient.of(new ItemStack(Items.OAK_LOG)), 1).build(consumer);

    }
}
