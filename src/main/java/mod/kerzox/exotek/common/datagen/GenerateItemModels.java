package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GenerateItemModels extends ItemModelProvider {

    private final ModelFile generated = getExistingFile(mcLoc("item/generated"));

    public GenerateItemModels(PackOutput generator, ExistingFileHelper existingFileHelper) {
        super(generator, Exotek.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        for (Material material : Material.MATERIALS.values()) {
            for (Material.Component item : material.getAllItems()) {
                if (item == Material.Component.WIRE) {
                    ItemModelBuilder builder = getBuilder(material.getName() + item.getSerializedName());
                    builder
                            .parent(generated)
                            .texture("layer0", "item/wire_spool")
                            .texture("layer1", "item/wire_spool_overlay");
                    addTint(builder, material.getTint());
                } else if (item == Material.Component.CRUSHED_ORE) {
                    getBuilder(material.getName() + item.getSerializedName())
                            .parent(generated)
                            .texture("layer0", "item/base_dust")
                            .texture("layer1", "item/crushed_ore_layer");
                } else {
                    getBuilder(material.getName() + item.getSerializedName())
                            .parent(generated)
                            .texture("layer0", "item/base_"+item.toString().toLowerCase());
                }
            }
        }
    }

    private void addTint(ItemModelBuilder builder, int tint) {
            builder.element().from(0, 0, 0).to(16, 16, 16)
                    .allFaces((direction, faceBuilder) -> faceBuilder.texture("#layer1").tintindex(tint)).end();
        }

}
