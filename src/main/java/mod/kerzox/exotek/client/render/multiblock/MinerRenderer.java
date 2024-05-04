package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.client.model.multiblock.MinerModel;
import mod.kerzox.exotek.client.model.multiblock.PumpjackModel;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.client.render.types.ExoRenderTypes;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.MinerManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
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

        MinerManager manager = animatable.getMultiblockManager();

        if (!manager.showRadius()) return;

        int r = manager.getRadius();

        pose.push();

//        Direction facing = animatable.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        pose.translate(0, -1, 0);

        if (facing == Direction.NORTH) {
            pose.translate(1, 0, 1);
        }

        if (facing == Direction.SOUTH) {
            pose.translate(-1, 0, -1);
        }

        if (facing == Direction.EAST) {
            pose.translate(-1, 0, 1);
        }

        if (facing == Direction.WEST) {
            pose.translate(1, 0, -1);
        }

        LevelRenderer.renderLineBox(pose.asStack(),
                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(ExoRenderTypes.NO_DEPTH_LINES),
                -r, 0, -r, r + 1, 1, r + 1,
                0, 1f, 0, 1.0F);

        pose.pop();

    }

    @Override
    public boolean shouldRenderOffScreen(MinerEntity p_112306_) {
        return true;
    }
}
