package mod.kerzox.exotek.client.render.transfer;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltRampEntity;
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

public class ConveyorBeltRampRenderer implements BlockEntityRenderer<ConveyorBeltRampEntity> {

   // public static final ResourceLocation CORE = new ResourceLocation(Exotek.MODID, "block/energy_cable");
   public static final ResourceLocation BELT_MODEL = new ResourceLocation(Exotek.MODID, "block/conveyor_belt_texture");

    public ConveyorBeltRampRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    public static float getPartialTicks() {
        Minecraft mc = Minecraft.getInstance();
        return mc.getFrameTime();
    }

    @Override
    public void render(ConveyorBeltRampEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        WrappedPose pose = WrappedPose.of(poseStack);
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(BELT_MODEL);
        Direction facing = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        TextureAtlasSprite sprite = model.getParticleIcon();
        float speedMult = entity.getSpeed();
        float scrollV = TickEvent.clientRenderTick * speedMult % (8);
//        float minV = scrollV < 8 ? scrollV : scrollV - 8;
//        float maxV = scrollV > 8 ? scrollV : scrollV + 8;
//        System.out.println(scrollV);

        if (entity.getVerticalDirection() == Direction.UP) {
            if (facing == Direction.SOUTH) {
                pose.rotateY(180);
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV, scrollV + 8);
            }
            if (facing == Direction.NORTH) {
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV, scrollV + 8);
            }
            if (facing == Direction.EAST) {
                pose.rotateY(270);
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV, scrollV + 8);
            }
            if (facing == Direction.WEST) {
                pose.rotateY(90);
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV, scrollV + 8);
            }
        } else {
            if (facing == Direction.SOUTH) {
                pose.rotateY(180);
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV + 8, scrollV);
            }
            if (facing == Direction.NORTH) {
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV + 8, scrollV);
            }
            if (facing == Direction.EAST) {
                pose.rotateY(270);
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV + 8, scrollV);
            }
            if (facing == Direction.WEST) {
                pose.rotateY(90);
                pose.rotateX(45);
                drawBelt(pose, sprite, bufferSource.getBuffer(RenderType.cutout()), 1,0, 16, scrollV + 8, scrollV);
            }
        }

    }

    public static void drawBelt(WrappedPose poseStack, TextureAtlasSprite sprite, VertexConsumer vertex, float length, float minU, float maxU, float minV, float maxV) {
        RenderSystem.enableDepthTest();
        poseStack.translate(0, -2.75f/16f, -8.5f/16f);
        poseStack.push();
        poseStack.asStack().scale(1, 1, 1.425f);
        addVertex(vertex, poseStack.asStack(), 2/16f, 1, length, sprite.getU(minU + 2), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 14/16f, 1, length, sprite.getU(maxU - 2), sprite.getV(maxV));
        addVertex(vertex, poseStack.asStack(), 14/16f,1, 0, sprite.getU(maxU - 2), sprite.getV(minV));
        addVertex(vertex, poseStack.asStack(),2/16f, 1, 0, sprite.getU(minU + 2), sprite.getV(minV));
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