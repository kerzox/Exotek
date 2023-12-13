package mod.kerzox.exotek.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Collections;
import java.util.List;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void doDataGeneration(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        BlockTagsProvider blockTagsProvider = new GenerateBlockTags(gen.getPackOutput(), event.getLookupProvider(), existingFileHelper);
        gen.addProvider(event.includeClient(), new GenerateBlockModels(gen.getPackOutput(), existingFileHelper));
        gen.addProvider(event.includeClient(), new GenerateItemModels(gen.getPackOutput(), existingFileHelper));
        gen.addProvider(event.includeServer(), blockTagsProvider);
        gen.addProvider(event.includeClient(), new GenerateLanguage(gen.getPackOutput(), "en_us"));
        gen.addProvider(event.includeServer(), new GenerateFluidTags(gen.getPackOutput(), event.getLookupProvider(), existingFileHelper));
        gen.addProvider(event.includeServer(), new GenerateItemTags(gen.getPackOutput(), event.getLookupProvider(), blockTagsProvider.contentsGetter(), existingFileHelper));
        gen.addProvider(event.includeServer(), new GenerateRecipes(gen.getPackOutput()));
        gen.addProvider(event.includeServer(), new LootTableProvider(gen.getPackOutput(), Collections.emptySet(),
                List.of(new LootTableProvider.SubProviderEntry(GenerateLootTables::new, LootContextParamSets.BLOCK))));
    }


}
