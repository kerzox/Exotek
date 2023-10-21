package mod.kerzox.exotek;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import mod.kerzox.exotek.client.render.event.RenderLevelNetworks;
import mod.kerzox.exotek.client.render.multiblock.*;
import mod.kerzox.exotek.client.render.pipe.EnergyCableRenderer;
import mod.kerzox.exotek.client.render.pipe.FluidPipeRenderer;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.AlternatorEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.FlotationEntity;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.event.CommonEvents;
import mod.kerzox.exotek.common.event.TickEvent;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.Material;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

import static mod.kerzox.exotek.registry.Material.MATERIALS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Exotek.MODID)
public class Exotek
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "exotek";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
//    public static final RegistryObject<CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
//            .withTabsBefore(CreativeModeTabs.COMBAT)
//            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
//            .displayItems((parameters, output) -> {
//                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
//            }).build());



    public Exotek()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(CommonSetup::init);

        // registry
        Registry.init(modEventBus);

        // network
        PacketHandler.register();

        // event bus
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TickEvent());
        MinecraftForge.EVENT_BUS.register(new RenderLevelNetworks());
        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        // client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init));

        modEventBus.addListener(this::colourItems);
        modEventBus.addListener(this::colourBlocks);
        modEventBus.addListener(this::onEntityRenderRegister);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void onEntityRenderRegister(EntityRenderersEvent.RegisterRenderers e) {
        System.out.println("Registering Entity Renderers");
        e.registerBlockEntityRenderer(Registry.BlockEntities.FLUID_PIPE_ENTITY.get(), FluidPipeRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.MULTIBLOCK_INVISIBLE_ENTITY.get(), MultiblockEntityRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.PUMP_JACK_ENTITY.get(), PumpjackRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.DISTILLATION_TOWER.get(), DistillationTowerRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.get(), GroundSampleDrillRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.MINER_ENTITY.get(), MinerRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.ENERGY_CABLE_ENTITY.get(), EnergyCableRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.FLUID_TANK_MULTIBLOCK_ENTITY.get(), FluidTankMultiblockRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.FROTH_FLOTATION_ENTITY.get(), FrothFlotationRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.BRINE_POOL_ENTITY.get(), BrinePoolRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.BOILER_ENTITY.get(), BoilerRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.ALTERNATOR_ENTITY.get(), AlternatorRenderer::new);
        e.registerBlockEntityRenderer(Registry.BlockEntities.TURBINE_ENTITY.get(), TurbineRenderer::new);
    }

    @SubscribeEvent
    public void onCapabilityAttach(AttachCapabilitiesEvent<LevelChunk> event) {
        event.addCapability(new ResourceLocation(MODID, "deposit"), new ChunkDeposit(event.getObject()));
    }

    @SubscribeEvent
    public void onCapabilityAttachLevel(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(new ResourceLocation(MODID, "energy_cable_network"), new LevelEnergyNetwork(event.getObject()));
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == Registry.EXOTEK_TAB.getKey()) {
            for (Material material : MATERIALS.values()) {
                for (Pair<RegistryObject<Block>, RegistryObject<Item>> value : material.getObjects().values()) {
                    if (value.getSecond() != null) {
                        event.accept(value.getSecond());
                    }
                }
            }
            for (RegistryObject<Item> item : Registry.ALL_ITEMS) {
                event.accept(item);
            }
        }
    }

    public void colourBlocks(RegisterColorHandlersEvent.Block event){
        event.register((state, level, pos, tint) -> 0x7A7A7A, Registry.Blocks.INDUSTRIAL_BLAST_FURNACE_MANAGER_BLOCK.get());
        event.register((state, level, pos, tint) -> 0x7A7A7A, Registry.Blocks.STEEL_SLAB_BLOCK.get());
        for (Material material : MATERIALS.values()) {
            for (Pair<RegistryObject<Block>, RegistryObject<Item>> value : material.getObjects().values()) {
                if (value.getFirst() != null) {
                    event.register((state, level, pos, tint) -> material.getTint(), value.getFirst().get());
                }
            }
        }
    }

    public void colourItems(RegisterColorHandlersEvent.Item event) {
        event.register((state, tint) -> 0x7A7A7A, Registry.Blocks.STEEL_SLAB_BLOCK.get().asItem());
        for (Material material : MATERIALS.values()) {
            for (Material.Component component : material.getComponents()) {
                if (component.isFluid()) continue;
                RegistryObject<Item> item = material.getComponentPair(component).getSecond();
                if (item != null) {
                    if (component.isBlock()) {
                        event.register((stack, tint) -> material.getTint(), item.get());
                    } else if (component == Material.Component.WIRE) {
                        event.register((stack, tint) -> tint == 1 ? material.getTint() : -1, item.get());
                    } else {
                        event.register((stack, tint) -> tint == 0 ? material.getTint() : -1, item.get());
                    }
                }
            }
        }
    }

}
