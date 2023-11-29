package mod.kerzox.exotek.registry;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.block.ExotekBlock;
import mod.kerzox.exotek.common.block.ScaffoldBlock;
import mod.kerzox.exotek.common.fluid.ExotekFluidType;
import mod.kerzox.exotek.common.item.ExotekItem;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.RegistryObject;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Material {

    public static Map<String, Material> MATERIALS = new HashMap<>();
    private List<Component> components;
    private String name;
    private int tint;
    private Supplier<Block> blockSupplier;
    private Supplier<Item> itemSupplier;

    private Map<String, Pair<RegistryObject<Block>, RegistryObject<Item>>> objects = new HashMap<>();
    private Map<Component, ExotekRegistry.Fluids.makeFluid<ExotekFluidType>> fluids = new HashMap<>();

    public Material(List<Component> components, String name, int tint, Supplier<Block> blockSupplier, Supplier<Item> itemSupplier) {
        this.components = components;
        this.name = name;
        this.tint = tint;
        this.blockSupplier = blockSupplier == null ? () -> new ExotekBlock(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops().strength(1.5F, 6.0F)) : blockSupplier;
        this.itemSupplier = itemSupplier == null ? () -> new ExotekItem(new Item.Properties()) : itemSupplier;
        register();
    }

    public void register() {
        for (Component component : getAllBlocks()) {
            RegistryObject<Block> ret = null;
            if (component.isOre()) {
                BlockBehaviour.Properties oreProps =
                        BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops()
                                .strength(3.0F, 3.0F);
                ret = ExotekRegistry.BLOCKS.register(name + component.getSerializedName(), () -> new DropExperienceBlock(oreProps));
            } else if (component == Component.SCAFFOLD) {
                ret = ExotekRegistry.BLOCKS.register(name + component.getSerializedName(), () -> new ScaffoldBlock(BlockBehaviour.Properties.of()
                        .mapColor(MapColor.METAL)
                        .noOcclusion()
                        .instrument(NoteBlockInstrument.BASEDRUM)
                        .requiresCorrectToolForDrops().strength(1.5F, 6.0F)));
            } else {
                ret = ExotekRegistry.BLOCKS.register(name + component.getSerializedName(), blockSupplier);
            }
            if (ret == null) continue;
            RegistryObject<Block> finalRet = ret;
            RegistryObject<Item> ret2 = ExotekRegistry.ITEMS.register(name + component.getSerializedName(), () -> new BlockItem(finalRet.get(), new Item.Properties()));
            objects.put(name + component.getSerializedName(), Pair.of(ret, ret2));
        }
        for (Component component : getAllItems()) {
            RegistryObject<Item> ret2 = ExotekRegistry.ITEMS.register(name + component.getSerializedName(), itemSupplier);
            objects.put(name + component.getSerializedName(), Pair.of(null, ret2));
        }
        for (Component fluid : getAllFluids()) {
            fluids.put(fluid, ExotekRegistry.Fluids.makeFluid.build(name + fluid.getSerializedName(), false, false, () -> ExotekFluidType.createColoured(tint, false)));
        }
        MATERIALS.put(name, this);
    }

    public Map<String, Pair<RegistryObject<Block>, RegistryObject<Item>>> getObjects() {
        return objects;
    }

    public Pair<RegistryObject<Block>, RegistryObject<Item>> getComponentPair(Component component) {
        return objects.get(this.name+component.getSerializedName());
    }

    public ExotekRegistry.Fluids.makeFluid<ExotekFluidType> getFluidByComponent(Component component) {
        return fluids.get(component);
    }

    public String getName() {
        return name;
    }

    public int getTint() {
        return tint;
    }

    public List<Component> getComponents() {
        return components;
    }

    public List<Component> getAllItems() {
        return components.stream().filter(c -> !c.isBlock() && !c.isFluid()).collect(Collectors.toList());
    }

    public List<Component> getAllBlocks() {
        return components.stream().filter(Component::isBlock).collect(Collectors.toList());
    }

    public List<Component> getAllFluids() {
        return components.stream().filter(Component::isFluid).collect(Collectors.toList());
    }

    public static class Builder {

        private Supplier<Block> blockSupplier;
        private Supplier<Item> itemSupplier;
        private List<Component> components;
        private String name;
        private int tint;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder colour(int tint) {
            this.tint = tint;
            return this;
        }

        public Builder allComponents() {
            this.components = new ArrayList<>();
            this.components.addAll(Arrays.stream(Component.values()).toList());
            return this;
        }

        public Builder fullMetalComponents() {
            this.components = new ArrayList<>();
            this.components.addAll(Arrays.stream(Component.values()).toList());
            this.components.removeIf(c -> c == Component.FLUID);
            return this;
        }

        public Builder allComponentsNoFluid() {
            this.components = new ArrayList<>();
            this.components.addAll(Arrays.stream(Component.values()).toList());
            this.components.removeIf(Component::isFluid);
            return this;
        }

        public Builder allComponentsWithoutWire() {
            this.components = new ArrayList<>();
            this.components.addAll(Arrays.stream(Component.values()).toList());
            this.components.removeIf(Component::isWire);
            return this;
        }

        public Builder allComponentsWithoutOreBlock() {
            this.components = new ArrayList<>();
            this.components.addAll(Arrays.stream(Component.values()).toList());
            this.components.removeIf(Component::isOre);
            this.components.remove(Component.CRUSHED_ORE);
            return this;
        }

        public Builder allComponentsWithoutBlock() {
            this.components = new ArrayList<>();
            this.components.addAll(Arrays.stream(Component.values()).toList());
            this.components.removeIf(Component::isBlock);
            return this;
        }

        public Builder addComponents(Component... components) {
            this.components = Arrays.asList(components);
            return this;
        }

        public Builder block(Supplier<Block> blockSupplier) {
            this.blockSupplier = blockSupplier;
            return this;
        }

        public Builder item(Supplier<Item> itemSupplier) {
            this.itemSupplier = itemSupplier;
            return this;
        }

        public Material build() {
            return new Material(components, name, tint, blockSupplier, itemSupplier);
        }

    }

    public enum Component implements StringRepresentable {
        BLOCK("block", "blocks"),
        SHEET_BLOCK("sheet_block", "sheet_blocks"),
        ORE("ore", "ores"),
        CRUSHED_ORE("crushed_ore", "crushed_ores"),
        DEEPSLATE_ORE("deepslate_ore", "ores"),
        SCAFFOLD("scaffold", "scaffolds"),
        SHEET("sheet", "sheets"),
        CHUNK("raw", "raw_materials"),
        NUGGET("nugget", "nuggets"),
        INGOT("ingot", "ingots"),
        PLATE("plate", "plates"),
        ROD("rod", "rods"),
        IMPURE_DUST("impure_dust", "impure_dusts"),
        DUST("dust", "dusts"),
        CLUSTER("cluster", "clusters"),
        WIRE("wire", "wires"),
        MICRO_WIRE("micro_wire", "micro_wires"),
        FLUID("fluid", ""),
        METAL_SOLUTION_FLUID("metal_solution", "metal_solution"),
        GEAR("gear", "gears");

        private String name;
        private String tagKeyString;

        Component(String name, String tagKeyString) {
            this.name = name;
            this.tagKeyString = tagKeyString;
        }

        public String getTagKeyString() {
            return tagKeyString;
        }

        @Override
        public String getSerializedName() {
            return "_" + toString().toLowerCase();
        }

        public boolean isOre() {
            return this == ORE || this == DEEPSLATE_ORE;
        }

        public boolean isWire() {
            return this == MICRO_WIRE || this == WIRE;
        }

        public boolean isBlock() {
            return isOre() || this == BLOCK || this == SHEET_BLOCK || this == SCAFFOLD;
        }

        public boolean isFluid() {return this == FLUID || this == METAL_SOLUTION_FLUID;}

        @Override
        public String toString() {
            return this.name;
        }
    }

}
