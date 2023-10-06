package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.client.model.multiblock.MinerModel;
import mod.kerzox.exotek.client.model.multiblock.PumpjackModel;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class MinerRenderer extends GeoBlockRenderer<MinerEntity> {

    public MinerRenderer(BlockEntityRendererProvider.Context pContext) {
        super(new MinerModel());
    }

    @Override
    public void preRender(PoseStack poseStack, MinerEntity animatable, BakedGeoModel model, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.preRender(poseStack, animatable, model, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);

        WrappedPose pose = WrappedPose.of(poseStack);
        pose.push();
        pose.translate(0, 5, -2);
        pose.pop();

    }

    @Override
    public void actuallyRender(PoseStack poseStack, MinerEntity animatable, BakedGeoModel model, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        WrappedPose pose = WrappedPose.of(poseStack);
        Direction facing = getFacing(animatable);
        pose.push();
        if (facing == Direction.SOUTH) pose.translate(-1f, -1, -1);
        else if (facing == Direction.NORTH) pose.translate(1f, -1, 1);
        else if (facing == Direction.WEST) pose.translate(1f, -1, -1);
        else if (facing == Direction.EAST) pose.translate(-1f, -1, 1);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
        pose.pop();
    }

    @Override
    public boolean shouldRenderOffScreen(MinerEntity p_112306_) {
        return true;
    }
}
