package mod.kerzox.exotek;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.client.gui.screen.*;
import mod.kerzox.exotek.client.gui.screen.multiblock.*;
import mod.kerzox.exotek.client.gui.screen.transfer.EnergyCableScreen;
import mod.kerzox.exotek.client.render.entity.ConveyorBeltItemStackRenderer;
import mod.kerzox.exotek.client.render.event.ClientMouseEvents;
import mod.kerzox.exotek.client.render.event.RenderLevelNetworks;
import mod.kerzox.exotek.client.render.machine.SingleBlockMinerRenderer;
import mod.kerzox.exotek.client.render.multiblock.*;
import mod.kerzox.exotek.client.render.transfer.*;
import mod.kerzox.exotek.client.render.types.ExoRenderTypes;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.ConveyorBeltSplitter;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.RegistryObject;

import static mod.kerzox.exotek.registry.Material.MATERIALS;

@Mod.EventBusSubscriber(modid = Exotek.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(ExotekRegistry.Menus.FURNACE_GUI.get(), FurnaceScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.BURNABLE_GENERATOR_GUI.get(), BurnableGeneratorScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.MACERATOR_GUI.get(), MaceratorScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.COMPRESSOR_GUI.get(), CompressorScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.ELECTROLYZER_GUI.get(), ElectrolyzerScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.FLOTATION_GUI.get(), FlotationScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.ENGRAVER_GUI.get(), EngraverScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.WASHING_PLANT_GUI.get(), WashingPlantScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.CHEMICAL_REACTOR_GUI.get(), ChemicalReactorScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.CIRCUIT_ASSEMBLY_GUI.get(), CircuitAssemblyScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.CENTRIFUGE_GUI.get(), CentrifugeScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.MANUFACTORY_GUI.get(), ManufactoryScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.RUBBER_EXTRACTION_GUI.get(), RubberExtractionScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.BLAST_FURNACE_GUI.get(), ExotekBlastFurnaceScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.COKE_OVEN_GUI.get(), CokeOvenScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.INDUSTRIAL_BLAST_FURNACE_GUI.get(), IndustrialBlastFurnaceScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.ENERGY_CABLE_MENU.get(), EnergyCableScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.FLUID_TANK_MULTIBLOCK_GUI.get(), FluidTankMultiblockScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.WORKSTATION_GUI.get(), WorkstationScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.SOLAR_PANEL_GUI.get(), SolarPanelScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.BOILER_GUI.get(), BoilerScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.TURBINE_GUI.get(), TurbineScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.FLUID_TANK_GUI.get(), FluidTankScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.ENERGY_BANK_GUI.get(), EnergyBankScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.SINGLE_BLOCK_MINER_GUI.get(), SingleBlockMinerScreen::new);
            MenuScreens.register(ExotekRegistry.Menus.STORAGE_CRATE_GUI.get(), StorageCrateScreen::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.FLUID_PIPE_ENTITY.get(), FluidPipeRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.MULTIBLOCK_INVISIBLE_ENTITY.get(), MultiblockEntityRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.PUMP_JACK_ENTITY.get(), PumpjackRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.DISTILLATION_TOWER.get(), DistillationTowerRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.get(), GroundSampleDrillRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.MULTIBLOCK_MINER_ENTITY.get(), MinerRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.ENERGY_CABLE_ENTITY.get(), EnergyCableRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.FLUID_TANK_MULTIBLOCK_ENTITY.get(), FluidTankMultiblockRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.FROTH_FLOTATION_ENTITY.get(), FrothFlotationRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.BRINE_POOL_ENTITY.get(), BrinePoolRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.BOILER_ENTITY.get(), BoilerRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.ALTERNATOR_ENTITY.get(), AlternatorRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.TURBINE_ENTITY.get(), TurbineRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.ENERGY_BANK_CASING.get(), EnergyBankCasingRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.CONVEYOR_BELT_ENTITY.get(), ConveyorBeltRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.CONVEYOR_BELT_RAMP_ENTITY.get(), ConveyorBeltRampRenderer::new);
            BlockEntityRenderers.register(ExotekRegistry.BlockEntities.MINER_DRILL_ENTITY.get(), SingleBlockMinerRenderer::new);
            EntityRenderers.register(ExotekRegistry.Entities.TRANSPORTING_ITEM.get(), ConveyorBeltItemStackRenderer::new);
        });

        MinecraftForge.EVENT_BUS.register(new RenderLevelNetworks());
        MinecraftForge.EVENT_BUS.register(new ClientMouseEvents());

    }

    @SubscribeEvent
    public static void colourBlocks(RegisterColorHandlersEvent.Block event){
        event.register((state, level, pos, tint) -> 0x7A7A7A, ExotekRegistry.Blocks.INDUSTRIAL_BLAST_FURNACE_MANAGER_BLOCK.get());
        event.register((state, level, pos, tint) -> 0x7A7A7A, ExotekRegistry.Blocks.STEEL_SLAB_BLOCK.get());
        for (Material material : MATERIALS.values()) {
            for (Pair<RegistryObject<Block>, RegistryObject<Item>> value : material.getObjects().values()) {
                if (value.getFirst() != null) {
                    event.register((state, level, pos, tint) -> material.getTint(), value.getFirst().get());
                }
            }
        }
    }

    @SubscribeEvent
    public static void colourItems(RegisterColorHandlersEvent.Item event) {
        event.register((state, tint) -> 0x7A7A7A, ExotekRegistry.Blocks.STEEL_SLAB_BLOCK.get().asItem());
        for (Material material : MATERIALS.values()) {
            for (Material.Component component : material.getComponents()) {
                if (component.isFluid()) continue;
                RegistryObject<Item> item = material.getComponentPair(component).getSecond();
                if (item != null) {
                    if (component.isBlock()) {
                        event.register((stack, tint) -> material.getTint(), item.get());
                    } else if (component == Material.Component.MICRO_WIRE) {
                        event.register((stack, tint) -> tint == 1 ? material.getTint() : -1, item.get());
                    } else {
                        event.register((stack, tint) -> tint == 0 ? material.getTint() : -1, item.get());
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onModelRegister(ModelEvent.RegisterAdditional event) {
        event.register(FluidPipeRenderer.CONNECTION_MODEL_EAST);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_WEST);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_NORTH);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_SOUTH);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_UP);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_DOWN);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER1);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER2);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER3);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER4);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER5);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER6);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER7);
        event.register(FluidPipeRenderer.CONNECTION_MODEL_CORNER8);
        event.register(FluidPipeRenderer.CONNECTED_BODY_AXIS_Z);
        event.register(FluidPipeRenderer.CONNECTED_BODY_AXIS_X);
        event.register(FluidPipeRenderer.CONNECTED_BODY_AXIS_Y);
        event.register(FluidPipeRenderer.BODY);
        event.register(FluidPipeRenderer.BIG_BODY);

        event.register(EnergyCableRenderer.CONNECTED_NORTH);
        event.register(EnergyCableRenderer.CONNECTED_WEST);
        event.register(EnergyCableRenderer.CONNECTED_EAST);
        event.register(EnergyCableRenderer.CONNECTED_SOUTH);
        event.register(EnergyCableRenderer.CONNECTED_UP);
        event.register(EnergyCableRenderer.CONNECTED_DOWN);
        event.register(EnergyCableRenderer.CORE);
        event.register(EnergyCableRenderer.CONNECTED_NORTH2);
        event.register(EnergyCableRenderer.CONNECTED_WEST2);
        event.register(EnergyCableRenderer.CONNECTED_EAST2);
        event.register(EnergyCableRenderer.CONNECTED_SOUTH2);
        event.register(EnergyCableRenderer.CONNECTED_UP2);
        event.register(EnergyCableRenderer.CONNECTED_DOWN2);
        event.register(EnergyCableRenderer.CORE2);
        event.register(EnergyCableRenderer.CONNECTED_NORTH3);
        event.register(EnergyCableRenderer.CONNECTED_WEST3);
        event.register(EnergyCableRenderer.CONNECTED_EAST3);
        event.register(EnergyCableRenderer.CONNECTED_SOUTH3);
        event.register(EnergyCableRenderer.CONNECTED_UP3);
        event.register(EnergyCableRenderer.CONNECTED_DOWN3);
        event.register(EnergyCableRenderer.CORE3);

        event.register(DistillationTowerRenderer.DISTILLATION_TOWER_MODEL);
        event.register(FluidTankMultiblockRenderer.FLUID_TANK_MODEL);
        event.register(BoilerRenderer.BOILER);
        event.register(FrothFlotationRenderer.FLOTATION_MODEL);
        event.register(AlternatorRenderer.MODEL);
        event.register(TurbineRenderer.MODEL);
        event.register(TurbineRenderer.SHAFT);

        event.register(BrinePoolRenderer.POOL);
        event.register(BrinePoolRenderer.EAST_WALL);
        event.register(BrinePoolRenderer.WEST_WALL);
        event.register(BrinePoolRenderer.NORTH_WALL);
        event.register(BrinePoolRenderer.SOUTH_WALL);

        event.register(EnergyBankCasingRenderer.MODEL_UNFORMED);
        event.register(EnergyBankCasingRenderer.MODEL_FORMED);
        event.register(EnergyBankCasingRenderer.MODEL_FRAME);

        event.register(ConveyorBeltRenderer.BELT_MODEL);
        event.register(ConveyorBeltPorterRenderer.PORTER_MODEL);
        event.register(ConveyorBeltSplitter.SPLITTER_MODEL);
    }

    @SubscribeEvent
    public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event) {
        System.out.println("Model bake");
//        for (BlockState blockState : Registry.Blocks.MULTIBLOCK_INVISIBLE_BLOCK.get().getStateDefinition().getPossibleStates()) {
//            ModelResourceLocation variantMRL = BlockModelShaper.stateToModelLocation(blockState);
//            BakedModel existingModel = event.getModels().get(variantMRL);
//            if (existingModel == null) {
//                System.out.println("Did not find the expected vanilla baked model(s) for blockCamouflage in registry");
//            } else if (existingModel instanceof MultiblockMimicBakedModel) {
//                System.out.println("Tried to replace CamouflagedBakedModel twice");
//            } else {
//                System.out.println("Adding multiblock mimic block baked model");
//                MultiblockMimicBakedModel customModel = new MultiblockMimicBakedModel(existingModel);
//                event.getModels().put(variantMRL, customModel);
//            }
//        }
    }

}
