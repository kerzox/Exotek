package mod.kerzox.exotek.client.model.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.blockentities.machine.GroundSampleDrillEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class GroundSampleDrillModel extends GeoModel<GroundSampleDrillEntity> {

    @Override
    public ResourceLocation getModelResource(GroundSampleDrillEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "geo/ground_sample_drill.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(GroundSampleDrillEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "textures/block/multiblock/drill_texture.png");
    }

    @Override
    public ResourceLocation getAnimationResource(GroundSampleDrillEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "animations/ground_sample_drill_block.json");
    }
}
