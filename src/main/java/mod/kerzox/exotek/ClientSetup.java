package mod.kerzox.exotek;

import mod.kerzox.exotek.client.gui.screen.*;
import mod.kerzox.exotek.client.gui.screen.transfer.EnergyCableScreen;
import mod.kerzox.exotek.client.model.baked.MultiblockMimicBakedModel;
import mod.kerzox.exotek.client.render.multiblock.DistillationTowerRenderer;
import mod.kerzox.exotek.client.render.pipe.EnergyCableRenderer;
import mod.kerzox.exotek.client.render.pipe.FluidPipeRenderer;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Exotek.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(FMLClientSetupEvent event) {

        event.enqueueWork(() -> {
            MenuScreens.register(Registry.Menus.FURNACE_GUI.get(), FurnaceScreen::new);
            MenuScreens.register(Registry.Menus.BURNABLE_GENERATOR_GUI.get(), BurnableGeneratorScreen::new);
            MenuScreens.register(Registry.Menus.MACERATOR_GUI.get(), MaceratorScreen::new);
            MenuScreens.register(Registry.Menus.COMPRESSOR_GUI.get(), CompressorScreen::new);
            MenuScreens.register(Registry.Menus.ELECTROLYZER_GUI.get(), ElectrolyzerScreen::new);
            MenuScreens.register(Registry.Menus.ENGRAVER_GUI.get(), EngraverScreen::new);
            MenuScreens.register(Registry.Menus.WASHING_PLANT_GUI.get(), WashingPlantScreen::new);
            MenuScreens.register(Registry.Menus.CHEMICAL_REACTOR_GUI.get(), ChemicalReactorScreen::new);
            MenuScreens.register(Registry.Menus.CIRCUIT_ASSEMBLY_GUI.get(), CircuitAssemblyScreen::new);
            MenuScreens.register(Registry.Menus.CENTRIFUGE_GUI.get(), CentrifugeScreen::new);
            MenuScreens.register(Registry.Menus.MANUFACTORY_GUI.get(), ManufactoryScreen::new);
            MenuScreens.register(Registry.Menus.RUBBER_EXTRACTION_GUI.get(), RubberExtractionScreen::new);
            MenuScreens.register(Registry.Menus.BLAST_FURNACE_GUI.get(), ExotekBlastFurnaceScreen::new);
            MenuScreens.register(Registry.Menus.COKE_OVEN_GUI.get(), CokeOvenScreen::new);
            MenuScreens.register(Registry.Menus.INDUSTRIAL_BLAST_FURNACE_GUI.get(), IndustrialBlastFurnaceScreen::new);
            MenuScreens.register(Registry.Menus.ENERGY_CABLE_MENU.get(), EnergyCableScreen::new);
        });


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

        event.register(DistillationTowerRenderer.DISTILLATION_TOWER_MODEL);
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
