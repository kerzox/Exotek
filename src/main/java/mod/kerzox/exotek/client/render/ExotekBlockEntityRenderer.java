package mod.kerzox.exotek.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public abstract class ExotekBlockEntityRenderer<T extends BasicBlockEntity> implements BlockEntityRenderer<T> {

    public ExotekBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    protected abstract void onRender(T blockEntity, WrappedPose wrappedPose, MultiBufferSource bufferSource, float partialTicks, int combinedLight, int lightOverlay);

    @Override
    public void render(T p_112307_, float p_112308_, PoseStack p_112309_, MultiBufferSource p_112310_, int p_112311_, int p_112312_) {
        WrappedPose pose = WrappedPose.of(p_112309_);
        pose.push();
        onRender(p_112307_, pose, p_112310_, p_112308_, p_112311_, p_112312_);
        pose.pop();
    }
}
