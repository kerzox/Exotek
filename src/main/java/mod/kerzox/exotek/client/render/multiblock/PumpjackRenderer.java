package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.client.model.multiblock.PumpjackModel;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class PumpjackRenderer extends GeoBlockRenderer<PumpjackEntity> {

    public PumpjackRenderer(BlockEntityRendererProvider.Context pContext) {
        super(new PumpjackModel());
    }

    @Override
    public boolean shouldRenderOffScreen(PumpjackEntity p_112306_) {
        return true;
    }
}
