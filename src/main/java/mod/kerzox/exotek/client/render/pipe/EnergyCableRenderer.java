package mod.kerzox.exotek.client.render.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockInvisibleEntity;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.ForgeModelBlockRenderer;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class EnergyCableRenderer implements BlockEntityRenderer<EnergyCableEntity> {

    public static final ResourceLocation CONNECTED_NORTH = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection");
    public static final ResourceLocation CONNECTED_EAST = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_east");
    public static final ResourceLocation CONNECTED_SOUTH = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_south");
    public static final ResourceLocation CONNECTED_WEST = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_west");
    public static final ResourceLocation CONNECTED_UP = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_up");
    public static final ResourceLocation CONNECTED_DOWN = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_down");
    public static final ResourceLocation CORE = new ResourceLocation(Exotek.MODID, "block/energy_cable");


    public EnergyCableRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(EnergyCableEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        WrappedPose pose = WrappedPose.of(poseStack);

        AtomicReference<float[]> colours = new AtomicReference<>();
        colours.set(RenderingUtil.convertColor(0xf14f4f));
        ForgeModelBlockRenderer renderer = (ForgeModelBlockRenderer) Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        AtomicReference<Boolean> hasConnections = new AtomicReference<>(false);

        BakedModel connection_north = Minecraft.getInstance().getModelManager().getModel(CONNECTED_NORTH);
        BakedModel connection_east = Minecraft.getInstance().getModelManager().getModel(CONNECTED_EAST);
        BakedModel connection_south = Minecraft.getInstance().getModelManager().getModel(CONNECTED_SOUTH);
        BakedModel connection_west = Minecraft.getInstance().getModelManager().getModel(CONNECTED_WEST);
        BakedModel connection_up = Minecraft.getInstance().getModelManager().getModel(CONNECTED_UP);
        BakedModel connection_down = Minecraft.getInstance().getModelManager().getModel(CONNECTED_DOWN);
        BakedModel core = Minecraft.getInstance().getModelManager().getModel(CORE);

        HashMap<Direction, BakedModel> cached = new HashMap<>(){{
            put(Direction.NORTH, connection_north);
            put(Direction.EAST, connection_east);
            put(Direction.SOUTH, connection_south);
            put(Direction.WEST, connection_west);
            put(Direction.UP, connection_up);
            put(Direction.DOWN, connection_down);
        }};

        pose.push();

        // base model
        render(renderer, entity,
                pose,
                core,
                entity.getBlockState(),
                entity.getBlockPos(),
                bufferSource.getBuffer(RenderType.solid()),
                false,
                Minecraft.getInstance().level.getRandom(),
                entity.getBlockPos().asLong(),
                combinedOverlay,
                ModelData.EMPTY,
                RenderType.cutout());


        for (Direction direction : entity.getConnectionsAsDirections()) {
            render(renderer, entity,
                    pose,
                    cached.get(direction),
                    entity.getBlockState(),
                    entity.getBlockPos(),
                    bufferSource.getBuffer(RenderType.solid()),
                    false,
                    Minecraft.getInstance().level.getRandom(),
                    entity.getBlockPos().asLong(),
                    combinedOverlay,
                    ModelData.EMPTY,
                    RenderType.cutout());
        }

        pose.pop();

    }

    private void render(ForgeModelBlockRenderer renderer,
                            EnergyCableEntity entity,
                            WrappedPose pose,
                            BakedModel model,
                            BlockState blockState,
                            BlockPos blockPos,
                            VertexConsumer buffer,
                            boolean checkSides,
                            RandomSource random,
                            long seed,
                            int packedOverlay,
                            ModelData data,
                            RenderType type) {
        renderer.tesselateWithAO(Minecraft.getInstance().level, model, blockState, blockPos, pose.asStack(), buffer, checkSides, random, seed, packedOverlay, data, type);
    }

}