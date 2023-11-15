package mod.kerzox.exotek.client.render.transfer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.event.TickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import static mod.kerzox.exotek.client.render.RenderingUtil.addVertex;

public class ConveyorBeltRenderer implements BlockEntityRenderer<ConveyorBeltEntity> {

    // public static final ResourceLocation CORE = new ResourceLocation(Exotek.MODID, "block/energy_cable");
    public static final ResourceLocation BELT_MODEL = new ResourceLocation(Exotek.MODID, "block/conveyor_belt_texture");

    public ConveyorBeltRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    public static float getPartialTicks() {
        Minecraft mc = Minecraft.getInstance();
        return mc.getFrameTime();
    }

    @Override
    public void render(ConveyorBeltEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        WrappedPose pose = WrappedPose.of(poseStack);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(BELT_MODEL);
        Direction facing = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        TextureAtlasSprite sprite = model.getParticleIcon();

        float speedMult = entity.getSpeed();
        float scrollV = TickEvent.clientRenderTick * speedMult % (8);

        pose.push();
        if (facing == Direction.SOUTH) {
            pose.rotateY(180);
            drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1, 0, 16, scrollV, scrollV + 8);
        }
        if (facing == Direction.NORTH) {
            drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1, 0, 16, scrollV, scrollV + 8);
        }
        if (facing == Direction.EAST) {
            pose.rotateY(270);
            drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1, 0, 16, scrollV, scrollV + 8);
        }
        if (facing == Direction.WEST) {
            pose.rotateY(90);
            drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1, 0, 16, scrollV, scrollV + 8);
        }
        pose.pop();
        entity.getBelt().getActiveCovers().forEach(c -> {
            if (c.getRenderer() != null) {
                pose.push();
                c.getRenderer().preRender(entity, c, partialTicks, pose, bufferSource, combinedLight, combinedOverlay);
                pose.pop();
            }
        });
    }

    public static void drawBelt(WrappedPose poseStack, TextureAtlasSprite sprite, VertexConsumer vertex, float length, float minU, float maxU, float minV, float maxV) {
        poseStack.translate(0, -0.5f / 16f, 0);
        poseStack.push();
        addVertex(vertex, poseStack.asStack(), 0, 8 / 16f, length, sprite.getU(minU), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 1, 8 / 16f, length, sprite.getU(maxU), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 1, 8 / 16f, 0, sprite.getU(maxU), sprite.getV(minV));
        addVertex(vertex, poseStack.asStack(), 0, 8 / 16f, 0, sprite.getU(minU), sprite.getV(minV));
        addVertex(vertex, poseStack.asStack(), 0, 7 / 16f, length, sprite.getU(minU), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 1, 7 / 16f, length, sprite.getU(maxU), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 1, 7 / 16f, 0, sprite.getU(maxU), sprite.getV(minV));
        addVertex(vertex, poseStack.asStack(), 0, 7 / 16f, 0, sprite.getU(minU), sprite.getV(minV));
        poseStack.pop();

        poseStack.push();
        poseStack.rotateX(90F);
        poseStack.rotateY(180F);
        poseStack.translate(0, 11 / 16f, 3 / 16f);
        addVertex(vertex, poseStack.asStack(), 2 / 16f, 5 / 16f, 5 / 16f, sprite.getU(minU + 2), sprite.getV(minV));
        addVertex(vertex, poseStack.asStack(), 14 / 16f, 5 / 16f, 5 / 16f, sprite.getU(maxU - 2), sprite.getV(minV));
        addVertex(vertex, poseStack.asStack(), 14 / 16f, 5 / 16f, 0, sprite.getU(maxU - 2), sprite.getV(maxV - 5));
        addVertex(vertex, poseStack.asStack(), 2 / 16f, 5 / 16f, 0, sprite.getU(minU + 2), sprite.getV(maxV - 5));
        poseStack.pop();

        poseStack.push();
        poseStack.rotateY(180F);
        poseStack.rotateX(90F);
        poseStack.translate(0, 11 / 16f, .5f);
        addVertex(vertex, poseStack.asStack(), 2 / 16f, 5 / 16f, 5 / 16f, sprite.getU(minU + 2), sprite.getV(minV + 5));
        addVertex(vertex, poseStack.asStack(), 14 / 16f, 5 / 16f, 5 / 16f, sprite.getU(maxU - 2), sprite.getV(minV + 5));
        addVertex(vertex, poseStack.asStack(), 14 / 16f, 5 / 16f, 0, sprite.getU(maxU - 2), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 2 / 16f, 5 / 16f, 0, sprite.getU(minU + 2), sprite.getV(maxV));
        poseStack.pop();
//        maxU = maxU;
//        minV = minV + 1;
//        maxV = maxV - 10;
//        poseStack.push();
//        poseStack.rotateX(90F);
//        poseStack.translate(0, 8/16f, 8/16f);
//
//        addVertex(vertex, poseStack.asStack(), 2/16f, 8/16f, 4/16f, sprite.getU(minU), sprite.getV(minV));
//        addVertex(vertex, poseStack.asStack(), 14/16f, 8/16f, 4/16f, sprite.getU(maxU), sprite.getV(minV));
//        addVertex(vertex, poseStack.asStack(), 14/16f,8/16f, 0, sprite.getU(maxU), sprite.getV(maxV));
//        addVertex(vertex, poseStack.asStack(),2/16f, 8/16f, 0, sprite.getU(minU), sprite.getV(maxV));
//        poseStack.pop();
//
//        poseStack.push();
//        poseStack.rotateX(-90F);
//        poseStack.translate(0, 8/16f, 4/16f);
//
//        addVertex(vertex, poseStack.asStack(), 2/16f, 8/16f, 4/16f, sprite.getU(minU), sprite.getV(minV));
//        addVertex(vertex, poseStack.asStack(), 14/16f, 8/16f, 4/16f, sprite.getU(maxU), sprite.getV(minV));
//        addVertex(vertex, poseStack.asStack(), 14/16f,8/16f, 0, sprite.getU(maxU), sprite.getV(maxV));
//        addVertex(vertex, poseStack.asStack(),2/16f, 8/16f, 0, sprite.getU(minU), sprite.getV(maxV));
//        poseStack.pop();

//        // bottom face
//
//        float height = .6f;
//

//
//        poseStack.pushPose();
//        Axis.ZP.rotationDegrees(90F);
//        poseStack.translate(-height + .15f, -1, 0);
//        addVertex(vertex, poseStack, 1, 0 + .1f, length, sprite.getU(minU), sprite.getV(minV));
//        addVertex(vertex, poseStack, 1, 1 - .1f, length, sprite.getU(maxU), sprite.getV(minV));
//        addVertex(vertex, poseStack, 1,1 - .1f, 0, sprite.getU(maxU), sprite.getV(maxV));
//        addVertex(vertex, poseStack,1, 0 + .1f, 0, sprite.getU(minU), sprite.getV(maxV));
//        poseStack.popPose();
//
//        poseStack.pushPose();
//        Axis.ZP.rotationDegrees(90F);
//        poseStack.translate(height - .15f, -1.1f, 0);
//        addVertex(vertex, poseStack, 0 + .1f, 1, length, sprite.getU(minU), sprite.getV(minV));
//        addVertex(vertex, poseStack, .15f, 1, length, sprite.getU(maxU), sprite.getV(minV));
//        addVertex(vertex, poseStack, .15f,1, 0, sprite.getU(maxU), sprite.getV(maxV));
//        addVertex(vertex, poseStack,0 + .1f, 1, 0, sprite.getU(minU), sprite.getV(maxV));
//
//        Axis.ZP.rotationDegrees(180F);
//        poseStack.translate(-.25f, -1.2f, 0);
//        addVertex(vertex, poseStack, 0 + .1f, 1, length, sprite.getU(minU), sprite.getV(minV));
//        addVertex(vertex, poseStack, .15f, 1, length, sprite.getU(maxU), sprite.getV(minV));
//        addVertex(vertex, poseStack, .15f,1, 0, sprite.getU(maxU), sprite.getV(maxV));
//        addVertex(vertex, poseStack,0 + .1f, 1, 0, sprite.getU(minU), sprite.getV(maxV));
//        poseStack.popPose();
    }


}