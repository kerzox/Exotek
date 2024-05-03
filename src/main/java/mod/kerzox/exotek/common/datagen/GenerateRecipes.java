package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.PatternRecipe;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.*;
import mod.kerzox.exotek.common.event.TickUtils;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

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
                ExotekRegistry.Items.RESIN.get(), 2f, 20 * 10).unlockedBy("has_slimeballs", has(Tags.Items.SLIMEBALLS)).save(consumer,
                new ResourceLocation(Exotek.MODID, "resin_from_slime"));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ExotekRegistry.Items.RESIN.get(), 4)
                .requires(ExotekRegistry.Fluids.RESIN_FLUID.getBucket().get())
                .group("exotek")
                .unlockedBy("has_resin_bucket", has(ExotekRegistry.Fluids.RESIN_FLUID.getBucket().get()))
                .save(consumer,   new ResourceLocation(Exotek.MODID, "resin_from_fluid"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.CONVEYOR_BELT_ITEM.get())
                .pattern("   ")
                .pattern("###")
                .pattern("pcp")
                .define('#', Tags.Items.LEATHER)
                .define('p', ExotekRegistry.Tags.PLATE_IRON)
                .define('c', Tags.Items.CHESTS)
                .group("exotek")
                .unlockedBy("has_leather", has(Tags.Items.LEATHER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.CONVEYOR_BELT_PORTER.get())
                .pattern("   ")
                .pattern("r#r")
                .pattern("   ")
                .define('#', ExotekRegistry.Items.CONVEYOR_BELT_ITEM.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .group("exotek")
                .unlockedBy("has_conveyor_belt", has(ExotekRegistry.Items.CONVEYOR_BELT_ITEM.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.STORAGE_CRATE_ITEM.get())
                .pattern("sss")
                .pattern("wbw")
                .pattern("iii")
                .define('s', ExotekRegistry.Tags.TREATED_WOOD_SLAB)
                .define('w', ExotekRegistry.Tags.TREATED_WOOD)
                .define('i', ExotekRegistry.Tags.INGOT_STEEL)
                .define('b', Tags.Items.BARRELS_WOODEN)
                .group("exotek")
                .unlockedBy("has_treated_wood", has(ExotekRegistry.Tags.TREATED_WOOD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.STORAGE_CRATE_BASIC_ITEM.get())
                .pattern("sss")
                .pattern("wbw")
                .pattern("iii")
                .define('s', ExotekRegistry.Tags.TREATED_WOOD_SLAB)
                .define('w', ExotekRegistry.Tags.TREATED_WOOD)
                .define('i', ExotekRegistry.Tags.INGOT_STEEL)
                .define('b', ExotekRegistry.Items.STORAGE_CRATE_ITEM.get())
                .group("exotek")
                .unlockedBy("has_treated_wood", has(ExotekRegistry.Tags.TREATED_WOOD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.STORAGE_CRATE_ADVANCED_ITEM.get())
                .pattern("sss")
                .pattern("wbw")
                .pattern("iii")
                .define('s', ExotekRegistry.Tags.TREATED_WOOD_SLAB)
                .define('w', ExotekRegistry.Tags.TREATED_WOOD)
                .define('i', ExotekRegistry.Tags.INGOT_STEEL)
                .define('b', ExotekRegistry.Items.STORAGE_CRATE_BASIC_ITEM.get())
                .group("exotek")
                .unlockedBy("has_treated_wood", has(ExotekRegistry.Tags.TREATED_WOOD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.STORAGE_CRATE_HYPER_ITEM.get())
                .pattern("sss")
                .pattern("wbw")
                .pattern("iii")
                .define('s', ExotekRegistry.Tags.TREATED_WOOD_SLAB)
                .define('w', ExotekRegistry.Tags.TREATED_WOOD)
                .define('i', ExotekRegistry.Tags.INGOT_STEEL)
                .define('b', ExotekRegistry.Items.STORAGE_CRATE_ADVANCED_ITEM.get())
                .group("exotek")
                .unlockedBy("has_treated_wood", has(ExotekRegistry.Tags.TREATED_WOOD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.CONVEYOR_BELT_SPLITTER.get())
                .pattern(" c ")
                .pattern("r#r")
                .pattern(" c ")
                .define('#', ExotekRegistry.Items.CONVEYOR_BELT_ITEM.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('c', Tags.Items.CHESTS)
                .group("exotek")
                .unlockedBy("has_conveyor_belt", has(ExotekRegistry.Items.CONVEYOR_BELT_ITEM.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.FLUID_PIPE_BLOCK.get())
                .pattern(" p ")
                .pattern("p p")
                .pattern(" p ")
                .define('p', ExotekRegistry.Tags.PLATE_IRON)
                .group("exotek")
                .unlockedBy("has_iron_plate", has(ExotekRegistry.Tags.PLATE_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.IRON_FLUID_TANK_BLOCK.get())
                .pattern("ppp")
                .pattern("p p")
                .pattern("ppp")
                .define('p', ExotekRegistry.Tags.PLATE_IRON)
                .group("exotek")
                .unlockedBy("has_iron_plate", has(ExotekRegistry.Tags.PLATE_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.BRINE_POOL_BLOCK.get())
                .pattern("   ")
                .pattern("wsw")
                .pattern("www")
                .define('s', Tags.Items.SAND)
                .define('w', ItemTags.PLANKS)
                .group("exotek")
                .unlockedBy("has_planks", has(ItemTags.PLANKS))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.PHOTOVOLTAIC_PANEL.get())
                .pattern("ggg")
                .pattern("wpw")
                .pattern("aaa")
                .define('g', Tags.Items.GLASS_PANES)
                .define('a', ExotekRegistry.Tags.SHEETS_ALUMINIUM)
                .define('w', ExotekRegistry.Tags.WIRE_COPPER)
                .define('p', ExotekRegistry.Items.PLASTIC_BOARD.get())
                .group("exotek")
                .unlockedBy("has_steel_ingot", has(ExotekRegistry.Tags.INGOT_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.SOLAR_PANEL_BLOCK.get())
                .pattern("ppp")
                .pattern("wCw")
                .pattern("rrr")
                .define('p', ExotekRegistry.Items.PHOTOVOLTAIC_PANEL.get())
                .define('w', ExotekRegistry.Tags.WIRE_GOLD)
                .define('C', ExotekRegistry.Items.ELECTRONIC_CIRCUIT.get())
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.COKE_OVEN_BRICK.get(), 3)
                .pattern(" b ")
                .pattern("bsb")
                .pattern(" b ")
                .define('b', Tags.Items.INGOTS_BRICK)
                .define('s', Tags.Items.SANDSTONE)
                .group("exotek")
                .unlockedBy("has_brick", has(Tags.Items.INGOTS_BRICK))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.FIRE_RESISTANT_BRICK.get(), 4)
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
                .define('p', ItemTags.PLANKS)
                .define('s', ItemTags.SLABS)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENGRAVING_BLOCK.get())
                .pattern("pgp")
                .pattern("rcr")
                .pattern("pCp")
                .define('p', ExotekRegistry.Tags.PLATE_ALUMINIUM)
                .define('g', Tags.Items.GLASS)
                .define('r', Tags.Items.DUSTS_REDSTONE)
                .define('C', ExotekRegistry.Items.ELECTRONIC_CIRCUIT.get())
                .define('c', MachineCasing())
                .group("exotek")
                .unlockedBy("has_casing", has(MachineCasing()))
                .save(consumer);

        // other shaped

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CABLE_BLOCK.get(), 4)
                .pattern(" w ")
                .pattern("wsw")
                .pattern(" w ")
                .define('w', ExotekRegistry.Tags.WIRE_COPPER)
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .group("exotek")
                .unlockedBy("has_copper_wire", has(ExotekRegistry.Tags.WIRE_COPPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CABLE_2_BLOCK.get(), 4)
                .pattern(" w ")
                .pattern("wsw")
                .pattern(" w ")
                .define('w', ExotekRegistry.Tags.WIRE_ALUMINIUM)
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .group("exotek")
                .unlockedBy("has_alum_wire", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_CABLE_3_BLOCK.get(), 4)
                .pattern(" w ")
                .pattern("wsw")
                .pattern(" w ")
                .define('w', ExotekRegistry.Tags.WIRE_PLATINUM)
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .group("exotek")
                .unlockedBy("has_platinum_wire", has(MachineCasing()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Blocks.ENERGY_BANK_CASING_BLOCK.get(), 4)
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

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.SLEDGE_HAMMER_ITEM.get())
                .pattern("ss ")
                .pattern("sw ")
                .pattern("  w")
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .define('w', Tags.Items.RODS_WOODEN)
                .group("exotek")
                .unlockedBy("has_steel_ingot", has(ExotekRegistry.Tags.INGOT_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC,  ExotekRegistry.Items.SOFT_MALLET_ITEM.get())
                .define('W', ItemTags.PLANKS)
                .define('s', Tags.Items.RODS)
                .pattern("WWW").pattern("WWW").pattern(" s ").unlockedBy("has_planks", has(ItemTags.PLANKS)).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ExotekRegistry.Items.WRENCH_ITEM.get())
                .pattern("sss")
                .pattern("sr ")
                .pattern("  r")
                .define('s', ExotekRegistry.Tags.INGOT_STEEL)
                .define('r', ExotekRegistry.Tags.ROD_STEEL)
                .group("exotek")
                .unlockedBy("has_steel_ingot", has(ExotekRegistry.Tags.INGOT_STEEL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())
                .define('#', ExotekRegistry.Tags.INGOT_STEEL)
                .define('W', Tags.Items.RODS_WOODEN)
                .pattern("W#")
                .pattern(" W")
                .unlockedBy("has_steel_ingot", has(ExotekRegistry.Tags.INGOT_STEEL)).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Items.COMPRESSOR_PLATE_DIE.get())
                .define('#', ExotekRegistry.Tags.PLATE_STEEL)
                .define('W', ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())
                .pattern("#W")
                .pattern("  ")
                .unlockedBy("has_wire_cutter", has(ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Items.COMPRESSOR_GEAR_DIE.get())
                .define('#', ExotekRegistry.Tags.PLATE_STEEL)
                .define('W', ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())
                .pattern("# ")
                .pattern("W ")
                .unlockedBy("has_wire_cutter", has(ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Items.COMPRESSOR_ROD_DIE.get())
                .define('#', ExotekRegistry.Tags.PLATE_STEEL)
                .define('W', ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())
                .pattern("# ")
                .pattern(" W")
                .unlockedBy("has_wire_cutter", has(ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Items.COMPRESSOR_WIRE_DIE.get())
                .define('#', ExotekRegistry.Tags.PLATE_STEEL)
                .define('W', ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())
                .pattern("W ")
                .pattern(" #")
                .unlockedBy("has_wire_cutter", has(ExotekRegistry.Items.WIRE_CUTTER_ITEM.get())).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Blocks.COAL_COKE_BLOCK.get())
                .define('#', ExotekRegistry.Items.COAL_COKE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .unlockedBy("has_coal_coke", has(ExotekRegistry.Items.COAL_COKE.get())).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Blocks.STEEL_SCAFFOLD_BLOCK.get(), 4)
                .define('r', ExotekRegistry.Tags.ROD_STEEL)
                .define('#', ExotekRegistry.Tags.PLATE_STEEL)
                .pattern("r#r")
                .pattern("#r#")
                .pattern("r#r")
                .unlockedBy("has_steel_plate", has(ExotekRegistry.Tags.PLATE_STEEL)).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Blocks.STEEL_SCAFFOLD_SLAB_BLOCK.get(), 6)
                .define('#', ExotekRegistry.Tags.SCAFFOLD_STEEL)
                .pattern("   ")
                .pattern("###")
                .pattern("   ")
                .unlockedBy("has_steel_scaffold", has(ExotekRegistry.Tags.SCAFFOLD_STEEL)).showNotification(true).save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Blocks.STEEL_SLAB_BLOCK.get(), 6)
                .define('#', ExotekRegistry.Tags.BLOCK_STEEL)
                .pattern("   ")
                .pattern("###")
                .pattern("   ")
                .unlockedBy("has_steel_block", has(ExotekRegistry.Tags.BLOCK_STEEL)).showNotification(true).save(consumer);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ExotekRegistry.Blocks.TREATED_PLANK.get(), 1)
                .requires(ExotekRegistry.Fluids.CREOSOTE_OIL.getBucket().get())
                .requires(ItemTags.PLANKS)
                .group("exotek")
                .unlockedBy("has_creosote_bucket", has(ExotekRegistry.Fluids.CREOSOTE_OIL.getBucket().get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS,
                ExotekRegistry.Blocks.CREOSOTE_TREATED_SLAB_BLOCK.get(), 6)
                .define('#', ExotekRegistry.Blocks.TREATED_PLANK.get())
                .pattern("   ")
                .pattern("###")
                .pattern("   ")
                .unlockedBy("has_treated_planks", has(ExotekRegistry.Blocks.TREATED_PLANK.get())).showNotification(true).save(consumer);

        TurbineRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "steam_to_water_turbine"),
                new FluidStack(Fluids.WATER, 60),
                FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.STEAM.getFluid().get(), 60)),
                20).build(consumer);

        ElectrolyzerRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "oxygen_and_hydrogen_from_water"),
                FluidIngredient.of(new FluidStack(Fluids.WATER, 1)),
                1,
                new FluidStack(ExotekRegistry.Fluids.OXYGEN.getFluid().get(), 1), new FluidStack(ExotekRegistry.Fluids.HYDROGEN.getFluid().get(), 1)).build(consumer);

        RubberExtractionRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "rubber_from_oak_log"),
                new FluidStack(ExotekRegistry.Fluids.RESIN_FLUID.getFluid().get(), 1), Ingredient.of(ItemTags.LOGS), 1).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "treated_planks_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Blocks.TREATED_PLANK.get()), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('X', SizeSpecificIngredient.of(ItemTags.PLANKS, 1))
                        .row("X  ")
                        .row("   ")
                        .row("   "),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.CREOSOTE_OIL.getFluid().get(), 125))}, 20 * 10
        ).build(consumer);

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

        WorkstationRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "vacuum_tubes_workstation_recipe"),
                new ItemStack(ExotekRegistry.Items.VACUUM_TUBE.get(), 2),
                PatternRecipe.Pattern.of(3, 3, true, true)
                        .addPredicate('G', SizeSpecificIngredient.of(Tags.Items.GLASS_PANES, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('I', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_IRON, 1))
                        .row(" G ")
                        .row("GRG")
                        .row("III"),
                1
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "vacuum_tubes_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.VACUUM_TUBE.get(), 4), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, true, true)
                        .addPredicate('G', SizeSpecificIngredient.of(Tags.Items.GLASS_PANES, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('I', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_IRON, 1))
                        .row(" G ")
                        .row("GRG")
                        .row("III"),
                new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SOLDERING_FLUID.getFluid().get(), 250))}, 20 * 10
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

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "upgrade_to_basic_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.BASIC_UPGRADE_ITEM.get(), 1), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.PRIMITIVE_CIRCUIT.get())))
                        .addPredicate('r', SizeSpecificIngredient.of(Tags.Items.DUSTS_REDSTONE, 1))
                        .addPredicate('c', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.STEEL_COMPONENT.get())))
                        .addPredicate('b', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.WOOD_CIRCUIT_BOARD.get())))
                        .row("rcr")
                        .row("bCb")
                        .row("rcr"),
                new FluidIngredient[]{FluidIngredient.of(FluidStack.EMPTY)}, 20 * 10
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "upgrade_to_adv_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.ADVANCED_UPGRADE_ITEM.get(), 1), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.ELECTRONIC_CIRCUIT.get())))
                        .addPredicate('r', SizeSpecificIngredient.of(Tags.Items.DUSTS_REDSTONE, 1))
                        .addPredicate('c', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.STEEL_COMPONENT.get())))
                        .addPredicate('b', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.PLASTIC_BOARD.get())))
                        .row("rcr")
                        .row("bCb")
                        .row("rcr"),
                new FluidIngredient[]{FluidIngredient.of(FluidStack.EMPTY)}, 20 * 10
        ).build(consumer);

        ManufactoryRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "upgrade_to_sup_manufactory_recipe"),
                new ItemStack(ExotekRegistry.Items.SUPERIOR_UPGRADE_ITEM.get(), 1), FluidStack.EMPTY,
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.CIRCUIT_PROCESSOR.get())))
                        .addPredicate('r', SizeSpecificIngredient.of(Tags.Items.DUSTS_REDSTONE, 1))
                        .addPredicate('c', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.STEEL_COMPONENT.get())))
                        .addPredicate('b', SizeSpecificIngredient.of(new ItemStack(ExotekRegistry.Items.ADVANCED_PLASTIC_BOARD.get())))
                        .row("rcr")
                        .row("bCb")
                        .row("rcr"),
                new FluidIngredient[]{FluidIngredient.of(FluidStack.EMPTY)}, 20 * 10
        ).build(consumer);

        WorkstationRecipe.DatagenBuilder.addRecipe(
                new ResourceLocation(Exotek.MODID, "capacitor_workstation_recipe"),
                new ItemStack(ExotekRegistry.Items.CAPACITOR.get(), 2),
                PatternRecipe.Pattern.of(3, 3, false, false)
                        .addPredicate('C', SizeSpecificIngredient.of(ExotekRegistry.Tags.MICRO_WIRE_COPPER, 1))
                        .addPredicate('R', SizeSpecificIngredient.of(new ItemStack(Items.REDSTONE, 1)))
                        .addPredicate('P', SizeSpecificIngredient.of(ExotekRegistry.Tags.SHEETS_PLASTIC, 1))
                        .row("CCR")
                        .row("P  ")
                        .row("   "),
                        1
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
                new ItemStack(ExotekRegistry.Items.ADVANCED_PLASTIC_BOARD.get(), 1), FluidStack.EMPTY,
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

        BlastFurnaceRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, "steel_ingot_blasting"),
                new ItemStack(ExotekRegistry.STEEL.getComponentPair(Material.Component.INGOT).getSecond().get()),
                new Ingredient[] {
                        Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/iron"))),
                        Ingredient.of(ItemTags.create(new ResourceLocation("forge", "coal_coke")))
                }, TickUtils.secondsToTicks(25)).build(consumer);

        IndustrialBlastFurnaceRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, "steel_ingot_industrial_blasting"),
                new ItemStack(ExotekRegistry.STEEL.getComponentPair(Material.Component.INGOT).getSecond().get()),
                Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/iron"))),
                FluidIngredient.of(FluidStack.EMPTY), TickUtils.secondsToTicks(25), 1200).build(consumer);

        IndustrialBlastFurnaceRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, "aluminium_ingot_industrial_blasting"),
                new ItemStack(ExotekRegistry.ALUMINIUM.getComponentPair(Material.Component.INGOT).getSecond().get()),
                Ingredient.of(ExotekRegistry.Tags.DUST_ALUMINIUM),
                FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.OXYGEN.getFluid().get(), 500)), TickUtils.secondsToTicks(25), 660).build(consumer);

        CokeOvenRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, "coal_coke_from_coke_oven"),
                new ItemStack(ExotekRegistry.Items.COAL_COKE.get()),
                new FluidStack(ExotekRegistry.Fluids.CREOSOTE_OIL.getFluid().get(), 500),
                TickUtils.secondsToTicks(25),
                new Ingredient[]{
                        Ingredient.of(ItemTags.COALS)
                }).build(consumer);

        CokeOvenRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, "coal_coke_block_from_coke_oven"),
                new ItemStack(ExotekRegistry.Blocks.COAL_COKE_BLOCK.get()),
                new FluidStack(ExotekRegistry.Fluids.CREOSOTE_OIL.getFluid().get(), 500 * 9),
                TickUtils.secondsToTicks(25 * 8),
                new Ingredient[]{
                        // input
                        Ingredient.of(Items.COAL_BLOCK),
                }).build(consumer);

        EngravingRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, "silicon_wafer_engraving"),
                new ItemStack(ExotekRegistry.Items.SILICON_WAFER.get()),
                new ItemStack(Items.DIAMOND),
                Ingredient.of(ExotekRegistry.Items.SILICON_BALL.get()), duration).build(consumer);

        SimpleCookingRecipeBuilder.smelting(
                Ingredient.of(Tags.Items.SAND), RecipeCategory.MISC,
                ExotekRegistry.Items.SILICON_BALL.get(), 2f, 20 * 10).unlockedBy("has_sand", has(Tags.Items.SAND)).save(consumer);


        MATERIALS.forEach((name, material) -> {

            List<Material.Component> components = material.getComponents();

            String name1 = material.getName();
            if (material.getName().contains("iron")) {
                name = "iron";
            }

            if (components.contains(Material.Component.ROD)) {
                CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_rod_from_%s_ingot",
                        name1, name1)),
                        new ItemStack(material.getComponentPair(Material.Component.ROD).getSecond().get(), 2),
                        new ItemStack(ExotekRegistry.Items.COMPRESSOR_ROD_DIE.get()),
                        Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1))), duration).build(consumer);
            }


            if (components.contains(Material.Component.SCAFFOLD)) {

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getComponentPair(Material.Component.SCAFFOLD).getFirst().get())
                        .pattern("rpr")
                        .pattern("prp")
                        .pattern("rpr")
                        .define('r', ItemTags.create(new ResourceLocation("forge", "rods/" + name1)))
                        .define('p', ItemTags.create(new ResourceLocation("forge", "plates/" + name1)))
                        .group("exotek")
                        .unlockedBy("has_plate_"+name1, has(ItemTags.create(new ResourceLocation("forge", "plates/" + name1))))
                        .save(consumer);

            }

            if (components.contains(Material.Component.BLOCK)) {

                if (components.contains(Material.Component.INGOT)) {
                    ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getComponentPair(Material.Component.BLOCK).getFirst().get(), 1)
                            .pattern("###")
                            .pattern("###")
                            .pattern("###")
                            .define('#', ItemTags.create(new ResourceLocation("forge", "ingots/" + name1)))
                            .group("exotek")
                            .unlockedBy("has_ingot"+name1, has(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1))))
                            .save(consumer);
                }

            }

            if (components.contains(Material.Component.SHEET_BLOCK)) {

                ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, material.getComponentPair(Material.Component.SHEET_BLOCK).getFirst().get(), 2)
                        .pattern(" p ")
                        .pattern("p p")
                        .pattern(" p ")
                        .define('p', ItemTags.create(new ResourceLocation("forge", "sheets/" + name1)))
                        .group("exotek")
                        .unlockedBy("has_plate_"+name1, has(ItemTags.create(new ResourceLocation("forge", "plates/" + name1))))
                        .save(consumer);

            }

            if (components.contains(Material.Component.WIRE)) {

                CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_wire_from_%s_ingot",
                        name1, name1)),
                        new ItemStack(material.getComponentPair(Material.Component.WIRE).getSecond().get(), 2),
                        new ItemStack(ExotekRegistry.Items.COMPRESSOR_WIRE_DIE.get()),
                        Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1))), duration).build(consumer);

                if (components.contains(Material.Component.MICRO_WIRE)) {
                    CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_micro_wire_from_%s_wire",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.MICRO_WIRE).getSecond().get(), 4),
                            new ItemStack(ExotekRegistry.Items.COMPRESSOR_WIRE_DIE.get()),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "wires/" + name1))), duration).build(consumer);
                }

            }

            if (components.contains(Material.Component.PLATE)) {
                if (components.contains(Material.Component.INGOT)) {
                    CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_plate_from_%s_ingot",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.PLATE).getSecond().get(), 1),
                            new ItemStack(ExotekRegistry.Items.COMPRESSOR_PLATE_DIE.get()),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1))), duration).build(consumer);

                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getComponentPair(Material.Component.PLATE).getSecond().get(), 1)
                            .requires(ExotekRegistry.Items.SLEDGE_HAMMER_ITEM.get())
                            .requires(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1)))
                            .requires(ItemTags.create(new ResourceLocation("forge", "ingots/" + name1)))
                            .group("exotek")
                            .unlockedBy("has_sledge_hammer", has(ExotekRegistry.Items.SLEDGE_HAMMER_ITEM.get()))
                            .save(consumer);

                }
                if (components.contains(Material.Component.SHEET)) {

                    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, material.getComponentPair(Material.Component.SHEET).getSecond().get(), 1)
                            .requires(ExotekRegistry.Items.SLEDGE_HAMMER_ITEM.get())
                            .requires(ItemTags.create(new ResourceLocation("forge", "plates/" + name1)))
                            .group("exotek")
                            .unlockedBy("has_sledge_hammer", has(ExotekRegistry.Items.SLEDGE_HAMMER_ITEM.get()))
                            .save(consumer);

                    CompressorRecipe.DatagenBuilder.addRecipe(new ResourceLocation(Exotek.MODID, String.format("%s_sheet_from_%s_plate",
                            name1, name1)),
                            new ItemStack(material.getComponentPair(Material.Component.SHEET).getSecond().get(), 2),
                            new ItemStack(ExotekRegistry.Items.COMPRESSOR_PLATE_DIE.get()),
                            Ingredient.of(ItemTags.create(new ResourceLocation("forge", "plates/" + name1))), duration).build(consumer);
                }
            }

            if (components.contains(Material.Component.CRUSHED_ORE)) {
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
                                    FluidIngredient.of(new FluidStack(Fluids.WATER, 500))
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
                            new FluidStack[]{new FluidStack(material.getFluidByComponent(Material.Component.METAL_SOLUTION_FLUID).getFluid().get(), 144 * 3)}, TickUtils.secondsToTicks(15),
                            new Ingredient[]{Ingredient.of(ItemTags.create(new ResourceLocation("forge", "raw_materials/" + name1)))},
                            new FluidIngredient[]{FluidIngredient.of(new FluidStack(ExotekRegistry.Fluids.SULPHURIC_ACID.getFluid().get(), 500))}).build(consumer);

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
