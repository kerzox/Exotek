package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.FluidTankMultiblockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.model.data.ModelData;


public class FluidTankMultiblockRenderer implements BlockEntityRenderer<FluidTankMultiblockEntity> {

    public static final ResourceLocation FLUID_TANK_MODEL = new ResourceLocation(Exotek.MODID, "multiblock/fluid_tank_multiblock");

    public FluidTankMultiblockRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(FluidTankMultiblockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(FLUID_TANK_MODEL);
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
