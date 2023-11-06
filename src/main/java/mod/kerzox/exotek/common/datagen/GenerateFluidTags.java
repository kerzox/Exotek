package mod.kerzox.exotek.common.datagen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.Material;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class GenerateFluidTags extends FluidTagsProvider {


    public GenerateFluidTags(PackOutput p_255941_, CompletableFuture<HolderLookup.Provider> p_256600_, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_255941_, p_256600_, Exotek.MODID, existingFileHelper);
    }

    public void forgeTag(String tagName, Fluid... items) {
        this.tag(TagKey.create(ForgeRegistries.FLUIDS.getRegistryKey(), new ResourceLocation("forge", tagName))).add(items);
    }

    @Override
    protected void addTags(HolderLookup.Provider p_256380_) {
        forgeTag("sulphuric_acid", Registry.Fluids.SULPHURIC_ACID.getFluid().get());
        forgeTag("hydrochloric_acid", Registry.Fluids.HYDROCHLORIC_ACID.getFluid().get());
        forgeTag("glue", Registry.Fluids.GLUE.getFluid().get());
        forgeTag("soldering_fluid", Registry.Fluids.SOLDERING_FLUID.getFluid().get());
        forgeTag("diesel", Registry.Fluids.DIESEL.getFluid().get());
        forgeTag("petrol", Registry.Fluids.PETROL.getFluid().get());
        forgeTag("petroleum", Registry.Fluids.PETROLEUM.getFluid().get());
        forgeTag("brine", Registry.Fluids.BRINE.getFluid().get());
        forgeTag("steam", Registry.Fluids.STEAM.getFluid().get());
        forgeTag("lubricant", Registry.Fluids.LUBRICANT.getFluid().get());
        forgeTag("redstone_acid", Registry.Fluids.REDSTONE_ACID.getFluid().get());
        for (Material material : Material.MATERIALS.values()) {
            for (Material.Component fluid : material.getAllFluids()) {
                forgeTag(fluid.getTagKeyString()+"/"+material.getName(), material.getFluidByComponent(fluid).getFluid().get());
            }
        }
    }
}
