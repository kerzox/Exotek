package mod.kerzox.exotek.client.render.transfer;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.ForgeModelBlockRenderer;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class FluidPipeRenderer  implements BlockEntityRenderer<FluidPipeEntity> {

    public static final ResourceLocation CONNECTION_MODEL_NORTH = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_connector");
    public static final ResourceLocation CONNECTION_MODEL_SOUTH = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_connector_south");
    public static final ResourceLocation CONNECTION_MODEL_EAST = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_connector_east");
    public static final ResourceLocation CONNECTION_MODEL_WEST = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_connector_west");
    public static final ResourceLocation CONNECTION_MODEL_DOWN = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_connector_down");
    public static final ResourceLocation CONNECTION_MODEL_UP = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_connector_up");

    // corners
    public static final ResourceLocation CONNECTION_MODEL_CORNER1 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner1");
    public static final ResourceLocation CONNECTION_MODEL_CORNER2 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner2");
    public static final ResourceLocation CONNECTION_MODEL_CORNER3 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner3");
    public static final ResourceLocation CONNECTION_MODEL_CORNER4 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner4");

    public static final ResourceLocation CONNECTION_MODEL_CORNER5 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner5");
    public static final ResourceLocation CONNECTION_MODEL_CORNER6 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner6");
    public static final ResourceLocation CONNECTION_MODEL_CORNER7 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner7");
    public static final ResourceLocation CONNECTION_MODEL_CORNER8 = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_corner8");

    // when pipe has connection change body for this one
    public static final ResourceLocation CONNECTED_BODY_AXIS_Z = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_connected");
    public static final ResourceLocation CONNECTED_BODY_AXIS_X = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_connected_xaxis");
    public static final ResourceLocation CONNECTED_BODY_AXIS_Y = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_body_connected_z");


    // default pipe body
    public static final ResourceLocation BODY = new ResourceLocation(Exotek.MODID, "block/fluid_pipe");
    public static final ResourceLocation BIG_BODY = new ResourceLocation(Exotek.MODID, "block/fluid_pipe_big_body");

    public FluidPipeRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(FluidPipeEntity entity, float pPartialTick, PoseStack poseStack, MultiBufferSource bufferSource, int pPackedLight, int pPackedOverlay) {
        WrappedPose pose = WrappedPose.of(poseStack);
        BakedModel connected_body = Minecraft.getInstance().getModelManager().getModel(CONNECTED_BODY_AXIS_Z);
        BakedModel connected_body2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_BODY_AXIS_X);
        BakedModel connected_body3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_BODY_AXIS_Y);
        BakedModel body = Minecraft.getInstance().getModelManager().getModel(BODY);
        ForgeModelBlockRenderer renderer = (ForgeModelBlockRenderer) Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        AtomicReference<Boolean> hasConnections = new AtomicReference<>(false);

        getConnections(entity).forEach(((direction, connected) -> {
            if (connected) {
                hasConnections.set(true);
                renderConnectedPipeIn(renderer, entity, direction,
                        pose,
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        pPackedLight,
                        pPackedOverlay,
                        ModelData.EMPTY,
                        RenderType.solid());
            }
        }));

        Collection<Direction> directions = getConnectedDirectionsOnly(entity);

        if (directions.isEmpty()) {
            renderBody(renderer, entity,
                    pose,
                    body,
                    entity.getBlockState(),
                    entity.getBlockPos(),
                    bufferSource.getBuffer(RenderType.solid()),
                    false,
                    Minecraft.getInstance().level.getRandom(),
                    entity.getBlockPos().asLong(),
                    pPackedOverlay,
                    ModelData.EMPTY,
                    RenderType.cutout());
            return;
        }

        // find if we need to be a block
        if (directions.size() >= 3) {
            renderBody(renderer, entity,
                    pose,
                    Minecraft.getInstance().getModelManager().getModel(BIG_BODY),
                    entity.getBlockState(),
                    entity.getBlockPos(),
                    bufferSource.getBuffer(RenderType.solid()),
                    false,
                    Minecraft.getInstance().level.getRandom(),
                    entity.getBlockPos().asLong(),
                    0,
                    ModelData.EMPTY,
                    RenderType.cutout());
            return;
        }
        // else if we are a corner piece
        if (directions.size() == 2) {
            if (directions.contains(Direction.EAST) && directions.contains(Direction.UP)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER5),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.EAST) && directions.contains(Direction.DOWN)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER6),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.WEST) && directions.contains(Direction.UP)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER7),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.WEST) && directions.contains(Direction.DOWN)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER8),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.EAST) && directions.contains(Direction.NORTH)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER1),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.EAST) && directions.contains(Direction.SOUTH)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER2),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.WEST) && directions.contains(Direction.NORTH) ) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER3),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
            else if (directions.contains(Direction.WEST) && directions.contains(Direction.SOUTH)) {
                renderBody(renderer, entity,
                        pose,
                        Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_CORNER4),
                        entity.getBlockState(),
                        entity.getBlockPos(),
                        bufferSource.getBuffer(RenderType.solid()),
                        false,
                        Minecraft.getInstance().level.getRandom(),
                        entity.getBlockPos().asLong(),
                        0,
                        ModelData.EMPTY,
                        RenderType.cutout());
                return;
            }
        }

        if (directions.stream().anyMatch(d -> d.getAxis() == Direction.Axis.X)) {
            renderBody(renderer, entity,
                    pose,
                    connected_body2,
                    entity.getBlockState(),
                    entity.getBlockPos(),
                    bufferSource.getBuffer(RenderType.solid()),
                    false,
                    Minecraft.getInstance().level.getRandom(),
                    entity.getBlockPos().asLong(),
                    0,
                    ModelData.EMPTY,
                    RenderType.cutout());

        } else if (directions.stream().anyMatch(d -> d.getAxis() == Direction.Axis.Z)) {
            renderBody(renderer, entity,
                    pose,
                    connected_body,
                    entity.getBlockState(),
                    entity.getBlockPos(),
                    bufferSource.getBuffer(RenderType.solid()),
                    false,
                    Minecraft.getInstance().level.getRandom(),
                    entity.getBlockPos().asLong(),
                    pPackedOverlay,
                    ModelData.EMPTY,
                    RenderType.cutout());
        } else {
            renderBody(renderer, entity,
                    pose,
                    connected_body3,
                    entity.getBlockState(),
                    entity.getBlockPos(),
                    bufferSource.getBuffer(RenderType.solid()),
                    false,
                    Minecraft.getInstance().level.getRandom(),
                    entity.getBlockPos().asLong(),
                    pPackedOverlay,
                    ModelData.EMPTY,
                    RenderType.cutout());
        }
    }

    private void renderBody(ForgeModelBlockRenderer renderer, FluidPipeEntity entity, WrappedPose pose, BakedModel model, BlockState blockState, BlockPos blockPos, VertexConsumer buffer, boolean checkSides, RandomSource random, long seed, int packedOverlay, ModelData data,
                            RenderType type) {
        renderer.tesselateWithAO(Minecraft.getInstance().level, model, blockState, blockPos, pose.asStack(), buffer, checkSides, random, seed, packedOverlay, data, type);
    }

    private void renderConnectedPipeIn(ForgeModelBlockRenderer renderer, FluidPipeEntity entity, Direction direction, WrappedPose pose, BlockState blockState, BlockPos blockPos, VertexConsumer buffer, boolean checkSides, RandomSource random, long seed, int packedLight, int packedOverlay, ModelData data,
                                       RenderType type) {
        if (Minecraft.getInstance().level == null) return;
        pose.push();
        BakedModel model = Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_NORTH);

        if (direction == Direction.SOUTH) {
            model = Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_SOUTH);
        }
       else if (direction == Direction.UP) {
            model = Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_UP);
        }
        else if (direction == Direction.DOWN) {
            model = Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_DOWN);
        }
        else if (direction == Direction.WEST) {
            model = Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_WEST);
        }
        else if (direction == Direction.EAST) {
            model = Minecraft.getInstance().getModelManager().getModel(CONNECTION_MODEL_EAST);
        }

//        for (Direction direction2 : Direction.values()) {
//            List<BakedQuad> quads = getQuads(connector, entity, direction, RenderType.cutout());
//            RenderingUtil.renderQuads(pose.last(), buffer, RenderingUtil.convertColor(0xFFffffff)[0], RenderingUtil.convertColor(0xFFffffff)[1], RenderingUtil.convertColor(0xFFffffff)[2], 1f,
//                    quads, packedLight, packedOverlay);
//        }
        renderer.tesselateWithAO(Minecraft.getInstance().level, model, blockState, blockPos, pose.asStack(), buffer, true, random, seed, packedOverlay, data, type);

        pose.pop();
    }

    private List<BakedQuad> getQuads(BakedModel model, BlockEntity tile, Direction side, RenderType type) {
        return model.getQuads(null, null, RandomSource.create(Mth.getSeed(tile.getBlockPos())), ModelData.EMPTY, type);
    }

    private Collection<Direction> getConnectedDirectionsOnly(FluidPipeEntity entity) {
        Map<Direction, Boolean> connections = getConnections(entity);
        if (connections.isEmpty()) return Collections.emptyList();
        return Arrays.stream(Direction.values()).filter(connections::get).collect(Collectors.toList());
    }

    private Map<Direction, Boolean> getConnections(FluidPipeEntity entity) {
        Map<Direction, Boolean> connections = new HashMap<>();
        CompoundTag tag = entity.getConnectionTag();
        ListTag list = tag.getList("pipe_connections", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag listItem = list.getCompound(i);
            connections.put(Direction.byName(listItem.getString("direction")), listItem.getBoolean("connection"));
        }
        return connections;
    }

}
