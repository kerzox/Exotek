package mod.kerzox.exotek.registry;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.menu.*;
import mod.kerzox.exotek.client.gui.menu.multiblock.EnergyBankMenu;
import mod.kerzox.exotek.client.gui.menu.multiblock.FluidTankMultiblockMenu;
import mod.kerzox.exotek.client.gui.menu.transfer.EnergyCableMenu;
import mod.kerzox.exotek.common.block.*;
import mod.kerzox.exotek.common.block.machine.*;
import mod.kerzox.exotek.common.block.multiblock.DynamicMultiblockEntityBlock;
import mod.kerzox.exotek.common.block.multiblock.MultiblockBlock;
import mod.kerzox.exotek.common.block.multiblock.MultiblockInvisibleBlock;
import mod.kerzox.exotek.common.block.transport.ConveyorBeltBlock;
import mod.kerzox.exotek.common.block.transport.ConveyorBeltRampBlock;
import mod.kerzox.exotek.common.block.transport.EnergyCableBlock;
import mod.kerzox.exotek.common.block.transport.FluidPipeBlock;
import mod.kerzox.exotek.common.blockentities.WorkstationEntity;
import mod.kerzox.exotek.common.blockentities.machine.*;
import mod.kerzox.exotek.common.blockentities.machine.generator.BurnableGeneratorEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.EnergyBankCasingEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.SolarPanelEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.BrinePoolEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.*;
import mod.kerzox.exotek.common.blockentities.storage.FluidTankSingleEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockInvisibleEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.ConveyorBeltPorter;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltRampEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.ConveyorBeltSplitter;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.*;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.fluid.ExotekFluidBlock;
import mod.kerzox.exotek.common.fluid.ExotekFluidType;
import mod.kerzox.exotek.common.item.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static mod.kerzox.exotek.Exotek.MODID;
import static mod.kerzox.exotek.registry.Material.Component.*;
import static mod.kerzox.exotek.registry.Registry.BlockEntities.BURNABLE_GENERATOR_ENTITY;
import static mod.kerzox.exotek.registry.Registry.BlockEntities.MULTIBLOCK_INVISIBLE_ENTITY;

public class Registry {

    public static final ArrayList<RegistryObject<Item>> ALL_ITEMS = new ArrayList<>();

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MODID);
    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MODID);
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, MODID);
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MODID);
    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MODID);
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,  MODID);
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static void init(IEventBus bus) {
        BLOCKS.register(bus);
        BLOCK_ENTITIES.register(bus);
        ITEMS.register(bus);
        FLUIDS.register(bus);
        FLUID_TYPES.register(bus);
        RECIPE_TYPES.register(bus);
        RECIPES.register(bus);
        MENUS.register(bus);
        EFFECTS.register(bus);
        PARTICLE_TYPES.register(bus);
        CREATIVE_MODE_TABS.register(bus);
        ENTITIES.register(bus);
        // these are for all the machine and special items
        Items.init();
        Blocks.init();
        BlockEntities.init();
        Menus.init();
        Fluids.init();
        Tags.init();
        Entities.init();

        CraftingHelper.register(new ResourceLocation(Exotek.MODID, "size_specific_ingredient"), SizeSpecificIngredient.Serializer.INSTANCE);
        CraftingHelper.register(new ResourceLocation(Exotek.MODID, "fluid_ingredient"), FluidIngredient.Serializer.INSTANCE);

    }

    //// generic blocks/items

    private static final Material.Builder mat = new Material.Builder();

    public static final Material TIN = mat
            .name("tin")
            .colour(0xFFe0e8f6)
            .fullMetalComponents()
            .build();
    public static final Material NICKEL = mat
            .name("nickel")
            .colour(0xFF727472)
            .fullMetalComponents()
            .build();
    public static Material IRON = mat
            .name("iron")
            .colour(0xFFffffff)
            .addComponents(SCAFFOLD, SHEET_BLOCK, ROD, SHEET, PLATE, MICRO_WIRE, GEAR, WIRE)
            .build();
    public static Material IRON_COMPONENTS = mat
            .name("iron_components")
            .colour(0xFFd8af93)
            .addComponents(CLUSTER, METAL_SOLUTION_FLUID, DUST, CRUSHED_ORE, IMPURE_DUST)
            .build();
    public static Material PLASTIC = mat
            .name("plastic")
            .colour(0xFFc9c9c9)
            .addComponents(PLATE, SHEET)
            .build();
    public static Material COPPER = mat
            .name("copper")
            .colour(0xFFe77c56)
            .addComponents(CLUSTER, METAL_SOLUTION_FLUID, DUST, CRUSHED_ORE, SCAFFOLD, IMPURE_DUST, SHEET_BLOCK, ROD, SHEET, PLATE, MICRO_WIRE, WIRE)
            .build();
    public static Material GOLD = mat
            .name("gold")
            .colour(0xFFffd34f)
            .addComponents(CLUSTER, METAL_SOLUTION_FLUID, DUST, CRUSHED_ORE, SCAFFOLD, IMPURE_DUST, SHEET_BLOCK, ROD, SHEET, PLATE, MICRO_WIRE, WIRE)
            .build();
    public static Material STEEL = mat
            .name("steel")
            .colour(0x7A7A7A)
            .addComponents(DUST, BLOCK, GEAR, SHEET_BLOCK, ROD, SHEET, PLATE, MICRO_WIRE, WIRE, INGOT, NUGGET)
            .build();
    public static Material LITHIUM = mat
            .name("lithium")
            .colour(0xFFd8c2b4)
            .fullMetalComponents()
            .build();
    public static Material ALUMINIUM = mat
            .name("aluminium")
            .colour(0xFFBDECFF)
            .fullMetalComponents()
            .build();
    public static Material URANIUM = mat
            .name("uranium")
            .colour(0xFF7bfa8e)
            .fullMetalComponents()
            .build();
    public static Material PLATINUM = mat
            .name("platinum")
            .colour(0xFFafffff)
            .fullMetalComponents()
            .build();
    public static Material LEAD = mat
            .name("lead")
            .colour(0xFF8b9ed3)
            .fullMetalComponents()
            .build();
    public static Material SILVER = mat
            .name("silver")
            .colour(0xFFe3e8f7)
            .fullMetalComponents()
            .build();
    ///



    public static final RegistryObject<RecipeType<MaceratorRecipe>> MACERATOR_RECIPE = RECIPE_TYPES.register("macerator", () -> RecipeType.simple(new ResourceLocation(MODID, "macerator")));
    public static final RegistryObject<MaceratorRecipe.Serializer> MACERATOR_RECIPE_SERIALIZER = RECIPES.register("macerator_recipe_serializer", MaceratorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<CompressorRecipe>> COMPRESSOR_RECIPE = RECIPE_TYPES.register("compressor", () -> RecipeType.simple(new ResourceLocation(MODID, "compressor")));
    public static final RegistryObject<CompressorRecipe.Serializer> COMPRESSOR_RECIPE_SERIALIZER = RECIPES.register("compressor_recipe_serializer", CompressorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ElectrolyzerRecipe>> ELECTROLYZER_RECIPE = RECIPE_TYPES.register("electrolyzer", () -> RecipeType.simple(new ResourceLocation(MODID, "electrolyzer")));
    public static final RegistryObject<ElectrolyzerRecipe.Serializer> ELECTROLYZER_RECIPE_SERIALIZER = RECIPES.register("electrolyzer_recipe_serializer", ElectrolyzerRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<EngraverRecipe>> ENGRAVER_RECIPE = RECIPE_TYPES.register("engraver", () -> RecipeType.simple(new ResourceLocation(MODID, "engraver")));
    public static final RegistryObject<EngraverRecipe.Serializer> ENGRAVER_RECIPE_SERIALIZER = RECIPES.register("engraver_recipe_serializer", EngraverRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<WashingPlantRecipe>> WASHING_PLANT_RECIPE = RECIPE_TYPES.register("washing_plant", () -> RecipeType.simple(new ResourceLocation(MODID, "washing_plant")));
    public static final RegistryObject<WashingPlantRecipe.Serializer> WASHING_PLANT_RECIPE_SERIALIZER = RECIPES.register("washing_plant_recipe_serializer", WashingPlantRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ChemicalReactorRecipe>> CHEMICAL_REACTOR_RECIPE = RECIPE_TYPES.register("chemical_reactor", () -> RecipeType.simple(new ResourceLocation(MODID, "chemical_reactor")));
    public static final RegistryObject<ChemicalReactorRecipe.Serializer> CHEMICAL_REACTOR_RECIPE_SERIALIZER = RECIPES.register("chemical_reactor_recipe_serializer", ChemicalReactorRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<CircuitAssemblyRecipe>> CIRCUIT_ASSEMBLY_RECIPE = RECIPE_TYPES.register("circuit_assembly", () -> RecipeType.simple(new ResourceLocation(MODID, "circuit_assembly")));
    public static final RegistryObject<CircuitAssemblyRecipe.Serializer> CIRCUIT_ASSEMBLY_RECIPE_SERIALIZER = RECIPES.register("circuit_assembly_recipe_serializer", CircuitAssemblyRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<CentrifugeRecipe>> CENTRIFUGE_RECIPE = RECIPE_TYPES.register("centrifuge", () -> RecipeType.simple(new ResourceLocation(MODID, "centrifuge")));
    public static final RegistryObject<CentrifugeRecipe.Serializer> CENTRIFUGE_RECIPE_SERIALIZER = RECIPES.register("centrifuge_recipe_serializer", CentrifugeRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<ManufactoryRecipe>> MANUFACTORY_RECIPE = RECIPE_TYPES.register("manufactory", () -> RecipeType.simple(new ResourceLocation(MODID, "manufactory")));
    public static final RegistryObject<ManufactoryRecipe.Serializer> MANUFACTORY_RECIPE_SERIALIZER = RECIPES.register("manufactory_recipe_serializer", ManufactoryRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<RubberExtractionRecipe>> RUBBER_EXTRACTION_RECIPE = RECIPE_TYPES.register("rubber_extraction", () -> RecipeType.simple(new ResourceLocation(MODID, "rubber_extraction")));
    public static final RegistryObject<RubberExtractionRecipe.Serializer> RUBBER_EXTRACTION_RECIPE_SERIALIZER = RECIPES.register("rubber_extraction_recipe_serializer", RubberExtractionRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<GroundSampleDrillRecipe>> GROUND_SAMPLE_DRILL_RECIPE = RECIPE_TYPES.register("ground_sample_drill", () -> RecipeType.simple(new ResourceLocation(MODID, "rubber_extraction")));
    public static final RegistryObject<GroundSampleDrillRecipe.Serializer> GROUND_SAMPLE_DRILL_RECIPE_SERIALIZER = RECIPES.register("ground_sample_drill_recipe_serializer", GroundSampleDrillRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<DistillationRecipe>> DISTILLATION_RECIPE = RECIPE_TYPES.register("distillation", () -> RecipeType.simple(new ResourceLocation(MODID, "distillation")));
    public static final RegistryObject<DistillationRecipe.Serializer> DISTILLATION_RECIPE_SERIALIZER = RECIPES.register("distillation_recipe_serializer", DistillationRecipe.Serializer::new);
    public static final RegistryObject<RecipeType<TurbineRecipe>> TURBINE_RECIPE = RECIPE_TYPES.register("turbine", () -> RecipeType.simple(new ResourceLocation(MODID, "turbine")));
    public static final RegistryObject<TurbineRecipe.Serializer> TURBINE_RECIPE_SERIALIZER = RECIPES.register("turbine_recipe_serializer", TurbineRecipe.Serializer::new);


    public static final RegistryObject<CreativeModeTab> EXOTEK_TAB = CREATIVE_MODE_TABS.register("exotek_tab", () -> CreativeModeTab.builder()
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .displayItems((params, output) -> {
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "blast_furnace"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "coke_oven"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "industrial_blast_furnace"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "pump_jack"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "distillation_tower"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "miner"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "fluid_tank"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "flotation_plant"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "boiler"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "turbine"));
                output.accept(BlueprintValidatorItem.of(Items.BLUEPRINT_ITEM.get(), "alternator"));
            })
            .icon(() -> Items.BLUEPRINT_ITEM.get().getDefaultInstance()).build());

    public static final class Tags {

        public static void init() {

        }

        public static final TagKey<Item> PLATES = ItemTags.create(new ResourceLocation("forge", "plates"));
        public static final TagKey<Item> SHEETS = ItemTags.create(new ResourceLocation("forge", "sheets"));
        public static final TagKey<Item> MICRO_WIRE = ItemTags.create(new ResourceLocation("forge", "micro_wires"));


        public static final TagKey<Item> SHEETS_IRON = ItemTags.create(new ResourceLocation("forge", "sheets/iron"));
        public static final TagKey<Item> SHEETS_COPPER = ItemTags.create(new ResourceLocation("forge", "sheets/copper"));
        public static final TagKey<Item> SHEETS_PLASTIC = ItemTags.create(new ResourceLocation("forge", "sheets/plastic"));

        public static final TagKey<Item> MICRO_WIRE_ELECTRUM = ItemTags.create(new ResourceLocation("forge", "micro_wires/electrum"));
        public static final TagKey<Item> MICRO_WIRE_GOLD = ItemTags.create(new ResourceLocation("forge", "micro_wires/gold"));
        public static final TagKey<Item> MICRO_WIRE_PLATINUM = ItemTags.create(new ResourceLocation("forge", "micro_wires/platinum"));
        public static final TagKey<Item> MICRO_WIRE_COPPER = ItemTags.create(new ResourceLocation("forge", "micro_wires/copper"));
    }

    public static final class Items {

        public static void init() {

        }

        public static final RegistryObject<Item> BLUEPRINT_ITEM = register(ITEMS.register("blueprint_item", () -> new BlueprintValidatorItem(new Item.Properties())));
        public static final RegistryObject<Item> SILICON_WAFER = build(ITEMS.register("silicon_wafer_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> PRIMITIVE_CIRCUIT = build(ITEMS.register("primitive_circuit_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> ELECTRONIC_CIRCUIT = build(ITEMS.register("electronic_circuit_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> CIRCUIT_PROCESSOR = build(ITEMS.register("processor_circuit_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> WOOD_CIRCUIT_BOARD = build(ITEMS.register("wood_circuit_board_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> VACUUM_TUBE = build(ITEMS.register("vacuum_tube_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> PLASTIC_BOARD = build(ITEMS.register("plastic_board_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> TRANSISTOR = build(ITEMS.register("transistor_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> CAPACITOR = build(ITEMS.register("capacitor_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> ADVANCED_PLASTIC_BOARD = build(ITEMS.register("adv_plastic_board_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> STEEL_COMPONENT = build(ITEMS.register("steel_component_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> COPPER_COMPONENT = build(ITEMS.register("copper_component_item", () -> new Item(new Item.Properties())));

        public static final RegistryObject<Item> SOFT_MALLET_ITEM = build(ITEMS.register("soft_mallet_item", () -> new ExotekItem(new Item.Properties())));
        public static final RegistryObject<Item> WRENCH_ITEM = build(ITEMS.register("wrench_item", () -> new WrenchItem(new Item.Properties())));

        public static final RegistryObject<Item> CERAMIC_PLATE = build(ITEMS.register("ceramic_plate_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> ELECTROLYSIS_HOUSING = build(ITEMS.register("electrolysis_housing_item", () -> new Item(new Item.Properties())));
        public static final RegistryObject<Item> BATTERY = build(ITEMS.register("battery_item", () -> new ElectricalItem(new Item.Properties().durability(100),
                100000)));
        public static final RegistryObject<Item> SPEED_UPGRADE_ITEM = build(ITEMS.register("speed_upgrade_item", () -> new MachineUpgradeItem(
                "speed",
                new Item.Properties().stacksTo(16))));

        public static final RegistryObject<Item> ENERGY_UPGRADE_ITEM = build(ITEMS.register("energy_upgrade_item", () -> new MachineUpgradeItem(
                "energy",
                new Item.Properties().stacksTo(16))));

        public static final RegistryObject<Item> ANCHOR_UPGRADE_ITEM = build(ITEMS.register("world_anchor_upgrade_item", () -> new MachineUpgradeItem(
                "world_anchor",
                new Item.Properties().stacksTo(1))));

        public static final RegistryObject<ConveyorBeltCoverItem> CONVEYOR_BELT_PORTER = build(ITEMS.register("conveyor_belt_porter", () -> new ConveyorBeltCoverItem(
                new Item.Properties(),
                new ConveyorBeltPorter())));

        public static final RegistryObject<ConveyorBeltCoverItem> CONVEYOR_BELT_SPLITTER = build(ITEMS.register("conveyor_belt_splitter", () -> new ConveyorBeltCoverItem(
                new Item.Properties(),
                new ConveyorBeltSplitter())));

        public static final RegistryObject<Item> CONVEYOR_BELT_RAMP_ITEM = build(ITEMS.register("conveyor_belt_ramp_item", () ->
                new ConveyorBeltBlock.Item(Blocks.CONVEYOR_BELT_RAMP_BLOCK.get(), CapabilityTiers.BASIC, new Item.Properties())));
        public static final RegistryObject<Item> CONVEYOR_BELT_ITEM = build(ITEMS.register("conveyor_belt_item", () ->
                new ConveyorBeltBlock.Item(Blocks.CONVEYOR_BELT_BLOCK.get(), CapabilityTiers.BASIC, new Item.Properties())));
//        public static final RegistryObject<Item> CONVEYOR_BELT_2_ITEM = build(ITEMS.register("energy_cable_2_item", () ->
//                new EnergyCableBlock.Item(Blocks.ENERGY_CABLE_2_BLOCK.get(), CapabilityTiers.ADVANCED, new Item.Properties())));
//        public static final RegistryObject<Item> CONVEYOR_BELT_3_ITEM = build(ITEMS.register("energy_cable_3_item", () ->
//                new EnergyCableBlock.Item(Blocks.ENERGY_CABLE_3_BLOCK.get(), CapabilityTiers.HYPER, new Item.Properties())));

        public static final RegistryObject<Item> ENERGY_CABLE_ITEM = build(ITEMS.register("energy_cable_item", () ->
                new EnergyCableBlock.Item(Blocks.ENERGY_CABLE_BLOCK.get(), CapabilityTiers.BASIC, new Item.Properties())));
        public static final RegistryObject<Item> ENERGY_CABLE_2_ITEM = build(ITEMS.register("energy_cable_2_item", () ->
                new EnergyCableBlock.Item(Blocks.ENERGY_CABLE_2_BLOCK.get(), CapabilityTiers.ADVANCED, new Item.Properties())));
        public static final RegistryObject<Item> ENERGY_CABLE_3_ITEM = build(ITEMS.register("energy_cable_3_item", () ->
                new EnergyCableBlock.Item(Blocks.ENERGY_CABLE_3_BLOCK.get(), CapabilityTiers.HYPER, new Item.Properties())));

        // don't add to all items
        private static <T extends Item> RegistryObject<Item> register(RegistryObject<Item> item) {
            return item;
        }

        private static <T extends Item> RegistryObject<T> build(RegistryObject<T> item) {
            ALL_ITEMS.add((RegistryObject<Item>) item);
            return item;
        }

    }

    public static final class Blocks {

        private static ArrayList<RegistryObject<Block>> all_blocks = new ArrayList<>();

        public static ArrayList<RegistryObject<Block>> getRegisteredBlocks() {
            return all_blocks;
        }

        public static void init() {
        }

        public static final makeBlock<BasicBlock> MACHINE_CASING_BLOCK
                = makeBlock.build("steel_casing_block",
                BasicBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .sound(SoundType.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                true);

        public static final makeBlock<ConveyorBeltBlock> CONVEYOR_BELT_BLOCK
                = makeBlock.build("conveyor_belt_block",
                ConveyorBeltBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                false);

        public static final makeBlock<ConveyorBeltRampBlock> CONVEYOR_BELT_RAMP_BLOCK
                = makeBlock.build("conveyor_belt_ramp_block",
                ConveyorBeltRampBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .noCollission()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                false);

        public static final makeBlock<ConveyorBeltRampBlock.Top> CONVEYOR_BELT_RAMP_TOP_BLOCK
                = makeBlock.build("conveyor_belt_ramp_top_block",
                ConveyorBeltRampBlock.Top::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .noCollission()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                false);

        public static final makeBlock<EnergyCableBlock> ENERGY_CABLE_BLOCK
                = makeBlock.build("energy_cable_block",
                EnergyCableBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                false);

        public static final makeBlock<BrinePoolBlock> BRINE_POOL_BLOCK
                = makeBlock.build("brine_pool_block",
                BrinePoolBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                true);

        public static final makeBlock<EnergyCableBlock> ENERGY_CABLE_2_BLOCK
                = makeBlock.build("energy_cable_2_block",
                EnergyCableBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                false);

        public static final makeBlock<EnergyCableBlock> ENERGY_CABLE_3_BLOCK
                = makeBlock.build("energy_cable_3_block",
                EnergyCableBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)),
                false);

        public static final makeBlock<HalfBlock> STEEL_SLAB_BLOCK
                = makeBlock.build("steel_slab_block",
                HalfBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<HalfBlock> STEEL_SCAFFOLD_SLAB_BLOCK
                = makeBlock.build("steel_scaffold_slab_block",
                HalfBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<HalfBlock> CREOSOTE_TREATED_SLAB_BLOCK
                = makeBlock.build("creosote_treated_slab_block",
                HalfBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .sound(SoundType.WOOD)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MultiblockInvisibleBlock<MultiblockInvisibleEntity>> MULTIBLOCK_INVISIBLE_BLOCK
                = makeBlock.build("multiblock_invisible_block",
                p -> new MultiblockInvisibleBlock<>(MULTIBLOCK_INVISIBLE_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<BurnableGeneratorEntity>> BURNABLE_GENERATOR_BLOCK
                = makeBlock.build("burnable_generator_block",
                p -> new MachineEntityBlock<>(BURNABLE_GENERATOR_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<CentrifugeEntity>> CENTRIFUGE_BLOCK
                = makeBlock.build("centrifuge_block",
                p -> new MachineEntityBlock<>(BlockEntities.CENTRIFUGE_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<ChemicalReactionChamberEntity>> CHEMICAL_REACTOR_CHAMBER_BLOCK
                = makeBlock.build("chemical_reactor_chamber_block",
                p -> new MachineEntityBlock<>(BlockEntities.CHEMICAL_REACTOR_CHAMBER_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<CircuitAssemblyEntity>> CIRCUIT_ASSEMBLY_BLOCK
                = makeBlock.build("circuit_assembly_block",
                p -> new MachineEntityBlock<>(BlockEntities.CIRCUIT_ASSEMBLY_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<CompressorEntity>> COMPRESSOR_BLOCK
                = makeBlock.build("compressor_block",
                p -> new MachineEntityBlock<>(BlockEntities.COMPRESSOR_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<ElectrolyzerEntity>> ELECTROLYZER_BLOCK
                = makeBlock.build("electrolyzer_block",
                p -> new MachineEntityBlock<>(BlockEntities.ELECTROLYZER_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<EngravingEntity>> ENGRAVING_BLOCK
                = makeBlock.build("engraving_block",
                p -> new MachineEntityBlock<>(BlockEntities.ENGRAVING_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<FurnaceEntity>> FURNACE_BLOCK
                = makeBlock.build("furnace_block",
                p -> new MachineEntityBlock<>(BlockEntities.FURNACE_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<MaceratorEntity>> MACERATOR_BLOCK
                = makeBlock.build("macerator_block",
                p -> new MachineEntityBlock<>(BlockEntities.MACERATOR_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<ManufactoryEntity>> MANUFACTORY_BLOCK
                = makeBlock.build("manufactory_block",
                p -> new MachineEntityBlock<>(BlockEntities.MANUFACTORY_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MachineEntityBlock<WashingPlantEntity>> WASHING_PLANT_BLOCK
                = makeBlock.build("washing_plant_block",
                p -> new MachineEntityBlock<>(BlockEntities.WASHING_PLANT_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<DynamicMultiblockEntityBlock<SolarPanelEntity>> SOLAR_PANEL_BLOCK
                = makeBlock.build("solar_panel_block",
                p -> new DynamicMultiblockEntityBlock<>(BlockEntities.SOLAR_PANEL_ENTITY.getType(), false, p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<WorkstationBlock> WORKSTATION_BLOCK
                = makeBlock.build("workstation_block",
                WorkstationBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<WorkstationBlock.WorkstationPart> WORKSTATION_PART_BLOCK
                = makeBlock.build("workstation_part_block",
                WorkstationBlock.WorkstationPart::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MachineEntityBlock<RubberExtractionEntity>> RUBBER_EXTRACTION_BLOCK
                = makeBlock.build("rubber_extraction_block",
                p -> new MachineEntityBlock<>(BlockEntities.RUBBER_EXTRACTION_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MultiblockInvisibleBlock<FlotationEntity>> FROTH_FLOTATION_BLOCK
                = makeBlock.build("froth_flotation_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.FROTH_FLOTATION_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MultiblockInvisibleBlock<FluidTankMultiblockEntity>> FLUID_TANK_MULTIBLOCK_BLOCK
                = makeBlock.build("fluid_tank_multiblock_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.FLUID_TANK_MULTIBLOCK_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<GroundSampleDrillBlock> GROUND_SAMPLE_DRILL_BLOCK = makeBlock.build("ground_sample_drill_block", GroundSampleDrillBlock::new,
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<ExotekBlock> FIRE_RESISTANT_BRICK = makeBlock.build("fire_resistant_block", ExotekBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.TERRACOTTA_BROWN)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F), true);

        public static final makeBlock<ExotekBlock> COKE_OVEN_BRICK = makeBlock.build("coke_oven_brick_block", ExotekBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.TERRACOTTA_YELLOW)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F), true);

        public static final makeBlock<ScaffoldBlock> STEEL_SCAFFOLD_BLOCK = makeBlock.build("steel_scaffold_block", ScaffoldBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F), true);

        public static final makeBlock<ExotekBlock> TREATED_PLANK = makeBlock.build("creosote_treated_plank", ExotekBlock::new,
                BlockBehaviour.Properties.of()
                        .mapColor(MapColor.WOOD)
                        .sound(SoundType.WOOD)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 3.0F), true);

        public static final makeBlock<MultiblockBlock<ExotekBlastFurnaceEntity>> BLAST_FURNACE_MANAGER_BLOCK = makeBlock.build("blast_furnace_block",
                p -> new MultiblockBlock<>(BlockEntities.BLAST_FURNACE_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MultiblockBlock<IndustrialBlastFurnaceEntity>> INDUSTRIAL_BLAST_FURNACE_MANAGER_BLOCK = makeBlock.build("industrial_blast_furnace_block",
                p -> new MultiblockBlock<>(BlockEntities.INDUSTRIAL_BLAST_FURNACE_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MultiblockBlock<CokeOvenEntity>> COKE_OVEN_MANAGER_BLOCK = makeBlock.build("coke_oven_block",
                p -> new MultiblockBlock<>(BlockEntities.COKE_OVEN_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MultiblockInvisibleBlock<PumpjackEntity>> PUMP_JACK_MANAGER_BLOCK = makeBlock.build("pump_jack_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.PUMP_JACK_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MultiblockInvisibleBlock<OilDistillationTowerEntity>> DISTILLATION_TOWER_BLOCK = makeBlock.build("distillation_tower_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.DISTILLATION_TOWER.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MultiblockInvisibleBlock<BoilerEntity>> BOILER_BLOCK = makeBlock.build("boiler_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.BOILER_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MultiblockInvisibleBlock<TurbineEntity>> TURBINE_BLOCK = makeBlock.build("turbine_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.TURBINE_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MultiblockInvisibleBlock<AlternatorEntity>> ALTERNATOR_BLOCK = makeBlock.build("alternator_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.ALTERNATOR_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        public static final makeBlock<MachineEntityBlock<FluidTankSingleEntity>> IRON_FLUID_TANK_BLOCK = makeBlock.build("iron_fluid_tank_block",
                p -> new MachineEntityBlock<>(BlockEntities.IRON_FLUID_TANK_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<FluidPipeBlock> FLUID_PIPE_BLOCK = makeBlock.build("fluid_pipe_block_basic",
                p -> new FluidPipeBlock(CapabilityTiers.BASIC, p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<FluidPipeBlock> FLUID_PIPE_BLOCK2 = makeBlock.build("fluid_pipe_block_advanced",
                p -> new FluidPipeBlock(CapabilityTiers.ADVANCED, p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<FluidPipeBlock> FLUID_PIPE_BLOCK3 = makeBlock.build("fluid_pipe_block_hyper",
                p -> new FluidPipeBlock(CapabilityTiers.HYPER, p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .noOcclusion()
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<MultiblockInvisibleBlock<MinerEntity>> MINER_BLOCK
                = makeBlock.build("miner_block",
                p -> new MultiblockInvisibleBlock<>(BlockEntities.MINER_ENTITY.getType(), p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), false);

        // energy bank stuff

        public static final makeBlock<DynamicMultiblockEntityBlock<EnergyBankCasingEntity>> ENERGY_BANK_CASING_BLOCK
                = makeBlock.build("energy_bank_casing_block",
                p -> new DynamicMultiblockEntityBlock<>(BlockEntities.ENERGY_BANK_CASING.getType(), true, p),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        // these wont be visible
        public static final makeBlock<EnergyCellBlock> ENERGY_CELL_BASIC_BLOCK
                = makeBlock.build("energy_bank_cell_basic_block",
                p -> new EnergyCellBlock(p, CapabilityTiers.BASIC),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<EnergyCellBlock> ENERGY_CELL_ADVANCED_BLOCK
                = makeBlock.build("energy_bank_cell_advanced_block",
                p -> new EnergyCellBlock(p, CapabilityTiers.ADVANCED),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);

        public static final makeBlock<EnergyCellBlock> ENERGY_CELL_HYPER_BLOCK
                = makeBlock.build("energy_bank_cell_hyper_block",
                p -> new EnergyCellBlock(p, CapabilityTiers.HYPER),
                (BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)), true);


        public static class makeBlock<T extends Block> implements Supplier<T> {

            private final RegistryObject<T> block;

            private final String name;

            private makeBlock(String name, RegistryObject<T> block) {
                this.name = name;
                this.block = block;
            }

            public static <T extends Block> makeBlock<T> build(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop, boolean asItem) {
                RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
                if (asItem) {
                    RegistryObject<Item> item = ITEMS.register(name, () -> new BlockItem(ret.get(), new Item.Properties()));
                    ALL_ITEMS.add(item);
                }
                all_blocks.add((RegistryObject<Block>) ret);
                return new makeBlock<>(name, ret);
            }

            public static <T extends Block> makeBlock<T> buildCustomSuppliedItem(String name, Function<BlockBehaviour.Properties, T> block, BlockBehaviour.Properties prop, Supplier<BlockItem> itemSupplier) {
                RegistryObject<T> ret = BLOCKS.register(name, () -> block.apply(prop));
                RegistryObject<Item> item = ITEMS.register(name, itemSupplier);
                ALL_ITEMS.add(item);
                all_blocks.add((RegistryObject<Block>) ret);
                return new makeBlock<>(name, ret);
            }

            public RegistryObject<T> getRegistry() {
                return block;
            }

            @Override
            public T get() {
                return this.block.get();
            }

            public String getName() {
                return name;
            }
        }
    }

    public static final class BlockEntities {

        public static void init() {
        }

        public static final makeBlockEntity<BurnableGeneratorEntity> BURNABLE_GENERATOR_ENTITY
                = makeBlockEntity.build("burnable_generator_entity", BurnableGeneratorEntity::new, Blocks.BURNABLE_GENERATOR_BLOCK);
        public static final makeBlockEntity<CentrifugeEntity> CENTRIFUGE_ENTITY
                = makeBlockEntity.build("centrifuge_entity", CentrifugeEntity::new, Blocks.CENTRIFUGE_BLOCK);
        public static final makeBlockEntity<ChemicalReactionChamberEntity> CHEMICAL_REACTOR_CHAMBER_ENTITY
                = makeBlockEntity.build("chemical_reactor_chamber_entity", ChemicalReactionChamberEntity::new, Blocks.CHEMICAL_REACTOR_CHAMBER_BLOCK);
        public static final makeBlockEntity<CircuitAssemblyEntity> CIRCUIT_ASSEMBLY_ENTITY
                = makeBlockEntity.build("circuit_assembly_entity", CircuitAssemblyEntity::new, Blocks.CIRCUIT_ASSEMBLY_BLOCK);
        public static final makeBlockEntity<CompressorEntity> COMPRESSOR_ENTITY
                = makeBlockEntity.build("compressor_entity", CompressorEntity::new, Blocks.COMPRESSOR_BLOCK);
        public static final makeBlockEntity<ElectrolyzerEntity> ELECTROLYZER_ENTITY
                = makeBlockEntity.build("electrolyzer_entity", ElectrolyzerEntity::new, Blocks.ELECTROLYZER_BLOCK);
        public static final makeBlockEntity<EngravingEntity> ENGRAVING_ENTITY
                = makeBlockEntity.build("engraving_entity", EngravingEntity::new, Blocks.ENGRAVING_BLOCK);
        public static final makeBlockEntity<FurnaceEntity> FURNACE_ENTITY
                = makeBlockEntity.build("furnace_entity", FurnaceEntity::new, Blocks.FURNACE_BLOCK);
        public static final makeBlockEntity<MaceratorEntity> MACERATOR_ENTITY
                = makeBlockEntity.build("macerator_entity", MaceratorEntity::new, Blocks.MACERATOR_BLOCK);
        public static final makeBlockEntity<ManufactoryEntity> MANUFACTORY_ENTITY
                = makeBlockEntity.build("manufactory_entity", ManufactoryEntity::new, Blocks.MANUFACTORY_BLOCK);
        public static final makeBlockEntity<SolarPanelEntity> SOLAR_PANEL_ENTITY
                = makeBlockEntity.build("solar_panel_entity", SolarPanelEntity::new, Blocks.SOLAR_PANEL_BLOCK);
        public static final makeBlockEntity<WashingPlantEntity> WASHING_PLANT_ENTITY
                = makeBlockEntity.build("washing_plant_entity", WashingPlantEntity::new, Blocks.WASHING_PLANT_BLOCK);
        public static final makeBlockEntity<RubberExtractionEntity> RUBBER_EXTRACTION_ENTITY
                = makeBlockEntity.build("rubber_extraction_entity", RubberExtractionEntity::new, Blocks.RUBBER_EXTRACTION_BLOCK);
        public static final makeBlockEntity<FlotationEntity> FROTH_FLOTATION_ENTITY
                = makeBlockEntity.build("froth_flotation_entity", FlotationEntity::new, Blocks.FROTH_FLOTATION_BLOCK);
        public static final makeBlockEntity<FluidTankMultiblockEntity> FLUID_TANK_MULTIBLOCK_ENTITY
                = makeBlockEntity.build("fluid_tank_multiblock_entity", FluidTankMultiblockEntity::new, Blocks.FLUID_TANK_MULTIBLOCK_BLOCK);
        public static final makeBlockEntity<BrinePoolEntity> BRINE_POOL_ENTITY
                = makeBlockEntity.build("brine_pool_entity", BrinePoolEntity::new, Blocks.BRINE_POOL_BLOCK);
        public static final makeBlockEntity<GroundSampleDrillEntity> GROUND_SAMPLE_DRILL_ENTITY
                = makeBlockEntity.build("ground_sample_drill", GroundSampleDrillEntity::new, Blocks.GROUND_SAMPLE_DRILL_BLOCK);
        public static final makeBlockEntity<IndustrialBlastFurnaceEntity> INDUSTRIAL_BLAST_FURNACE_ENTITY
                = makeBlockEntity.build("industrial_blast_furnace_entity", IndustrialBlastFurnaceEntity::new, Blocks.INDUSTRIAL_BLAST_FURNACE_MANAGER_BLOCK);
        public static final makeBlockEntity<ExotekBlastFurnaceEntity> BLAST_FURNACE_ENTITY
                = makeBlockEntity.build("blast_furnace_entity", ExotekBlastFurnaceEntity::new, Blocks.BLAST_FURNACE_MANAGER_BLOCK);
        public static final makeBlockEntity<CokeOvenEntity> COKE_OVEN_ENTITY
                = makeBlockEntity.build("coke_oven_entity", CokeOvenEntity::new, Blocks.COKE_OVEN_MANAGER_BLOCK);
        public static final makeBlockEntity<MultiblockInvisibleEntity> MULTIBLOCK_INVISIBLE_ENTITY
                = makeBlockEntity.build("multiblock_invisible_entity", MultiblockInvisibleEntity::new, Blocks.MULTIBLOCK_INVISIBLE_BLOCK);
        public static final makeBlockEntity<FluidTankSingleEntity> IRON_FLUID_TANK_ENTITY
                = makeBlockEntity.build("iron_fluid_tank_entity", FluidTankSingleEntity::new, Blocks.IRON_FLUID_TANK_BLOCK);
        public static final makeBlockEntity<FluidPipeEntity> FLUID_PIPE_ENTITY
                = makeBlockEntity.build("fluid_pipe_entity", FluidPipeEntity::new, Blocks.FLUID_PIPE_BLOCK);
        public static final makeBlockEntity<PumpjackEntity> PUMP_JACK_ENTITY
                = makeBlockEntity.build("pump_jack_entity", PumpjackEntity::new, Blocks.PUMP_JACK_MANAGER_BLOCK);
        public static final makeBlockEntity<OilDistillationTowerEntity> DISTILLATION_TOWER
                = makeBlockEntity.build("distillation_tower_entity", OilDistillationTowerEntity::new, Blocks.DISTILLATION_TOWER_BLOCK);
        public static final makeBlockEntity<BoilerEntity> BOILER_ENTITY
                = makeBlockEntity.build("boiler_entity", BoilerEntity::new, Blocks.BOILER_BLOCK);
        public static final makeBlockEntity<TurbineEntity> TURBINE_ENTITY
                = makeBlockEntity.build("turbine_entity", TurbineEntity::new, Blocks.TURBINE_BLOCK);
        public static final makeBlockEntity<AlternatorEntity> ALTERNATOR_ENTITY
                = makeBlockEntity.build("alternator_entity", AlternatorEntity::new, Blocks.ALTERNATOR_BLOCK);
        public static final makeBlockEntity<MinerEntity> MINER_ENTITY
                = makeBlockEntity.build("miner_entity", MinerEntity::new, Blocks.MINER_BLOCK);
        public static final makeBlockEntity<WorkstationEntity> WORKSTATION_ENTITY
                = makeBlockEntity.build("workstation_entity", WorkstationEntity::new, Blocks.WORKSTATION_BLOCK);
        public static final RegistryObject<BlockEntityType<ConveyorBeltEntity>> CONVEYOR_BELT_ENTITY
                = BLOCK_ENTITIES.register("conveyor_belt_entity",
                () -> BlockEntityType.Builder.of(ConveyorBeltEntity::new, Blocks.CONVEYOR_BELT_BLOCK.get()).build(null));
        public static final RegistryObject<BlockEntityType<ConveyorBeltRampEntity>> CONVEYOR_BELT_RAMP_ENTITY
                = BLOCK_ENTITIES.register("conveyor_belt_ramp_entity",
                () -> BlockEntityType.Builder.of(ConveyorBeltRampEntity::new, Blocks.CONVEYOR_BELT_RAMP_BLOCK.get()).build(null));
        public static final RegistryObject<BlockEntityType<ConveyorBeltRampEntity.Top>> CONVEYOR_BELT_RAMP_TOP_ENTITY
                = BLOCK_ENTITIES.register("conveyor_belt_ramp_top_entity",
                () -> BlockEntityType.Builder.of(ConveyorBeltRampEntity.Top::new, Blocks.CONVEYOR_BELT_RAMP_TOP_BLOCK.get()).build(null));
        public static final RegistryObject<BlockEntityType<EnergyCableEntity>> ENERGY_CABLE_ENTITY
                = BLOCK_ENTITIES.register("energy_cable_entity",
                () -> BlockEntityType.Builder.of(EnergyCableEntity::new, Blocks.ENERGY_CABLE_BLOCK.get(), Blocks.ENERGY_CABLE_2_BLOCK.get(),Blocks.ENERGY_CABLE_3_BLOCK.get()).build(null));

        // energy bank casing
        public static final makeBlockEntity<EnergyBankCasingEntity> ENERGY_BANK_CASING
                = makeBlockEntity.build("energy_bank_casing", EnergyBankCasingEntity::new, Blocks.ENERGY_BANK_CASING_BLOCK);


        public static class makeBlockEntity<T extends BlockEntity> implements Supplier<BlockEntityType<T>> {

            private final RegistryObject<BlockEntityType<T>> type;

            public static <T extends BlockEntity> makeBlockEntity<T> build(
                    String name,
                    BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier,
                    Supplier<? extends Block> valid) {
                return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, valid.get()).build(null)));
            }

            public static <T extends BlockEntity> makeBlockEntity<T> buildEntityAndBlock(
                    String name,
                    BlockEntityType.BlockEntitySupplier<T> blockEntitySupplier,
                    Blocks.makeBlock<?> valid) {
                return new makeBlockEntity<T>(BLOCK_ENTITIES.register(name, () -> BlockEntityType.Builder.of(blockEntitySupplier, valid.get()).build(null)));
            }

            public makeBlockEntity(RegistryObject<BlockEntityType<T>> type) {
                this.type = type;
            }

            @Override
            public BlockEntityType<T> get() {
                return this.getType().get();
            }

            public RegistryObject<BlockEntityType<T>> getType() {
                return type;
            }
        }

    }

    public static final class Menus {
        public static void init() {
        }

        public static final RegistryObject<MenuType<BurnableGeneratorMenu>> BURNABLE_GENERATOR_GUI = MENUS.register("burnable_generator_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new BurnableGeneratorMenu(windowId, inv, inv.player, (BurnableGeneratorEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<BoilerMenu>> BOILER_GUI = MENUS.register("boiler_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new BoilerMenu(windowId, inv, inv.player, (BoilerEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<TurbineMenu>> TURBINE_GUI = MENUS.register("turbine_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new TurbineMenu(windowId, inv, inv.player, (TurbineEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<FlotationMenu>> FLOTATION_GUI = MENUS.register("flotation_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new FlotationMenu(windowId, inv, inv.player, (FlotationEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<FurnaceMenu>> FURNACE_GUI = MENUS.register("furnace", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new FurnaceMenu(windowId, inv, inv.player, (FurnaceEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<ManufactoryMenu>> MANUFACTORY_GUI = MENUS.register("manufactory_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new ManufactoryMenu(windowId, inv, inv.player, (ManufactoryEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<SolarPanelMenu>> SOLAR_PANEL_GUI = MENUS.register("solar_panel_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new SolarPanelMenu(windowId, inv, inv.player, (SolarPanelEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<ElectrolyzerMenu>> ELECTROLYZER_GUI = MENUS.register("electrolyzer_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new ElectrolyzerMenu(windowId, inv, inv.player, (ElectrolyzerEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<FluidTankMultiblockMenu>> FLUID_TANK_MULTIBLOCK_GUI = MENUS.register("fluid_tank_multiblock_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new FluidTankMultiblockMenu(windowId, inv, inv.player, (FluidTankMultiblockEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<EnergyBankMenu>> ENERGY_BANK_GUI = MENUS.register("energy_bank_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new EnergyBankMenu(windowId, inv, inv.player, (EnergyBankCasingEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<FluidTankMenu>> FLUID_TANK_GUI = MENUS.register("fluid_tank_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new FluidTankMenu(windowId, inv, inv.player, (FluidTankSingleEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<MaceratorMenu>> MACERATOR_GUI = MENUS.register("macerator_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new MaceratorMenu(windowId, inv, inv.player, (MaceratorEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<CompressorMenu>> COMPRESSOR_GUI = MENUS.register("compressor_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new CompressorMenu(windowId, inv, inv.player, (CompressorEntity) level.getBlockEntity(pos));
        }));


        public static final RegistryObject<MenuType<CentrifugeMenu>> CENTRIFUGE_GUI = MENUS.register("centrifuge_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new CentrifugeMenu(windowId, inv, inv.player, (CentrifugeEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<RubberExtractionMenu>> RUBBER_EXTRACTION_GUI = MENUS.register("rubber_extraction_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new RubberExtractionMenu(windowId, inv, inv.player, (RubberExtractionEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<CircuitAssemblyMenu>> CIRCUIT_ASSEMBLY_GUI = MENUS.register("circuit_assembly_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new CircuitAssemblyMenu(windowId, inv, inv.player, (CircuitAssemblyEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<WashingPlantMenu>> WASHING_PLANT_GUI = MENUS.register("washing_plant_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new WashingPlantMenu(windowId, inv, inv.player, (WashingPlantEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<ChemicalReactorMenu>> CHEMICAL_REACTOR_GUI = MENUS.register("chemical_reactor_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new ChemicalReactorMenu(windowId, inv, inv.player, (ChemicalReactionChamberEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<EngraverMenu>> ENGRAVER_GUI = MENUS.register("engraver_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new EngraverMenu(windowId, inv, inv.player, (EngravingEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<ExotekBlastFurnaceMenu>> BLAST_FURNACE_GUI = MENUS.register("blast_furnace_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new ExotekBlastFurnaceMenu(windowId, inv, inv.player, (ExotekBlastFurnaceEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<WorkstationMenu>> WORKSTATION_GUI = MENUS.register("workstation_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new WorkstationMenu(windowId, inv, inv.player, (WorkstationEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<CokeOvenMenu>> COKE_OVEN_GUI = MENUS.register("coke_oven_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new CokeOvenMenu(windowId, inv, inv.player, (CokeOvenEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<IndustrialBlastFurnaceMenu>> INDUSTRIAL_BLAST_FURNACE_GUI = MENUS.register("industrial_blast_furnace_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new IndustrialBlastFurnaceMenu(windowId, inv, inv.player, (IndustrialBlastFurnaceEntity) level.getBlockEntity(pos));
        }));

        public static final RegistryObject<MenuType<EnergyCableMenu>> ENERGY_CABLE_MENU = MENUS.register("energy_cable_gui", () -> IForgeMenuType.create((windowId, inv, data) -> {
            BlockPos pos = data.readBlockPos();
            Level level = inv.player.level();
            return new EnergyCableMenu(windowId, inv, inv.player, (EnergyCableEntity) level.getBlockEntity(pos));
        }));

    }

    public static final class Fluids {

        public static void init() {
        }

        public static final makeFluid<ExotekFluidType> REDSTONE_ACID = makeFluid.build("redstone_acid",
                true, true, () -> ExotekFluidType.createColoured(0xFFFC3100, false));
        public static final makeFluid<ExotekFluidType> HYDROCHLORIC_ACID = makeFluid.build("hydrochloric_acid",
                true, true, () -> ExotekFluidType.createColoured(0xFF67e3ea, false));
        public static final makeFluid<ExotekFluidType> SULPHURIC_ACID = makeFluid.build("sulphuric_acid",
                true, true, () -> ExotekFluidType.createColoured(0xFF850F, false));
        public static final makeFluid<ExotekFluidType> GLUE = makeFluid.build("glue",
                true, true, () -> ExotekFluidType.createColoured(0xFFF4FFA6, false));
        public static final makeFluid<ExotekFluidType> SOLDERING_FLUID = makeFluid.build("soldering_fluid",
                true, true, () -> ExotekFluidType.createColoured(0xFF919191, false));
        public static final makeFluid<ExotekFluidType> BRINE = makeFluid.build("brine",
                true, true, () -> ExotekFluidType.createColoured(0xFFffd51c, false));
        public static final makeFluid<ExotekFluidType> STEAM = makeFluid.build("steam",
                false, false, () -> ExotekFluidType.createColoured(FluidType.Properties.create().temperature(300), 0xFFc7d5e0, true));
        public static final makeFluid<ExotekFluidType> PETROLEUM = makeFluid.build("petroleum",
                false, false, () -> ExotekFluidType.createColoured(FluidType.Properties.create().density(3000).viscosity(6000), 0xFF1C1E1B, false));
        public static final makeFluid<ExotekFluidType> PETROL = makeFluid.build("petrol",
                false, false, () -> ExotekFluidType.createColoured(FluidType.Properties.create(), 0xFFB57E12, false));
        public static final makeFluid<ExotekFluidType> DIESEL = makeFluid.build("diesel",
                false, false, () -> ExotekFluidType.createColoured(FluidType.Properties.create(), 0xFF694705, false));
        public static final makeFluid<ExotekFluidType> LUBRICANT = makeFluid.build("lubricant",
                false, false, () -> ExotekFluidType.createColoured(FluidType.Properties.create(), 0x4fc92b, false));

        public static final List<makeFluid<?>> FLUID_LIST = new ArrayList<>();

        public static class makeFluid<T extends FluidType> implements Supplier<T> {

            private RegistryObject<T> fluidType;
            private RegistryObject<Fluid> fluid;
            private RegistryObject<FlowingFluid> flowingFluid;
            private RegistryObject<ExotekFluidBlock> block;
            private RegistryObject<Item> bucket;
            private ForgeFlowingFluid.Properties properties;

            private final String name;

            public makeFluid(RegistryObject<T> fluidType, String name, boolean placeable, boolean needsBucket) {
                this.fluidType = fluidType;
                this.name = name;
                this.properties = new ForgeFlowingFluid.Properties(this.fluidType, makeSource(name), makeFlowing(name));
                if (placeable) {
                    this.properties.block(makeBlock(name));
                }
                if (needsBucket) {
                    this.properties.bucket(makeBucket(name));
                }
               // FLUID_LIST.add(this);
            }

            public static <T extends FluidType> makeFluid<T> build(String name, boolean placeable, boolean bucket, Supplier<T> fluid) {
                RegistryObject<T> type = FLUID_TYPES.register(name, fluid);
                return new makeFluid<>(type, name, placeable, bucket);
            }


            private RegistryObject<Fluid> makeSource(String name) {
                this.fluid = FLUIDS.register(name, () -> new ForgeFlowingFluid.Source(this.properties));
                return fluid;
            }

            private RegistryObject<FlowingFluid> makeFlowing(String name) {
                this.flowingFluid = FLUIDS.register(name + "_flowing", () -> new ForgeFlowingFluid.Flowing(this.properties));
                return flowingFluid;
            }

            private RegistryObject<ExotekFluidBlock> makeBlock(String name) {
                this.block = BLOCKS.register(name + "_block",
                        () -> new ExotekFluidBlock(this.flowingFluid, BlockBehaviour.Properties.of()
                                .mapColor(MapColor.NONE)
                                .replaceable()
                                .noCollission()
                                .strength(100.0F)
                                .pushReaction(PushReaction.DESTROY)
                                .noLootTable()
                                .liquid()
                                .sound(SoundType.EMPTY)));
                return block;
            }

            private RegistryObject<Item> makeBucket(String name) {
                this.bucket = ITEMS.register(name + "_bucket",
                        () -> new BucketItem(this.fluid, new Item.Properties()
                                .craftRemainder(net.minecraft.world.item.Items.BUCKET)
                                .stacksTo(1)));
                ALL_ITEMS.add(this.bucket);
                return bucket;
            }

            @Override
            public T get() {
                return this.fluidType.get();
            }

            public String getName() {
                return name;
            }

            public ForgeFlowingFluid.Properties getProperties() {
                return properties;
            }

            public RegistryObject<FlowingFluid> getFlowingFluid() {
                return flowingFluid;
            }

            public RegistryObject<Fluid> getFluid() {
                return fluid;
            }

            public RegistryObject<Item> getBucket() {
                return bucket;
            }

            public RegistryObject<ExotekFluidBlock> getBlock() {
                return block;
            }

            public RegistryObject<T> getType() {
                return fluidType;
            }
        }

    }

    public static final class Entities {

        public static final RegistryObject<EntityType<ConveyorBeltItemStack>> TRANSPORTING_ITEM =
                ENTITIES.register("transporting_item",
                        () -> EntityType.Builder.<ConveyorBeltItemStack>of(ConveyorBeltItemStack::new,
                                MobCategory.MISC)
                                .sized(0.25f, 0.25f)
                                .clientTrackingRange(6)
                                .updateInterval(20)
                                .build("transporting_item"));


        public static void init() {

        }

    }

}
