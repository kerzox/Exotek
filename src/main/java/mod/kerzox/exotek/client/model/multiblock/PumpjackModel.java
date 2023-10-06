package mod.kerzox.exotek.client.model.multiblock;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.model.GeoModel;

public class PumpjackModel extends GeoModel<PumpjackEntity> {



    @Override
    public ResourceLocation getModelResource(PumpjackEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "geo/pumpjack.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PumpjackEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "textures/block/pump_jack.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PumpjackEntity animatable) {
        return new ResourceLocation(Exotek.MODID, "animations/pump_jack.json");
    }
}
