package mod.kerzox.exotek.common.datagen;

import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.Material;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.IGeneratedBlockState;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

public class GenerateBlockModels extends BlockStateProvider {

    public GenerateBlockModels(PackOutput gen, ExistingFileHelper exFileHelper) {
        super(gen, Exotek.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        for (RegistryObject<Block> block : Registry.Blocks.getRegisteredBlocks()) {
//            String name = block.getId().getPath();
//            SimpleModelBuilder model = new SimpleModelBuilder(name);
//            ResourceLocation baseName = new ResourceLocation(Exotek.MODID, "block/"+name);
//            BlockModelBuilder builder = models().getBuilder(Exotek.MODID+":block/"+name)
//                    .parent(models().getExistingFile(new ResourceLocation("block")))
//                    .texture("particle", baseName);
//            builder.texture("all", baseName);
//            registeredBlocks.put(block.get(), model);
//            itemModels().withExistingParent(name, modLoc("block/"+name));
        }
        for (Material material : Material.MATERIALS.values()) {
            for (Material.Component block : material.getAllBlocks()) {

                String blockName = material.getName() + block.getSerializedName();

                SimpleModelBuilder model = new SimpleModelBuilder(blockName);
                ResourceLocation baseName = !block.isOre()
                        ? new ResourceLocation(Exotek.MODID, "block/base"+block.getSerializedName()) :
                        block == Material.Component.ORE ? new ResourceLocation("block/stone") : new ResourceLocation("block/deepslate");

                BlockModelBuilder builder = models().getBuilder(Exotek.MODID+":block/"+blockName)
                        .parent(models().getExistingFile(new ResourceLocation("block")))
                        .texture("particle", baseName);
                if (block.isOre()) {
                    builder.renderType("minecraft:cutout");
                    builder.texture("layer1", baseName);
                    builder.texture("layer2", new ResourceLocation(Exotek.MODID, "block/ore_overlay1"));
                } else if (block == Material.Component.SCAFFOLD) {
                    builder.renderType("minecraft:cutout");
                    builder.texture("all", baseName);
                } else {
                    builder.texture("all", baseName);
                }

                addTint(builder, material.getTint(), block);
                registeredBlocks.put((Block) material.getComponentPair(block).getFirst().get(), model);
                itemModels().withExistingParent(blockName, modLoc("block/"+blockName));

            }
        }
    }


    private void addTint(BlockModelBuilder builder, int tint, Material.Component component) {
        if (component.isOre()) {
            builder.element().from(0, 0, 0).to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 0, 16, 16).texture("#layer1").end());
            builder.element().from(0, 0, 0).to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.uvs(0, 0, 16, 16).texture("#layer2").tintindex(tint).end());
        } else {
            builder.element().from(0, 0, 0).to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#all").tintindex(tint)).end();
        }
    }

    public static class SimpleModelBuilder implements IGeneratedBlockState {

        private String modelName;

        public SimpleModelBuilder(String model) {
            this.modelName = model;
        }

        @Override
        public JsonObject toJson() {
            JsonObject root = new JsonObject();
            JsonObject model = new JsonObject();
            model.addProperty("model", Exotek.MODID + ":block/" + modelName);
            JsonObject variants = new JsonObject();
            variants.add("", model);
            root.add("variants", variants);
            return root;
        }
    }

}
