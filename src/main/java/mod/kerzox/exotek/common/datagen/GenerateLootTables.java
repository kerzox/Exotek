package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.block.machine.MachineEntityBlock;
import mod.kerzox.exotek.common.block.multiblock.MultiblockBlock;
import mod.kerzox.exotek.common.block.transport.IConveyorBeltBlock;
import mod.kerzox.exotek.common.blockentities.CapabilityBlockEntity;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.registry.ExotekRegistry;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.data.loot.packs.VanillaBlockLoot;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.DynamicLoot;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.nbt.ContextNbtProvider;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Map;
import java.util.stream.Collectors;

public class GenerateLootTables extends VanillaBlockLoot {

    @Override
    protected void generate() {

        Material.MATERIALS.values().forEach(m -> {
            m.getAllBlocks().forEach(b -> dropSelf(m.getComponentPair(b).getFirst().get()));
        });

        for (RegistryObject<Block> block : ExotekRegistry.Blocks.getRegisteredBlocks()) {
            if (block.get() instanceof IConveyorBeltBlock beltBlock) {
                dropOther(block.get(), ExotekRegistry.Items.CONVEYOR_BELT_ITEM.get());
            }
            if (!(block.get() instanceof MachineEntityBlock<?>)) {
                dropSelf(block.get());
            }
            if (block.get() instanceof MultiblockBlock<?>) add(block.get(), noDrop());
        }

        createStandardTableWithOtherDrop(
                ExotekRegistry.Blocks.STORAGE_CRATE_BLOCK.get(),
                ExotekRegistry.Items.STORAGE_CRATE_ITEM.get(),
                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );

        createStandardTableWithOtherDrop(
                ExotekRegistry.Blocks.STORAGE_CRATE_BASIC_BLOCK.get(),
                ExotekRegistry.Items.STORAGE_CRATE_BASIC_ITEM.get(),
                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );

        createStandardTableWithOtherDrop(
                ExotekRegistry.Blocks.STORAGE_CRATE_ADVANCED_BLOCK.get(),
                ExotekRegistry.Items.STORAGE_CRATE_ADVANCED_ITEM.get(),
                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );

        createStandardTableWithOtherDrop(
                ExotekRegistry.Blocks.STORAGE_CRATE_HYPER_BLOCK.get(),
                ExotekRegistry.Items.STORAGE_CRATE_HYPER_ITEM.get(),
                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );

//        createStandardTable(
//                ExotekRegistry.Blocks.STORAGE_CRATE_BLOCK.get(),
//                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
//                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
//        );
//
//        createStandardTable(
//                ExotekRegistry.Blocks.STORAGE_CRATE_ADVANCED_BLOCK.get(),
//                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
//                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
//        );
//
//        createStandardTable(
//                ExotekRegistry.Blocks.STORAGE_CRATE_HYPER_BLOCK.get(),
//                ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(),
//                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
//        );

        createStandardTable(
                ExotekRegistry.Blocks.BURNABLE_GENERATOR_BLOCK.get(),
                ExotekRegistry.BlockEntities.BURNABLE_GENERATOR_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.CENTRIFUGE_BLOCK.get(),
                ExotekRegistry.BlockEntities.CENTRIFUGE_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.CHEMICAL_REACTOR_CHAMBER_BLOCK.get(),
                ExotekRegistry.BlockEntities.CHEMICAL_REACTOR_CHAMBER_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.CIRCUIT_ASSEMBLY_BLOCK.get(),
                ExotekRegistry.BlockEntities.CIRCUIT_ASSEMBLY_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.COMPRESSOR_BLOCK.get(),
                ExotekRegistry.BlockEntities.COMPRESSOR_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.ELECTROLYZER_BLOCK.get(),
                ExotekRegistry.BlockEntities.ELECTROLYZER_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.ENGRAVING_BLOCK.get(),
                ExotekRegistry.BlockEntities.ENGRAVING_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.FURNACE_BLOCK.get(),
                ExotekRegistry.BlockEntities.FURNACE_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG,
                ITieredMachine.TIERED_MACHINE_TAG

        );
        createStandardTable(
                ExotekRegistry.Blocks.GROUND_SAMPLE_DRILL_BLOCK.get(),
                ExotekRegistry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.MACERATOR_BLOCK.get(),
                ExotekRegistry.BlockEntities.MACERATOR_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG,
                ITieredMachine.TIERED_MACHINE_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.MANUFACTORY_BLOCK.get(),
                ExotekRegistry.BlockEntities.MANUFACTORY_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );

        createStandardTable(
                ExotekRegistry.Blocks.RUBBER_EXTRACTION_BLOCK.get(),
                ExotekRegistry.BlockEntities.RUBBER_EXTRACTION_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.SINGLE_BLOCK_MINER_DRILL_BLOCK.get(),
                ExotekRegistry.BlockEntities.MINER_DRILL_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.WASHING_PLANT_BLOCK.get(),
                ExotekRegistry.BlockEntities.WASHING_PLANT_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );
        createStandardTable(
                ExotekRegistry.Blocks.IRON_FLUID_TANK_BLOCK.get(),
                ExotekRegistry.BlockEntities.IRON_FLUID_TANK_ENTITY.get(),
                CapabilityBlockEntity.MACHINE_CAPABILITY_LIST_TAG
        );

   }

//    @Override
//    protected Iterable<Block> getKnownBlocks() {
//        return List.of(ExotekRegistry.Blocks.FURNACE_BLOCK.get());
//    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ForgeRegistries.BLOCKS.getEntries().stream()
                .filter(e -> e.getKey().location().getNamespace().equals(Exotek.MODID))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }

    private void createStandardTable(Block block, BlockEntityType<?> type, String... tags) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(block);
        lti.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
        for (String tag : tags) {
            lti.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(tag, "BlockEntityTag." + tag, CopyNbtFunction.MergeStrategy.REPLACE));
        }
        lti.apply(SetContainerContents.setContents(type).withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))));

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);
        add(block, LootTable.lootTable().withPool(builder));
    }

    private void createStandardTableWithOtherDrop(Block block, Item drop, BlockEntityType<?> type, String... tags) {
        LootPoolSingletonContainer.Builder<?> lti = LootItem.lootTableItem(drop);
        lti.apply(CopyNameFunction.copyName(CopyNameFunction.NameSource.BLOCK_ENTITY));
        for (String tag : tags) {
            lti.apply(CopyNbtFunction.copyData(ContextNbtProvider.BLOCK_ENTITY).copy(tag, "BlockEntityTag." + tag, CopyNbtFunction.MergeStrategy.REPLACE));
        }
        lti.apply(SetContainerContents.setContents(type).withEntry(DynamicLoot.dynamicEntry(new ResourceLocation("minecraft", "contents"))));

        LootPool.Builder builder = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .add(lti);
        add(block, LootTable.lootTable().withPool(builder));
    }

}


