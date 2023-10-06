package mod.kerzox.exotek.common.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void doDataGeneration(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeServer(), new GenerateRecipes(gen.getPackOutput()));
        gen.addProvider(event.includeClient(), new GenerateBlockModels(gen.getPackOutput(), existingFileHelper));
        gen.addProvider(event.includeClient(), new GenerateItemModels(gen.getPackOutput(), existingFileHelper));
    }


}
