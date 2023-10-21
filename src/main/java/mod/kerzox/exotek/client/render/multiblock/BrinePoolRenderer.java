package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.BrinePoolEntity;
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

import java.util.Arrays;
import java.util.stream.Collectors;


public class BrinePoolRenderer implements BlockEntityRenderer<BrinePoolEntity> {

    public static final ResourceLocation POOL = new ResourceLocation(Exotek.MODID, "block/brine_pool");
    public static final ResourceLocation NORTH_WALL = new ResourceLocation(Exotek.MODID, "block/brine_pool_north");
    public static final ResourceLocation EAST_WALL = new ResourceLocation(Exotek.MODID, "block/brine_pool_east");
    public static final ResourceLocation SOUTH_WALL = new ResourceLocation(Exotek.MODID, "block/brine_pool_south");
    public static final ResourceLocation WEST_WALL = new ResourceLocation(Exotek.MODID, "block/brine_pool_west");


    public BrinePoolRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(BrinePoolEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(POOL);
        BakedModel north = Minecraft.getInstance().getModelManager().getModel(NORTH_WALL);
        BakedModel east = Minecraft.getInstance().getModelManager().getModel(EAST_WALL);
        BakedModel south = Minecraft.getInstance().getModelManager().getModel(SOUTH_WALL);
        BakedModel west = Minecraft.getInstance().getModelManager().getModel(WEST_WALL);
        WrappedPose pose = WrappedPose.of(poseStack);
        pose.push();
        Direction facing = entity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                entity.getBlockPos(),
                poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                RenderType.cutout());


        for (Direction direction : Arrays.stream(Direction.values().clone()).filter(d -> !entity.getNeighbouringEntities().containsKey(d)).collect(Collectors.toSet())) {
            // render this wall
            if (direction == Direction.NORTH) {
               // pose.translate(0.5f, 0, 0.5f);
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, north, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
            }

            if (direction == Direction.EAST) {
                // pose.translate(0.5f, 0, 0.5f);
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, east, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
            }

            if (direction == Direction.SOUTH) {
                // pose.translate(0.5f, 0, 0.5f);
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, south, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
            }

            if (direction == Direction.WEST) {
                // pose.translate(0.5f, 0, 0.5f);
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, west, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
            }

        }

        pose.pop();
    }
}
