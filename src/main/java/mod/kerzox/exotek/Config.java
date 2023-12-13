package mod.kerzox.exotek;

import mod.kerzox.exotek.registry.ConfigConsts;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import java.util.Set;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Forge's config APIs
@Mod.EventBusSubscriber(modid = Exotek.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config
{
    public static final ForgeConfigSpec GENERAL_SPEC;

    static {
        ForgeConfigSpec.Builder configBuilder = new ForgeConfigSpec.Builder();
        setupConfig(configBuilder);
        GENERAL_SPEC = configBuilder.build();
    }

    public static ForgeConfigSpec.IntValue FE_USAGE_FURNACE;
    public static ForgeConfigSpec.IntValue FE_GEN_BURNABLE_GENERATOR;
    private static ForgeConfigSpec.IntValue FE_BATTERY_CAPACITY;
    private static ForgeConfigSpec.IntValue FE_BATTERY_EXTRACT;
    private static ForgeConfigSpec.IntValue FE_BATTERY_INSERT;




    // blast furnace
    private static ForgeConfigSpec.IntValue FE_USAGE_IND_BLAST_FURNACE;
    private static ForgeConfigSpec.IntValue FE_MAX_HEAT_IND_BLAST_FURNACE;
    private static ForgeConfigSpec.IntValue INDUSTRIAL_BLAST_FURNACE_HEAT_GAIN;
    private static ForgeConfigSpec.IntValue INDUSTRIAL_BLAST_FURNACE_HEAT_LOSS;
    private static ForgeConfigSpec.IntValue INDUSTRIAL_SPEED_MODIFIER;

    private static void setupConfig(ForgeConfigSpec.Builder builder) {
        builder.comment("Machine Options");
        builder.push("Burnable Furnace Options");
        FE_GEN_BURNABLE_GENERATOR = builder
                .comment("Burnable generators energy gen each tick")
                .defineInRange("fe/tick", 10, 0, Integer.MAX_VALUE);
        builder.pop();


        builder.push("Furnace Options");
        FE_USAGE_FURNACE = builder
                        .comment("Powered Furnace energy usage each tick")
                        .defineInRange("fe/tick", 5, 0, Integer.MAX_VALUE);

        builder.pop();

        builder.push("Industrial Blast Furnace Options");
        FE_USAGE_IND_BLAST_FURNACE = builder
                .comment("Industrial Blast Furnace energy usage each tick")
                .defineInRange("fe/tick", 1500, 0, Integer.MAX_VALUE);
        FE_MAX_HEAT_IND_BLAST_FURNACE = builder
                .comment("Industrial Blast Furnace maximum heat")
                .defineInRange("heat/tick", 1500, 500, Integer.MAX_VALUE);
        INDUSTRIAL_BLAST_FURNACE_HEAT_GAIN = builder
                .comment("Industrial Blast Furnace maximum heat")
                .defineInRange("seconds for each heat gained", 1, 1, Integer.MAX_VALUE);
        INDUSTRIAL_BLAST_FURNACE_HEAT_LOSS = builder
                .comment("Industrial Blast Furnace maximum heat")
                .defineInRange("seconds for each heat loss", 4, 1, Integer.MAX_VALUE);
        INDUSTRIAL_SPEED_MODIFIER = builder
                .comment("Industrial Blast Furnace modifier speed")
                .defineInRange("modifier", 2, 0, Integer.MAX_VALUE);
        builder.pop();

        builder.push("Battery Options");
        FE_BATTERY_CAPACITY = builder
                .comment("Battery capacity (effects the cells of the multiblock energy bank)")
                .defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
        FE_BATTERY_INSERT = builder
                .defineInRange("insert limit", 500, 1, Integer.MAX_VALUE);
        FE_BATTERY_EXTRACT = builder
                .defineInRange("extract limit", 500, 1, Integer.MAX_VALUE);
        builder.pop();

//        builder.push("Tier Options");
//        CAP = builder
//                .comment("Battery capacity (effects the cells of the multiblock energy bank)")
//                .defineInRange("capacity", 100000, 1, Integer.MAX_VALUE);
//        FE_BATTERY_INSERT = builder
//                .defineInRange("insert limit", 500, 1, Integer.MAX_VALUE);
//        FE_BATTERY_EXTRACT = builder
//                .defineInRange("extract limit", 500, 1, Integer.MAX_VALUE);
//        builder.pop();

    }

    public static Set<Item> items;

    private static boolean validateItemName(final Object obj)
    {
        return true;
    }

    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        ConfigConsts.FURNACE_FE_USAGE_PER_TICK = FE_USAGE_FURNACE.get();
        ConfigConsts.BURNABLE_GENERATOR_FE_GEN_PER_TICK = FE_GEN_BURNABLE_GENERATOR.get();
        ConfigConsts.BATTERY_CAPACITY = FE_BATTERY_CAPACITY.get();
        ConfigConsts.BATTERY_INSERT_LIMIT = FE_BATTERY_INSERT.get();
        ConfigConsts.BATTERY_EXTRACT_LIMIT = FE_BATTERY_EXTRACT.get();
        ConfigConsts.INDUSTRIAL_BLAST_FURNACE_FE_USAGE_PER_TICK = FE_USAGE_IND_BLAST_FURNACE.get();
        ConfigConsts.INDUSTRIAL_BLAST_FURNACE_MAX_HEAT = FE_MAX_HEAT_IND_BLAST_FURNACE.get();
        ConfigConsts.INDUSTRIAL_BLAST_FURNACE_SPEED_MODIFIER = INDUSTRIAL_SPEED_MODIFIER.get();
        ConfigConsts.INDUSTRIAL_BLAST_FURNACE_HEAT_GAIN = INDUSTRIAL_BLAST_FURNACE_HEAT_GAIN.get();
        ConfigConsts.INDUSTRIAL_BLAST_FURNACE_HEAT_LOSS = INDUSTRIAL_BLAST_FURNACE_HEAT_LOSS.get();
    }
}
