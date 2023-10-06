package mod.kerzox.exotek.client.model.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MinerModel extends GeoModel<MinerEntity> {

    @Override
    public ResourceLocation getModelResource(MinerEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "geo/miner.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MinerEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "textures/block/multiblock/miner.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MinerEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "animations/miner.json");
    }
}
