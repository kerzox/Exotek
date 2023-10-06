package mod.kerzox.exotek;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.jgrapht.alg.util.Pair;

import java.util.List;
import java.util.Map;
import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Exotek.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    private static final ForgeConfigSpec.IntValue FE_USAGE_FURNACE = BUILDER
            .comment("Powered Furnace energy usage each tick")
            .defineInRange("fe/tick", 5, 0, Integer.MAX_VALUE);

    private static final ForgeConfigSpec.IntValue FE_GEN_BURNABLE_GENERATOR = BUILDER
            .comment("Burnable generators energy gen each tick")
            .defineInRange("fe/tick", 10, 0, Integer.MAX_VALUE);

//    // a list of strings that are treated as resource locations for items
//    private static final ForgeConfigSpec.ConfigValue<List<?>> ORE_DEPOSITS = BUILDER
//            .comment("A collection of ore deposits that can spawn in world")
//            .defineListAllowEmpty("veins", List.of(Map.of("Magnetite", List.of("minecraft:iron_ingot", "minecraft:gold_ingot"))), Config::validateItemName);

    static final ForgeConfigSpec SPEC = BUILDER.build();

    public static Set<Item> items;

    public static int FURNACE_FE_USAGE_PER_TICK;
    public static int BURNABLE_GENERATOR_FE_GEN_PER_TICK;

    private static boolean validateItemName(final Object obj)
    {
        return true;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        FURNACE_FE_USAGE_PER_TICK = FE_USAGE_FURNACE.get();
        BURNABLE_GENERATOR_FE_GEN_PER_TICK = FE_GEN_BURNABLE_GENERATOR.get();
    }
}
