package mod.kerzox.exotek;

import com.mojang.datafixers.util.Pair;
import com.mojang.logging.LogUtils;
import mod.kerzox.exotek.client.render.types.ExoRenderTypes;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.event.CommonEvents;
import mod.kerzox.exotek.common.event.TickUtils;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.ConfigConsts;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
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

    public Exotek()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ConfigConsts.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.GENERAL_SPEC);

        // Register the commonSetup method for modloading
        modEventBus.addListener(CommonSetup::init);

        // registry
        ExotekRegistry.init(modEventBus);

        // network
        PacketHandler.register();

        // event bus
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new TickUtils());
        MinecraftForge.EVENT_BUS.register(new CommonEvents());

        // client
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> modEventBus.addListener(ClientSetup::init));

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

    }

    @SubscribeEvent
    public static void onRegisterNamedRenderTypes(RegisterNamedRenderTypesEvent event)
    {
        event.register("no_texture_quad", ExoRenderTypes.SOLID_COLOUR, ExoRenderTypes.SOLID_COLOUR);
        event.register("no_depth_lines", ExoRenderTypes.NO_DEPTH_LINES, ExoRenderTypes.NO_DEPTH_LINES);
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
        if (event.getTabKey() == ExotekRegistry.EXOTEK_TAB.getKey()) {
            for (Material material : MATERIALS.values()) {
                for (Pair<RegistryObject<Block>, RegistryObject<Item>> value : material.getObjects().values()) {
                    if (value.getSecond() != null) {
                        event.accept(value.getSecond());
                    }
                }
            }
            for (RegistryObject<Item> item : ExotekRegistry.ALL_ITEMS) {
                event.accept(item);
            }
        }
    }


}
