package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.PatternRecipe;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.*;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import software.bernie.shadowed.eliotlash.mclib.math.functions.classic.Exp;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static mod.kerzox.exotek.registry.Material.MATERIALS;

public class GenerateRecipes extends RecipeProvider {

    public GenerateRecipes(PackOutput p_248933_) {
        super(p_248933_);
    }

    public static Block MachineCasing() {
        return ExotekRegistry.Blocks.MACHINE_CASING_BLOCK.get();
    }

    @Override
    protected void buildRecipes(Consumer<FinishedRecipe> consumer) {

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Tags.Items.SLIMEBALLS), RecipeCategory.BREWING,
                ExotekRegistry.Items.RESIN.get(), 2f, 20 * 10);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ExotekRegistry.Items.RESIN.get(), 4)
                .requires(ExotekRegistry.Fluids.RESIN_FLUID.getBucket().get())
                .group("exotek")
                .unlockedBy("has_resin_bucket", has(ExotekRegistry.Fluids.RESIN_FLUID.getBucket().get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.MACHINE_CASING_BLOCK.get())
                .pattern("scs")
                .pattern("csc")
                .pattern("scs")
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .define('c', ExotekRegistry.Tags.PLATE_COPPER)
                .group("exotek")
                .unlockedBy("has_steel_ingot", has(ExotekRegistry.Tags.INGOT_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.BATTERY.get())
                .pattern(" tr")
                .pattern("tlt")
                .pattern("rt ")
                .define('t', ExotekRegistry.Tags.SHEETS_TIN)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('l', ExotekRegistry.Tags.DUST_LITHIUM)
                .group("exotek")
                .unlockedBy("has_lithium_dust", has(ExotekRegistry.Tags.DUST_LITHIUM))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.ELECTROLYSIS_HOUSING.get())
                .pattern("   ")
                .pattern("pcs")
                .pattern("   ")
                .define('c', ExotekRegistry.Items.FIRED_CERAMIC_PLATE.get())
                .define('p', ExotekRegistry.Tags.ROD_PLATINUM)
                .define('s', ExotekRegistry.Tags.ROD_STEEL)
                .group("exotek")
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.CERAMIC_PLATE.get())
                .pattern(" cc")
                .pattern("ccc")
                .pattern("cc ")
                .define('c', Items.CLAY_BALL)
                .group("exotek")
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.STEEL_COMPONENT.get())
                .pattern("sss")
                .pattern("r r")
                .pattern("sss")
                .define('s', ExotekRegistry.Tags.SHEETS_STEEL)
                .define('r', ExotekRegistry.Tags.ROD_STEEL)
                .group("exotek")
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.COPPER_COMPONENT.get())
                .pattern("sss")
                .pattern("r r")
                .pattern("sss")
                .define('s', ExotekRegistry.Tags.SHEETS_COPPER)
                .define('r', ExotekRegistry.Tags.ROD_COPPER)
                .group("exotek")
                .unlockedBy("has_clay", has(Items.CLAY_BALL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.COKE_OVEN_BRICK.get())
                .pattern(" b ")
                .pattern("bsb")
                .pattern(" b ")
                .define('b', Tags.Items.INGOTS_BRICK)
                .define('s', Tags.Items.SANDSTONE)
                .group("exotek")
                .unlockedBy("has_brick", has(Tags.Items.INGOTS_BRICK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.FIRE_RESISTANT_BRICK.get())
                .pattern("bnb")
                .pattern("nsn")
                .pattern("bnb")
                .define('b', Tags.Items.INGOTS_BRICK)
                .define('n', Tags.Items.INGOTS_NETHER_BRICK)
                .define('s', Tags.Items.SANDSTONE)
                .group("exotek")
                .unlockedBy("has_netherbrick", has(Tags.Items.INGOTS_NETHER_BRICK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.WORKSTATION_BLOCK.get())
                .pattern("   ")
                .pattern("pss")
                .pattern("p p")
                .define('p', ExotekRegistry.Tags.TREATED_WOOD)
                .define('s', ExotekRegistry.Tags.TREATED_WOOD_SLAB)
                .group("exotek")
                .unlockedBy("has_netherbrick", has(Tags.Items.INGOTS_NETHER_BRICK))
                .save(consumer);

        // machines

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.RUBBER_EXTRACTION_BLOCK.get())
                .pattern("pgp")
                .pattern("rcr")
                .pattern("pgp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('g', ExotekRegistry.Tags.GEAR_STEEL)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.FURNACE_BLOCK.get())
                .pattern("pwp")
                .pattern("rcr")
                .pattern("pwp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('w', ExotekRegistry.Tags.WIRE_COPPER)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.BURNABLE_GENERATOR_BLOCK.get())
                .pattern("prp")
                .pattern("rcr")
                .pattern("prp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('c', ExotekRegistry.Blocks.FURNACE_BLOCK.get())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.MACERATOR_BLOCK.get())
                .pattern("pCp")
                .pattern("rcr")
                .pattern("pgp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('g', ExotekRegistry.Tags.GEAR_STEEL)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.CENTRIFUGE_BLOCK.get())
                .pattern("pCp")
                .pattern("rcr")
                .pattern("pgp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('g', Tags.Items.GLASS)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.COMPRESSOR_BLOCK.get())
                .pattern("pCp")
                .pattern("rcr")
                .pattern("pbp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('b', ExotekRegistry.Tags.BLOCK_STEEL)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ELECTROLYZER_BLOCK.get())
                .pattern("pCp")
                .pattern("TcT")
                .pattern("pCp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('T', ExotekRegistry.Blocks.IRON_FLUID_TANK_BLOCK.get())
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_tank", has(ExotekRegistry.Blocks.IRON_FLUID_TANK_BLOCK.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.CIRCUIT_ASSEMBLY_BLOCK.get())
                .pattern("pCp")
                .pattern("CcC")
                .pattern("pCp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.CHEMICAL_REACTOR_CHAMBER_BLOCK.get())
                .pattern("pCp")
                .pattern("rcr")
                .pattern("pCp")
                .define('p', ExotekRegistry.Tags.PLATE_ALUMINIUM)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.SINGLE_BLOCK_MINER_DRILL_BLOCK.get())
                .pattern("pCp")
                .pattern("scs")
                .pattern("prp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('s', ExotekRegistry.Items.STEEL_COMPONENT.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.MANUFACTORY_BLOCK.get())
                .pattern("pCp")
                .pattern("tct")
                .pattern("ptp")
                .define('p', ExotekRegistry.Tags.PLATE_ALUMINIUM)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('t', ExotekRegistry.Blocks.IRON_FLUID_TANK_BLOCK.get())
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.WASHING_PLANT_BLOCK.get())
                .pattern("pCp")
                .pattern("scs")
                .pattern("ptp")
                .define('p', ExotekRegistry.Tags.PLATE_STEEL)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .define('t', ExotekRegistry.Blocks.IRON_FLUID_TANK_BLOCK.get())
                .define('s', ExotekRegistry.Items.COPPER_COMPONENT.get())
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        // other shaped

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CABLE_BLOCK.get())
                .pattern(" w ")
                .pattern("wsw")
                .pattern(" w ")
                .define('w', ExotekRegistry.Tags.WIRE_COPPER)
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .group("exotek")
                .unlockedBy("has_copper_wire", has(ExotekRegistry.Tags.WIRE_COPPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CABLE_2_BLOCK.get())
                .pattern(" w ")
                .pattern("wsw")
                .pattern(" w ")
                .define('w', ExotekRegistry.Tags.WIRE_ALUMINIUM)
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .group("exotek")
                .unlockedBy("has_alum_wire", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CABLE_3_BLOCK.get())
                .pattern(" w ")
                .pattern("wsw")
                .pattern(" w ")
                .define('w', ExotekRegistry.Tags.WIRE_PLATINUM)
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .group("exotek")
                .unlockedBy("has_platinum_wire", has(MachineCasing()))
                .save(consumer);


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.SOLAR_PANEL_BLOCK.get())
                .pattern("ggg")
                .pattern("aCa")
                .pattern("aba")
                .define('g', Tags.Items.GLASS_PANES)
                .define('a', ExotekRegistry.Tags.SHEETS_ALUMINIUM)
                .define('C', ExotekRegistry.Items.ELECTRONIC_CIRCUIT.get())
                .define('b', ExotekRegistry.Items.BATTERY.get())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_BANK_CASING_BLOCK.get())
                .pattern(" p ")
                .pattern("psp")
                .pattern(" p ")
                .define('p', ExotekRegistry.Tags.PLATE_PLASTIC)
                .define('s', ExotekRegistry.Tags.SCAFFOLD_ALUMINIUM)
                .group("exotek")
                .unlockedBy("has_plastic", has(ExotekRegistry.Tags.PLATE_PLASTIC))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CELL_BASIC_BLOCK.get())
                .pattern("bCb")
                .pattern("rsr")
                .pattern("bCb")
                .define('b', ExotekRegistry.Items.BATTERY.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('s', ExotekRegistry.Tags.SCAFFOLD_ALUMINIUM)
                .define('C', ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())
                .group("exotek")
                .unlockedBy("has_plastic", has(ExotekRegistry.Tags.PLATE_PLASTIC))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CELL_ADVANCED_BLOCK.get())
                .pattern("bCb")
                .pattern("rsr")
                .pattern("bCb")
                .define('b', ExotekRegistry.Blocks.ENERGY_CELL_BASIC_BLOCK.get())
                .define('r', ExotekRegistry.Tags.PLATE_PLASTIC)
                .define('s', ExotekRegistry.Tags.SCAFFOLD_ALUMINIUM)
                .define('C', ExotekRegistry.Items.ELECTRONIC_CIRCUIT.get())
                .group("exotek")
                .unlockedBy("has_plastic", has(ExotekRegistry.Tags.PLATE_PLASTIC))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CELL_HYPER_BLOCK.get())
                .pattern("bCb")
                .pattern("rsr")
                .pattern("bCb")
                .define('b', ExotekRegistry.Blocks.ENERGY_CELL_ADVANCED_BLOCK.get())
                .define('r', ExotekRegistry.Tags.PLATE_PLASTIC)
                .define('s', ExotekRegistry.Tags.SCAFFOLD_ALUMINIUM)
                .define('C', ExotekRegistry.Items.CIRCUIT_PROCESSOR.get())
                .group("exotek")
                .unlockedBy("has_plastic", has(ExotekRegistry.Tags.PLATE_PLASTIC))
                .save(consumer);


        TurbineRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "steam_to_water_turbine"),
                new FluidStack(Fluids.WATER, 60),
                FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.STEAM.getFluid().get(), 60)),
                20).build(consumer);

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
                new ItemStack(Items.IRON_INGOT),
                new Ingredient[]{Ingredient.of(new ItemStack(Items.IRON_INGOT))},
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(Fluids.WATER, 1000))}, 100).build(consumer);

        CentrifugeRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "debug_centrifuge_recipe"),
                new ItemStack[]{new ItemStack(Items.REDSTONE, 4)},
                new FluidStack(Fluids.WATER, 1000), 100,
                SizeSpecificIngredient.of(new ItemStack(Items.COAL, 2)),
                FluidIngredient.of(new FluidStack(Fluids.LAVA, 1000))).build(consumer);

        RubberExtractionRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "rubber_from_oak_log"),
                new FluidStack(ExotekRegistry.Fluids.RESIN_FLUID.getFluid().get(), 1), Ingredient.of(ItemTags.LOGS), 1).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "wood_circuit_board_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.WOOD_CIRCUIT_BOARD.get()), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, true, false)
                        .addPredicate('X', SizeSpecificIngredient.of(ItemTags.PLANKS, 1))
                        .row("   ")
                        .row("XXX")
                        .row("   "),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.GLUE.getFluid().get(), 250))}, 20 * 10
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "wood_circuit_board2_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.WOOD_CIRCUIT_BOARD.get()), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, true, false)
                        .addPredicate('X', SizeSpecificIngredient.of(ItemTags.PLANKS, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.RESIN.get(), 1)))
                        .row("   ")
                        .row("XXX")
                        .row("RRR"),
                new FluidIngredient[]{FluidIngredient.of(FluidStack.EMPTY)}, 20 * 10
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "vacuum_tubes_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.VACUUM_TUBE.get(), 2), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, true, true)
                        .addPredicate('G', SizeSpecificIngredient.of(Tags.Items.GLASS_PANES, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('I', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_IRON, 1))
                        .row(" G ")
                        .row("GRG")
                        .row("III"),
                new FluidIngredient[]{FluidIngredient.of(FluidStack.EMPTY)}, 20 * 10
        ).build(consumer);

        WorkstationRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "wood_circuit_workstation_recipe"),
                new ItemStack(ExotekRegistry.Items.WOOD_CIRCUIT_BOARD.get()),
                PatternRecipe.Pattern.of(3, 3, true, false)
                        .addPredicate('X', SizeSpecificIngredient.of(ItemTags.PLANKS, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.RESIN.get(), 1)))
                        .row("   ")
                        .row("XXX")
                        .row("RRR"), 1
        ).build(consumer);

        WorkstationRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "primitive_circuit_recipe"),
                new ItemStack(ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get()),
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('V', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.VACUUM_TUBE.get(), 1)))
                        .addPredicate('W', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.WOOD_CIRCUIT_BOARD.get(), 1)))
                        .addPredicate('C', SizeSpecificIngredient.of(Tags.Items.INGOTS_COPPER, 1))
                        .row("VV ")
                        .row("WC ")
                        .row("   "), 1
        ).build(consumer);


        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "plastic_board_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.PLASTIC_BOARD.get(), 1), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_COPPER, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('P', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_PLASTIC, 1))
                        .addPredicate('E', SizeSpecificIngredient.of(ExotekRegistry.Tags.MICRO_WIRE_GOLD, 1))
                        .row("PCR")
                        .row("EP ")
                        .row("   "),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 250))}, 20 * 10
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "transistor_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.TRANSISTOR.get(), 4), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(ExotekRegistry.Tags.MICRO_WIRE_GOLD, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('P', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_PLASTIC, 1))
                        .row("CCR")
                        .row("P  ")
                        .row("   "),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 250))}, 20 * 10
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "capacitor_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.CAPACITOR.get(), 2), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(ExotekRegistry.Tags.MICRO_WIRE_COPPER, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('P', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_PLASTIC, 1))
                        .row("CCR")
                        .row("P  ")
                        .row("   "),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 250))}, 20 * 10
        ).build(consumer);

        CircuitAssemblyRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "electronic_circuit_recipe"),
                new ItemStack(ExotekRegistry.Items.ELECTRONIC_CIRCUIT.get()),
                PatternRecipe.Pattern.of(2, 3, false, false)
                        .addPredicate('B', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.PLASTIC_BOARD.get(), 1)))
                        .addPredicate('T', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.TRANSISTOR.get(), 1)))
                        .addPredicate('C', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.CAPACITOR.get(), 1)))
                        .row("B ")
                        .row("TT")
                        .row("CC"),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 500))}, 20 * 12
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "advanced_plastic_board"),
                new ItemStack(ExotekRegistry.Items.ADVANCED_PLASTIC_BOARD.get(), 2), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_COPPER, 1))
                        .addPredicate('p', SizeSpecificIngredient.of(ExotekRegistry.Tags.MICRO_WIRE_PLATINUM, 1))
                        .addPredicate('P', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_PLASTIC, 1))
                        .row("p p")
                        .row("CPC")
                        .row("   "),
                new FluidIngredient[]{
                        FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.REDSTONE_ACID.getFluid().get(), 250)),
                        FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 250))}, 10 * 6
        ).build(consumer);

        CircuitAssemblyRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "electronic_processor_recipe"),
                new ItemStack(ExotekRegistry.Items.CIRCUIT_PROCESSOR.get()),
                PatternRecipe.Pattern.of(2, 3, false, false)
                        .addPredicate('B', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.ADVANCED_PLASTIC_BOARD.get(), 1)))
                        .addPredicate('S', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.SILICON_WAFER.get(), 1)))
                        .addPredicate('T', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.TRANSISTOR.get(), 1)))
                        .addPredicate('C', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.CAPACITOR.get(), 1)))
                        .row("BS")
                        .row("TT")
                        .row("CC"),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 500))}, 20 * 12
        ).build(consumer);

//        ForgeRegistries.ITEMS.getValues().forEach(item -> {
//            if (item.toString().contains("raw") && (!(item instanceof BlockItem))) {
//                Item result = ForgeRegistries.ITEMS.getValue(new ResourceLocation(Exotek.MODID, item.toString().replace("raw_", "") + "_crushed_ore"));
//                if (result != null && result != Items.AIR.asItem()) {
//                    MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_crushed_ore_from_%s_raw",
//                            item.toString().replace("raw_", ""),
//                            item)),
//                            new ItemStack(result),
//                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/"+
//                                    item.toString().replace("raw_", "")))), 20 * 6).build(consumer);
//                }
//            }});

        int duration = 20 * 8;

        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_crushed_ore_from_%s_ore", "iron", "iron")),
                new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Exotek.MODID, "iron_components" + "_crushed_ore")))),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ores/iron"))), duration).build(consumer);

        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_crushed_ore_from_%s_ore", "gold", "gold")),
                new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Exotek.MODID, "gold" + "_crushed_ore")))),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ores/gold"))), duration).build(consumer);

        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_crushed_ore_from_%s_ore", "copper", "copper")),
                new ItemStack(Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(Exotek.MODID, "copper" + "_crushed_ore")))),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ores/copper"))), duration).build(consumer);

        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_impure_dust_from_%s_raw", "iron", "iron")),
                new ItemStack(ExotekRegistry.IRON_COMPONENTS.getComponentPair(Material.Component.IMPURE_DUST).getSecond().get()),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/iron"))), duration).build(consumer);

        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_impure_dust_from_%s_raw", "gold", "gold")),
                new ItemStack(ExotekRegistry.GOLD.getComponentPair(Material.Component.IMPURE_DUST).getSecond().get()),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/gold"))), duration).build(consumer);

        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_impure_dust_from_%s_raw", "copper", "copper")),
                new ItemStack(ExotekRegistry.COPPER.getComponentPair(Material.Component.IMPURE_DUST).getSecond().get()),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/copper"))), duration).build(consumer);

//        CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_plate_from_%s_ingot", "iron", "iron")),
//                new ItemStack(Registry.IRON.getComponentPair(Material.Component.PLATE).getSecond().get()),
//                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/iron"))), duration).build(consumer);
//
//        CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_plate_from_%s_ingot", "gold", "gold")),
//                new ItemStack(Registry.GOLD.getComponentPair(Material.Component.PLATE).getSecond().get()),
//                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/gold"))), duration).build(consumer);
//
//        CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_plate_from_%s_ingot", "copper", "copper")),
//                new ItemStack(Registry.COPPER.getComponentPair(Material.Component.PLATE).getSecond().get()),
//                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/copper"))), duration).build(consumer);

        MATERIALS.forEach((name, material) -> {

            List<Material.Component> components = material.getComponents();

            String name1 = material.getName();
            if (material.getName().contains("iron")) {
                name = "iron";
            }

            if (components.contains(Material.Component.PLATE)) {
                if (components.contains(Material.Component.INGOT)) {
                    CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_plate_from_%s_ingot",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.PLATE).getSecond().get(), 1),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1))), duration).build(consumer);
                }
                if (components.contains(Material.Component.SHEET)) {
                    CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_sheet_from_%s_plate",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.SHEET).getSecond().get(), 2),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "plates/" + name1))), duration).build(consumer);
                }
            }

            if (components.contains(Material.Component.CRUSHED_ORE)) {
                if (components.contains(Material.Component.CHUNK))
//                        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_crushed_ore_from_%s_raw",
//                                material.getName(),
//                                material.getName())),
//                                new ItemStack(material.getComponentPair(Material.Component.CRUSHED_ORE).getSecond().get()),
//                                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/" + material.getName()))), 20 * 6).build(consumer);

                    if (components.contains(Material.Component.ORE) || components.contains(Material.Component.DEEPSLATE_ORE))
                        MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_crushed_ore_from_%s_ore",
                                name1, name1)),
                                new ItemStack(material.getComponentPair(Material.Component.CRUSHED_ORE).getSecond().get(), 2),
                                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ores/" + name1))), duration).build(consumer);
            }

            if (components.contains(Material.Component.IMPURE_DUST)) {

                if (components.contains(Material.Component.CHUNK))
                    MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_impure_dust_from_%s_raw",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.IMPURE_DUST).getSecond().get(), 2),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/" + name1))), duration).build(consumer);

                if (components.contains(Material.Component.DUST))
                    WashingPlantRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_dust_from_%s_impure_dust", name, name)),
                            new ItemStack(material.getComponentPair(Material.Component.DUST).getSecond().get()),
                            new Ingredient[]{
                                    Ingredient.of(ItemTags.create(new ResourceLocation("forge", "impure_dusts/" + name1)))
                            },
                            new FluidIngredient[]{
                                    FluidIngredient.of(new FluidStack(Fluids.WATER, 250))
                            }, duration).build(consumer);
            }

            if (components.contains(Material.Component.DUST)) {

                if (components.contains(Material.Component.INGOT)) {

                    MaceratorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_dust_from_%s_ingot",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.DUST).getSecond().get(), 1),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1))), duration).build(consumer);
                }

            }

            if (components.contains(Material.Component.CLUSTER)) {

                if (components.contains(Material.Component.CHUNK))
                    ChemicalReactorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_impure_dust_from_%s_cluster", name, name)),
                            new ItemStack[]{new ItemStack(material.getComponentPair(Material.Component.IMPURE_DUST).getSecond().get()),},
                            new FluidStack[]{FluidStack.EMPTY}, duration,
                            new Ingredient[]{Ingredient.of(ItemTags.create(new ResourceLocation("forge", "clusters/" + name1)))},
                            new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.HYDROCHLORIC_ACID.getFluid().get(), 250))}).build(consumer);


                if (components.contains(Material.Component.METAL_SOLUTION_FLUID)) {

//                        ChemicalReactorRecipe.DatagenBuilder.addRecipe(
//                                new ResourceLocation(Exotek.MODID, "debug_chemical_reactor_recipe"),
//                                new ItemStack[]{new ItemStack(Items.REDSTONE), new ItemStack(Items.IRON_INGOT)},
//                                new FluidStack[]{new FluidStack(Fluids.WATER, 1000)}, 100,
//
//                                new Ingredient[]{Ingredient.of(new ItemStack(Items.COAL)), Ingredient.of(new ItemStack(Items.GOLD_INGOT))},
//                                new FluidIngredient[]{FluidIngredient.of(new FluidStack(Fluids.LAVA, 1000))}).build(consumer);

                    ChemicalReactorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_metal_solution_%s_raw", name, name)),
                            new ItemStack[]{ItemStack.EMPTY},
                            new FluidStack[]{new FluidStack(material.getFluidByComponent(Material.Component.METAL_SOLUTION_FLUID).getFluid().get(), 144 * 3)}, 100,
                            new Ingredient[]{Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/" + name1)))},
                            new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SULPHURIC_ACID.getFluid().get(), 250))}).build(consumer);

                    CentrifugeRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_cluster_from_%s_metal_solution", name, name)),
                            new ItemStack[]{new ItemStack(material.getComponentPair(Material.Component.CLUSTER).getSecond().get(), 1)},
                            FluidStack.EMPTY, 20 * 12,
                            SizeSpecificIngredient.of(ItemStack.EMPTY),
                            FluidIngredient.of(new FluidStack(material.getFluidByComponent(Material.Component.METAL_SOLUTION_FLUID).getFluid().get(), 144))

                    ).build(consumer);
                }

            }

        });

    }
}
