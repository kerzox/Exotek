package mod.kerzox.exotek.client.render.multiblock;

import mod.kerzox.exotek.client.model.multiblock.GroundSampleDrillModel;
import mod.kerzox.exotek.client.model.multiblock.PumpjackModel;
import mod.kerzox.exotek.common.blockentities.machine.GroundSampleDrillEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class GroundSampleDrillRenderer extends GeoBlockRenderer<GroundSampleDrillEntity> {

    public GroundSampleDrillRenderer(BlockEntityRendererProvider.Context pContext) {
        super(new GroundSampleDrillModel());
    }

    @Override
    public boolean shouldRenderOffScreen(GroundSampleDrillEntity p_112306_) {
        return true;
    }
}
