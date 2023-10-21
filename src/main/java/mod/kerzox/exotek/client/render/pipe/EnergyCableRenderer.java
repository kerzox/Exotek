package mod.kerzox.exotek.client.render.pipe;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.ILevelNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.ForgeModelBlockRenderer;
import net.minecraftforge.common.util.LazyOptional;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicReference;

public class EnergyCableRenderer implements BlockEntityRenderer<EnergyCableEntity> {

    public static final ResourceLocation CONNECTED_NORTH = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection");
    public static final ResourceLocation CONNECTED_EAST = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_east");
    public static final ResourceLocation CONNECTED_SOUTH = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_south");
    public static final ResourceLocation CONNECTED_WEST = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_west");
    public static final ResourceLocation CONNECTED_UP = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_up");
    public static final ResourceLocation CONNECTED_DOWN = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_down");
    public static final ResourceLocation CORE = new ResourceLocation(Exotek.MODID, "block/energy_cable");

    public static final ResourceLocation CONNECTED_NORTH2 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection2");
    public static final ResourceLocation CONNECTED_EAST2 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_east2");
    public static final ResourceLocation CONNECTED_SOUTH2 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_south2");
    public static final ResourceLocation CONNECTED_WEST2 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_west2");
    public static final ResourceLocation CONNECTED_UP2 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_up2");
    public static final ResourceLocation CONNECTED_DOWN2 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_down2");
    public static final ResourceLocation CORE2 = new ResourceLocation(Exotek.MODID, "block/energy_cable2");

    public static final ResourceLocation CONNECTED_NORTH3 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection3");
    public static final ResourceLocation CONNECTED_EAST3 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_east3");
    public static final ResourceLocation CONNECTED_SOUTH3 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_south3");
    public static final ResourceLocation CONNECTED_WEST3 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_west3");
    public static final ResourceLocation CONNECTED_UP3 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_up3");
    public static final ResourceLocation CONNECTED_DOWN3 = new ResourceLocation(Exotek.MODID, "block/energy_cable_connection_down3");
    public static final ResourceLocation CORE3 = new ResourceLocation(Exotek.MODID, "block/energy_cable3");

    HashMap<Direction, BakedModel> cached = new HashMap<>();
    HashMap<Direction, BakedModel> cached2 = new HashMap<>();
    HashMap<Direction, BakedModel> cached3 = new HashMap<>();

    public EnergyCableRenderer(BlockEntityRendererProvider.Context pContext) {
        BakedModel connection_north = Minecraft.getInstance().getModelManager().getModel(CONNECTED_NORTH);
        BakedModel connection_east = Minecraft.getInstance().getModelManager().getModel(CONNECTED_EAST);
        BakedModel connection_south = Minecraft.getInstance().getModelManager().getModel(CONNECTED_SOUTH);
        BakedModel connection_west = Minecraft.getInstance().getModelManager().getModel(CONNECTED_WEST);
        BakedModel connection_up = Minecraft.getInstance().getModelManager().getModel(CONNECTED_UP);
        BakedModel connection_down = Minecraft.getInstance().getModelManager().getModel(CONNECTED_DOWN);

        BakedModel connection_north2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_NORTH2);
        BakedModel connection_east2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_EAST2);
        BakedModel connection_south2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_SOUTH2);
        BakedModel connection_west2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_WEST2);
        BakedModel connection_up2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_UP2);
        BakedModel connection_down2 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_DOWN2);

        BakedModel connection_north3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_NORTH3);
        BakedModel connection_east3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_EAST3);
        BakedModel connection_south3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_SOUTH3);
        BakedModel connection_west3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_WEST3);
        BakedModel connection_up3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_UP3);
        BakedModel connection_down3 = Minecraft.getInstance().getModelManager().getModel(CONNECTED_DOWN3);

        cached = new HashMap<>() {{
            put(Direction.NORTH, connection_north);
            put(Direction.EAST, connection_east);
            put(Direction.SOUTH, connection_south);
            put(Direction.WEST, connection_west);
            put(Direction.UP, connection_up);
            put(Direction.DOWN, connection_down);
        }};

        cached2 = new HashMap<>() {{
            put(Direction.NORTH, connection_north2);
            put(Direction.EAST, connection_east2);
            put(Direction.SOUTH, connection_south2);
            put(Direction.WEST, connection_west2);
            put(Direction.UP, connection_up2);
            put(Direction.DOWN, connection_down2);
        }};

        cached3 = new HashMap<>() {{
            put(Direction.NORTH, connection_north3);
            put(Direction.EAST, connection_east3);
            put(Direction.SOUTH, connection_south3);
            put(Direction.WEST, connection_west3);
            put(Direction.UP, connection_up3);
            put(Direction.DOWN, connection_down3);
        }};

    }

    @Override
    public void render(EnergyCableEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        WrappedPose pose = WrappedPose.of(poseStack);

        AtomicReference<float[]> colours = new AtomicReference<>();
        colours.set(RenderingUtil.convertColor(0xf14f4f));
        ForgeModelBlockRenderer renderer = (ForgeModelBlockRenderer) Minecraft.getInstance().getBlockRenderer().getModelRenderer();
        AtomicReference<Boolean> hasConnections = new AtomicReference<>(false);

        LazyOptional<ILevelNetwork> capability = entity.getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY);
        if (!capability.isPresent()) return;
        if (capability.resolve().isEmpty()) return;
        if (capability.resolve().get() instanceof LevelEnergyNetwork network) {

            EnergySingleNetwork subnet = network.getNetworkFromPosition(entity.getBlockPos());

            if (subnet == null) return;


            pose.push();

            // base model
            render(renderer, entity,
                    pose,
                    getCoreModelFromTier(subnet.getTier()),
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
                        getConnectionFromTier(subnet.getTier(), direction),
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
    }

    private BakedModel getCoreModelFromTier(CapabilityTiers tier) {
        BakedModel core = Minecraft.getInstance().getModelManager().getModel(CORE);
        BakedModel core2 = Minecraft.getInstance().getModelManager().getModel(CORE2);
        BakedModel core3 = Minecraft.getInstance().getModelManager().getModel(CORE3);
        switch (tier) {
            case ADVANCED -> {
                return core2;
            }
            case HYPER -> {
                return core3;
            }
            default -> {
                return core;
            }
        }
    }

    private BakedModel getConnectionFromTier(CapabilityTiers tier, Direction direction) {
        switch (tier) {
            case ADVANCED -> {
                return cached2.get(direction);
            }
            case HYPER -> {
                return cached3.get(direction);
            }
            default -> {
                return cached.get(direction);
            }
        }
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