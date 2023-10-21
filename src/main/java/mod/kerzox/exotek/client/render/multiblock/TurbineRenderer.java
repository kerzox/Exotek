package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.TurbineEntity;
import mod.kerzox.exotek.registry.Material;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.client.model.data.ModelData;

import java.util.List;


public class TurbineRenderer implements BlockEntityRenderer<TurbineEntity> {

    public static final ResourceLocation MODEL = new ResourceLocation(Exotek.MODID, "multiblock/turbine");
    public static final ResourceLocation SHAFT = new ResourceLocation(Exotek.MODID, "multiblock/turbine_shaft");

    public TurbineRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(TurbineEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(MODEL);
        BakedModel shaft = Minecraft.getInstance().getBlockRenderer().getBlockModel(Registry.STEEL.getComponentPair(Material.Component.BLOCK).getFirst().get().defaultBlockState());
        WrappedPose pose = WrappedPose.of(poseStack);
        pose.push();
        Direction facing = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        pose.translate(0, -1, 0);

        float[] colours = RenderingUtil.convertColor(0x7A7A7A);

        if (facing == Direction.SOUTH) {
            pose.push();
            pose.rotateY(180);
//            pose.translate(1, 1, 7);
//            List<BakedQuad> quads = RenderingUtil.getQuads(shaft, entity, null, RenderType.cutout());
//            RenderingUtil.renderQuads(pose.last(), bufferSource.getBuffer(RenderType.cutout()),
//                    colours[0], colours[1], colours[2], 1f,
//                    quads, combinedLight, combinedOverlay);
//
//            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, shaft, entity.getBlockState(),
//                    entity.getBlockPos(),
//                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
//                    RenderType.cutout());
//            pose.translate(-1, -1, -7);
            pose.translate(0.5f, 0, 0.5f);
            pose.translate(1, 0, 0);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        else if (facing == Direction.NORTH) {
            pose.push();
//            pose.translate(1, 1, 7);
//            List<BakedQuad> quads = RenderingUtil.getQuads(shaft, entity, null, RenderType.cutout());
//            RenderingUtil.renderQuads(pose.last(), bufferSource.getBuffer(RenderType.cutout()),
//                    colours[0], colours[1], colours[2], 1f,
//                    quads, combinedLight, combinedOverlay);
//            pose.translate(-1, -1, -7);
            pose.translate(0.5f, 0, 0.5f);
            pose.translate(1, 0, 0);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        else if (facing == Direction.WEST) {
            pose.push();
            pose.rotateY(90);
//            pose.translate(1, 1, 7);
//            List<BakedQuad> quads = RenderingUtil.getQuads(shaft, entity, null, RenderType.cutout());
//            RenderingUtil.renderQuads(pose.last(), bufferSource.getBuffer(RenderType.cutout()),
//                    colours[0], colours[1], colours[2], 1f,
//                    quads, combinedLight, combinedOverlay);
//            pose.translate(-1, -1, -7);
            pose.translate(0.5f, 0, 0.5f);
            pose.translate(1, 0, 0);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        else if (facing == Direction.EAST) {
            pose.push();
            pose.rotateY(270);
//            pose.translate(1, 1, 7);
//            List<BakedQuad> quads = RenderingUtil.getQuads(shaft, entity, null, RenderType.cutout());
//            RenderingUtil.renderQuads(pose.last(), bufferSource.getBuffer(RenderType.cutout()),
//                    colours[0], colours[1], colours[2], 1f,
//                    quads, combinedLight, combinedOverlay);
//            pose.translate(-1, -1, -7);
            pose.translate(0.5f, 0, 0.5f);
            pose.translate(1, 0, 0);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            pose.pop();
        }
        pose.pop();
    }
}
