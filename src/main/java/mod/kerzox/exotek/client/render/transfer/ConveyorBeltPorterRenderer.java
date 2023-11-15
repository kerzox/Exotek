package mod.kerzox.exotek.client.render.transfer;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.model.data.ModelData;

public class ConveyorBeltPorterRenderer extends AbstractConveyorCoverRenderer {

    public static final ResourceLocation PORTER_MODEL = new ResourceLocation(Exotek.MODID, "block/conveyor_belt_porter");

    public ConveyorBeltPorterRenderer() {
        super(PORTER_MODEL);
    }

    @Override
    public void init() {
    }

    @Override
    protected void render(ConveyorBeltEntity entity, IConveyorCover cover, float partialTicks, WrappedPose poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        Direction facingTowards = cover.getFacing().getOpposite();
        poseStack.push();
        if (facingTowards == Direction.NORTH) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, getModel(), entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack.asStack(), bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
        } else if (facingTowards == Direction.SOUTH) {
            poseStack.rotateY(180);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, getModel(), entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack.asStack(), bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
        } else if (facingTowards == Direction.EAST) {
            poseStack.rotateY(270);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, getModel(), entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack.asStack(), bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
        } else if (facingTowards == Direction.WEST) {
            poseStack.rotateY(90);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, getModel(), entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack.asStack(), bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
        }
//        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
//                entity.getBlockPos(),
//                poseStack.asStack(), bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
//                RenderType.cutout());
        poseStack.pop();

        AABB bounds = cover.getCollision();

        double minX = bounds.minX - entity.getBlockPos().getX();
        double minY = bounds.minY - entity.getBlockPos().getY();
        double minZ = bounds.minZ - entity.getBlockPos().getZ();
        double maxX = bounds.maxX - entity.getBlockPos().getX();
        double maxY = bounds.maxY - entity.getBlockPos().getY();
        double maxZ = bounds.maxZ - entity.getBlockPos().getZ();

        LevelRenderer.renderLineBox(poseStack.asStack(),
                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                minX,
                minY,
                minZ,
                maxX,
                maxY,
                maxZ, 0, 0, 0, 1.0F);

    }

}
