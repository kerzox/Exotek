package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.OilDistillationTowerEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;

public class DistillationTowerRenderer implements BlockEntityRenderer<OilDistillationTowerEntity> {

    public static final ResourceLocation DISTILLATION_TOWER_MODEL = new ResourceLocation(Exotek.MODID, "multiblock/distillation_tower");

    public DistillationTowerRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public boolean shouldRender(OilDistillationTowerEntity p_173568_, Vec3 p_173569_) {
        return BlockEntityRenderer.super.shouldRender(p_173568_, p_173569_);
    }


    @Override
    public void render(OilDistillationTowerEntity entity, float p_112308_, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(DISTILLATION_TOWER_MODEL);
        WrappedPose pose = WrappedPose.of(poseStack);
        pose.push();
        pose.translate(0, -1, 0);

        Direction facing = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        if (facing == Direction.SOUTH) {
            pose.push();
            pose.rotateY(180);
            pose.translate(0.5f, 0, 0.5f);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        else if (facing == Direction.NORTH) {
            pose.push();
            pose.translate(0.5f, 0, 0.5f);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        else if (facing == Direction.WEST) {
            pose.push();
            pose.rotateY(90);
            pose.translate(0.5f, 0, 0.5f);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        else if (facing == Direction.EAST) {
            pose.push();
            pose.rotateY(270);
            pose.translate(0.5f, 0, 0.5f);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        pose.pop();
    }
}
