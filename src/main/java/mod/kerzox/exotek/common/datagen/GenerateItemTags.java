package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static mod.kerzox.exotek.registry.Material.MATERIALS;

public class GenerateItemTags extends ItemTagsProvider {


    public GenerateItemTags(PackOutput p_275343_,
                            CompletableFuture<HolderLookup.Provider> p_275729_,
                            CompletableFuture<TagLookup<Block>> p_275322_,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(p_275343_, p_275729_, p_275322_, Exotek.MODID, existingFileHelper);
    }

    public void forgeTag(String tagName, Item... items) {
        this.tag(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", tagName))).add(items);
    }


    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {

        forgeTag("coal_coke", ExotekRegistry.Items.COAL_COKE.get());
        forgeTag("treated_wood", ExotekRegistry.Blocks.TREATED_PLANK.get().asItem());
        forgeTag("treated_wood_slab", ExotekRegistry.Blocks.CREOSOTE_TREATED_SLAB_BLOCK.get().asItem());

        for (Material material : MATERIALS.values()) {
            String name = material.getName();
            if (material.getName().contains("iron")) {
                name = "iron";
            }
            for (Material.Component component : material.getComponents()) {
                if (!component.isFluid()) {
                    forgeTag(component.getTagKeyString()+"/"+ name, material.getComponentPair(component).getSecond().get());
                }
//
//                if (component == Material.Component.CHUNK) forgeTag("raw_materials/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.DUST) forgeTag("dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.IMPURE_DUST) forgeTag("impure_dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.IMPURE_DUST) forgeTag("impure_dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.IMPURE_DUST) forgeTag("impure_dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.IMPURE_DUST) forgeTag("impure_dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.IMPURE_DUST) forgeTag("impure_dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//                if (component == Material.Component.IMPURE_DUST) forgeTag("impure_dusts/" + material.getName(), material.getComponentPair(component).getSecond().get());
//
                if (component.isOre()) {
                    forgeTag("ores", material.getComponentPair(component).getSecond().get());
                    forgeTag("ore_rates/singular", material.getComponentPair(component).getSecond().get());
                    if (component == Material.Component.ORE) forgeTag("ores_in_ground/stone", material.getComponentPair(component).getSecond().get());
                    if (component == Material.Component.DEEPSLATE_ORE) forgeTag("ores_in_ground/deepslate", material.getComponentPair(component).getSecond().get());
                }
            }
        }
     //   tag(Tags.Items.RAW_MATERIALS).addTags(TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), new ResourceLocation("forge", "tin")));
    }
}
