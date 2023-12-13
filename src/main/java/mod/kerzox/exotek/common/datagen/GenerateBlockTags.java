package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static mod.kerzox.exotek.registry.Material.MATERIALS;

public class GenerateBlockTags extends BlockTagsProvider {

    public GenerateBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, Exotek.MODID, existingFileHelper);
    }

    public void axe(Block block, int level) {
        tag(BlockTags.MINEABLE_WITH_AXE).add(block);
        harvest(level, block);
    }

    public void pickaxe(Block block, int level) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block);
        harvest(level, block);
    }

    public void harvest(int level, Block block) {
        switch (level) {
            case -1 -> {
                tag(Tags.Blocks.NEEDS_WOOD_TOOL).add(block);
            }
            case 0 -> {
                tag(Tags.Blocks.NEEDS_GOLD_TOOL).add(block);
            }
            case 1 -> {
                tag(BlockTags.NEEDS_STONE_TOOL).add(block);
            }
            case 2 -> {
                tag(BlockTags.NEEDS_IRON_TOOL).add(block);
            }
            case 3 -> {
                tag(BlockTags.NEEDS_DIAMOND_TOOL).add(block);
            }
            case 4 -> {
                tag(Tags.Blocks.NEEDS_NETHERITE_TOOL).add(block);
            }
        }
    }


    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {

        for (Material material : MATERIALS.values()) {
            for (Material.Component component : material.getComponents()) {
                if (component.isBlock()) pickaxe(material.getComponentPair(component).getFirst().get(), 1);
            }
        }

        pickaxe(ExotekRegistry.Blocks.STEEL_SCAFFOLD_BLOCK.get(), 1);
        pickaxe(ExotekRegistry.Blocks.STEEL_SLAB_BLOCK.get(), 1);
        pickaxe(ExotekRegistry.Blocks.STEEL_SCAFFOLD_SLAB_BLOCK.get(), 1);
        pickaxe(ExotekRegistry.Blocks.CONVEYOR_BELT_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.CONVEYOR_BELT_RAMP_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.CONVEYOR_BELT_RAMP_TOP_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.BURNABLE_GENERATOR_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.FURNACE_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.MACERATOR_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.CHEMICAL_REACTOR_CHAMBER_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.COMPRESSOR_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.ELECTROLYZER_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.ENGRAVING_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.MANUFACTORY_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.WASHING_PLANT_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.CENTRIFUGE_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.CIRCUIT_ASSEMBLY_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.RUBBER_EXTRACTION_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.SINGLE_BLOCK_MINER_DRILL_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.FIRE_RESISTANT_BRICK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.COKE_OVEN_BRICK.get(), -1);

        pickaxe(ExotekRegistry.Blocks.MACHINE_CASING_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.ENERGY_BANK_CASING_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.ENERGY_CELL_BASIC_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.ENERGY_CELL_ADVANCED_BLOCK.get(), -1);
        pickaxe(ExotekRegistry.Blocks.ENERGY_CELL_HYPER_BLOCK.get(), -1);

        axe(ExotekRegistry.Blocks.WORKSTATION_BLOCK.get(), -1);
        axe(ExotekRegistry.Blocks.WORKSTATION_PART_BLOCK.get(), -1);
        axe(ExotekRegistry.Blocks.TREATED_PLANK.get(), -1);
        axe(ExotekRegistry.Blocks.CREOSOTE_TREATED_SLAB_BLOCK.get(), -1);
    }
}
