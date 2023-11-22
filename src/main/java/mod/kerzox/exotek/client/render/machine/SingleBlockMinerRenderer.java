package mod.kerzox.exotek.client.render.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.client.render.ExotekBlockEntityRenderer;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.client.render.types.ExoRenderTypes;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class SingleBlockMinerRenderer extends ExotekBlockEntityRenderer<SingleBlockMinerEntity> {

    public SingleBlockMinerRenderer(BlockEntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    protected void onRender(SingleBlockMinerEntity blockEntity,
                            WrappedPose wrappedPose,
                            MultiBufferSource bufferSource, float partialTicks, int combinedLight, int lightOverlay) {

        if (!blockEntity.showRadius()) return;

        int r = blockEntity.getRadius();

        LevelRenderer.renderLineBox(wrappedPose.asStack(),
                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(ExoRenderTypes.NO_DEPTH_LINES),
                -r, 0, -r, r + 1, 1, r + 1,
                0, 1f, 0, 1.0F);

    }


}