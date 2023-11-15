package mod.kerzox.exotek.client.render.event;

import blusunrize.immersiveengineering.client.BlockOverlayUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mcjty.lib.client.CustomRenderTypes;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.client.render.transfer.EnergyCableRenderer;
import mod.kerzox.exotek.client.render.types.ExoRenderTypes;
import mod.kerzox.exotek.common.block.transport.IConveyorBeltBlock;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.OutlineBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.event.RenderHighlightEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.ForgeModelBlockRenderer;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.*;

public class RenderLevelNetworks {

    public HashSet<BlockPos> all_cables = new HashSet<>();

    public static double calculateDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double deltaX = x2 - x1;
        double deltaY = y2 - y1;
        double deltaZ = z2 - z1;

        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        return distance;
    }

    private List<BakedQuad> getQuads(BakedModel model, BlockPos pos, Direction side, RenderType type) {
        return model.getQuads(null, null, RandomSource.create(Mth.getSeed(pos)), ModelData.EMPTY, type);
    }

    private static double[] calculateBarycentricCoordinates(double[] point, double[] vertex1, double[] vertex2, double[] vertex3) {
        double x = point[0];
        double y = point[1];

        double x1 = vertex1[0];
        double y1 = vertex1[1];
        double x2 = vertex2[0];
        double y2 = vertex2[1];
        double x3 = vertex3[0];
        double y3 = vertex3[1];

        double detT = (y2 - y3) * (x1 - x3) + (x3 - x2) * (y1 - y3);

        double alpha = ((y2 - y3) * (x - x3) + (x3 - x2) * (y - y3)) / detT;
        double beta = ((y3 - y1) * (x - x3) + (x1 - x3) * (y - y3)) / detT;
        double gamma = 1 - alpha - beta;

        return new double[]{alpha, beta, gamma};
    }

    public void test() {
        double[] squareBottomLeft = {0, 0};
        double[] squareTopRight = {8, 8};

        double[] diagonalMidpoint = {(squareBottomLeft[0] + squareTopRight[0]) / 2, (squareBottomLeft[1] + squareTopRight[1]) / 2};

        // Vertices of the four triangles
        double[][] triangle1 = {squareBottomLeft, {diagonalMidpoint[0], squareTopRight[1]}, diagonalMidpoint};
        double[][] triangle2 = {{diagonalMidpoint[0], squareBottomLeft[1]}, squareBottomLeft, diagonalMidpoint};
        double[][] triangle3 = {diagonalMidpoint, squareTopRight, {squareBottomLeft[0], diagonalMidpoint[1]}};
        double[][] triangle4 = {squareTopRight, {diagonalMidpoint[0], squareBottomLeft[1]}, {squareTopRight[0], diagonalMidpoint[1]}};

        // Print vertices of the triangles
        System.out.println("Triangle 1: " + Arrays.deepToString(triangle1));
        System.out.println("Triangle 2: " + Arrays.deepToString(triangle2));
        System.out.println("Triangle 3: " + Arrays.deepToString(triangle3));
        System.out.println("Triangle 4: " + Arrays.deepToString(triangle4));
    }

    private boolean isPointInTriangle(double[] A, double[] B, double[] C, double[] P) {
        double alpha = ((B[1] - C[1]) * (P[0] - C[0]) + (C[0] - B[0]) * (P[1] - C[1])) /
                ((B[1] - C[1]) * (A[0] - C[0]) + (C[0] - B[0]) * (A[1] - C[1]));

        double beta = ((C[1] - A[1]) * (P[0] - C[0]) + (A[0] - C[0]) * (P[1] - C[1])) /
                ((B[1] - C[1]) * (A[0] - C[0]) + (C[0] - B[0]) * (A[1] - C[1]));

        double gamma = 1 - alpha - beta;

        return alpha > 0 && beta > 0 && gamma > 0;
    }

    @SubscribeEvent()
    public void renderAdditionalBlockBounds(RenderHighlightEvent.Block event) {
        if (event.getTarget().getType() == HitResult.Type.BLOCK && event.getCamera().getEntity() instanceof LivingEntity living) {
            PoseStack transform = event.getPoseStack();
            MultiBufferSource buffer = event.getMultiBufferSource();
            BlockHitResult rtr = event.getTarget();
            BlockPos pos = rtr.getBlockPos();
            Vec3 renderView = event.getCamera().getPosition();
            transform.pushPose();
            transform.translate(-renderView.x, -renderView.y, -renderView.z);
            transform.translate(pos.getX(), pos.getY(), pos.getZ());
            float eps = 0.002F;
            BlockEntity tile = living.level().getBlockEntity(rtr.getBlockPos());
            ItemStack stack = living.getItemInHand(InteractionHand.MAIN_HAND);


            Level world =  living.level();
            if (!stack.isEmpty() && Block.byItem(stack.getItem()) instanceof IConveyorBeltBlock && rtr.getDirection().getAxis() == Direction.Axis.Y) {
                Direction side = rtr.getDirection();
                VoxelShape shape = world.getBlockState(pos).getBlockSupportShape(world, pos);
                AABB targetedBB = null;
                if (!shape.isEmpty())
                    targetedBB = shape.bounds();

                MultiBufferSource buffers = event.getMultiBufferSource();

                float y = (float) (targetedBB == null ? 0 : side == Direction.DOWN ? targetedBB.minY - eps : targetedBB.maxY + eps);
                Matrix4f mat = transform.last().pose();
                Matrix3f matN = transform.last().normal();
                VertexConsumer lineBuilder = buffers.getBuffer(ExoRenderTypes.NO_DEPTH_LINES);
                float sqrt2Half = (float) (Math.sqrt(2) / 2);
                lineBuilder.vertex(mat, 0 - eps, y, 0 - eps)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, .25f + eps, y, .25f + eps)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, .75f + eps, y, .75f + eps)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, 1 + eps, y, 1 + eps)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, 0 - eps, y, 1 + eps)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, -sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, 1 + eps - .75f, y, eps + .75f)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, -sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, .75f + eps, y, eps + .25f)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, -sqrt2Half)
                        .endVertex();
                lineBuilder.vertex(mat, 1 + eps, y, 0 + eps)
                        .color(0, 0, 0, 0.4F)
                        .normal(matN, sqrt2Half, 0, -sqrt2Half)
                        .endVertex();

                LevelRenderer.renderLineBox(transform,
                        Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                        0 + .25f,
                        y,
                        0 + .25f,
                        1 - .25f,
                        y,
                        1 - .25f, 0, 0, 0, 1.0F);

//                lineBuilder.vertex(mat, 0 - eps, y, 1 + eps)
//                        .color(0, 0, 0, 0.4F)
//                        .normal(matN, sqrt2Half, 0, -sqrt2Half)
//                        .endVertex();
//                lineBuilder.vertex(mat, 1 + eps, y, 0 - eps)
//                        .color(0, 0, 0, 0.4F)
//                        .normal(matN, sqrt2Half, 0, -sqrt2Half)
//                        .endVertex();

                float xFromMid = side.getAxis() == Direction.Axis.X ? 0 : (float) rtr.getLocation().x - pos.getX() - .5f;
                float yFromMid = side.getAxis() == Direction.Axis.Y ? 0 : (float) rtr.getLocation().y - pos.getY() - .5f;
                float zFromMid = side.getAxis() == Direction.Axis.Z ? 0 : (float) rtr.getLocation().z - pos.getZ() - .5f;
                float max = Math.max(Math.abs(yFromMid), Math.max(Math.abs(xFromMid), Math.abs(zFromMid)));
                Vec3 dir = new Vec3(
                        max == Math.abs(xFromMid) ? Math.signum(xFromMid) : 0,
                        max == Math.abs(yFromMid) ? Math.signum(yFromMid) : 0,
                        max == Math.abs(zFromMid) ? Math.signum(zFromMid) : 0);
                //BlockOverlayUtils.drawBlockOverlayArrow(transform.last(), buffers, dir, side, targetedBB);
            }
            transform.popPose();
        }
    }

    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_PARTICLES) {
            Player player = Minecraft.getInstance().player;
            PoseStack poseStack = event.getPoseStack();
            int renderTick = event.getRenderTick();
            ClientLevel level = Minecraft.getInstance().level;
            float partialTicks = event.getPartialTick();
            ItemStack item = player.getMainHandItem();
            Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
            Vec3 projectedView = renderInfo.getPosition();
            HitResult ray = event.getCamera().getEntity().pick(20.0D, 0.0F, false);

            if (player != null && player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_2_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_3_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.WRENCH_ITEM.get()) {

                int radius = 25;

                event.getCamera().getBlockPosition();

                BlockPos position = new BlockPos(player.getBlockX(), player.getBlockY(), player.getBlockZ());

//                if (ray instanceof BlockHitResult result) {
//                    BlockPos hitPos = result.getBlockPos();
//                    if (level.getBlockEntity(hitPos) instanceof EnergyBankCasingEntity entity) {
//                        BlockPos[] twoCorners = entity.getMaster().calculateMinMax();
//                        entity.getMaster().calculateMinMax();
//                        if (player.isShiftKeyDown()) {
//                            try {
//                                entity.tryValidateStructure();
//                                poseStack.pushPose();
//                                poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//                                LevelRenderer.renderLineBox(poseStack,
//                                        Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
//                                        twoCorners[0].getX(),
//                                        twoCorners[0].getY(),
//                                        twoCorners[0].getZ(),
//                                        twoCorners[1].getX() + 1,
//                                        twoCorners[1].getY() + 1,
//                                        twoCorners[1].getZ() + 1, 0,  1f, 0, 1.0F);
//                                poseStack.popPose();
//                            } catch (MultiblockException e) {
//                                player.sendSystemMessage(Component.literal(e.getMessage()));
//                            }
//                        }
//
//
//                        if (twoCorners.length != 2)return;
//
//                        int width = twoCorners[1].getX() - twoCorners[0].getX() + 1;
//                        int height = twoCorners[1].getY() - twoCorners[0].getY() + 1;
//                        int length = twoCorners[1].getZ() - twoCorners[0].getZ() + 1;
//
//                        for (int x = twoCorners[0].getX(); x <= twoCorners[1].getX(); x++) {
//                            for (int y = twoCorners[0].getY(); y <= twoCorners[1].getY(); y++) {
//                                for (int z = twoCorners[0].getZ(); z <= twoCorners[1].getZ(); z++) {
//                                    BlockPos pos = new BlockPos(x, y, z);
//                                    int relativeX = x - (twoCorners[0].getX() - 1);
//                                    int relativeY = y - (twoCorners[0].getY() - 1);
//                                    int relativeZ = z - (twoCorners[0].getZ() - 1);
//                                    if (relativeY == 1 || relativeY == height
//                                    || relativeX == 1 || relativeX == width
//                                    || relativeZ == 1 || relativeZ == length) {
//                                        poseStack.pushPose();
//                                        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//                                        LevelRenderer.renderLineBox(poseStack,
//                                                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
//                                                pos.getX(),
//                                                pos.getY(),
//                                                pos.getZ(),
//                                                pos.getX() + 1,
//                                                pos.getY() + 1,
//                                                pos.getZ() + 1, 0,  0, 1f, 1.0F);
//                                        poseStack.popPose();
//                                    }
//                                }
//                            }
//                        }
//
//
//
////                        if (twoCorners[0].getX() < twoCorners[1].getX()) {
////                            for (int x = twoCorners[0].getX(); x <= twoCorners[1].getX(); x++) {
////                                BlockPos pos = new BlockPos(x, twoCorners[0].getY(), twoCorners[0].getZ());
////                                poseStack.pushPose();
////                                poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
////                                LevelRenderer.renderLineBox(poseStack,
////                                        Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
////                                        pos.getX(),
////                                        pos.getY(),
////                                        pos.getZ(),
////                                        pos.getX() + 1,
////                                        pos.getY() + 1,
////                                        pos.getZ() + 1, 0,  0, 1f, 1.0F);
////                                poseStack.popPose();
////                            }
////                        }
//                        for (int i = 0; i < twoCorners.length; i++) {
//                            BlockPos corner = twoCorners[i];
//                            poseStack.pushPose();
//                            poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//                            LevelRenderer.renderLineBox(poseStack,
//                                    Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
//                                    corner.getX(),
//                                    corner.getY(),
//                                    corner.getZ(),
//                                    corner.getX() + 1,
//                                    corner.getY() + 1,
//                                    corner.getZ() + 1, i == 1 ? 1f : 0, i == 0 ? 1f : 0, 0, 1.0F);
//                            poseStack.popPose();
//                        }
//                    }
//                }
//
//


                level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
                    if (capability instanceof LevelEnergyNetwork network && network.getNetworks().size() != 0) {

                        for (EnergySubNetwork sub : network.getNetworks()) {

                            for (LevelNode node : sub.getNetwork().getNodes()) {

                                BlockPos cablePos = node.getWorldPosition();

                                if (calculateDistance(position.getX(), position.getY(), position.getZ(), cablePos.getX(), cablePos.getY(), cablePos.getZ()) < radius) {
                                    PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));
                                    if (level.getBlockEntity(node.getWorldPosition()) instanceof EnergyCableEntity entity) {
                                        continue;
                                    }

                                    float red = 0;
                                    float green = 1f;
                                    float blue = 0;

                                    if (sub.getTier() == CapabilityTiers.ADVANCED) {
                                        red = 1f;
                                        green = 0f;
                                        blue = 0f;
                                    }

                                    if (sub.getTier() == CapabilityTiers.HYPER) {
                                        red = 0f;
                                        green = 0f;
                                        blue = 1f;
                                    }

                                    AtomicReference<float[]> colours = new AtomicReference<>();
                                    colours.set(RenderingUtil.convertColor(0xf14f4f));

                                    BakedModel core = Minecraft.getInstance().getModelManager().getModel(EnergyCableRenderer.CORE);
                                    BakedModel core2 = Minecraft.getInstance().getModelManager().getModel(EnergyCableRenderer.CORE2);
                                    BakedModel core3 = Minecraft.getInstance().getModelManager().getModel(EnergyCableRenderer.CORE3);

                                    ForgeModelBlockRenderer renderer = (ForgeModelBlockRenderer) Minecraft.getInstance().getBlockRenderer().getModelRenderer();
                                    Direction facing = player.getDirection();
                                    BlockPos relative = node.getWorldPosition().relative(player.getDirection()).above();
                                    poseStack.pushPose();
                                    poseStack.pushPose();
                                    net.minecraft.client.gui.Font fontRenderer = Minecraft.getInstance().font;
                                    Quaternionf cameraRotation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
                                    poseStack.pushPose();
                                    poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                                    poseStack.translate(node.getWorldPosition().getX(), node.getWorldPosition().getY(), node.getWorldPosition().getZ());
                                    poseStack.translate(0.5f, 0.5f, 0.5f);
                                    poseStack.mulPose(cameraRotation);
                                    poseStack.scale(-0.01f, -0.01f, 0.01f);

//                                    poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//                                    poseStack.translate(node.getWorldPosition().getX(), node.getWorldPosition().getY(), node.getWorldPosition().getZ());

                                    Matrix4f matrix4f = poseStack.last().pose();
                                    RenderSystem.disableDepthTest();
                                    float backgroundOpacity = Minecraft.getInstance().options.getBackgroundOpacity(0.25F);
                                    int alpha = (int) (backgroundOpacity * 255.0F) << 24;
                                    int y = sub.getHandler().map(IEnergyStorage::getEnergyStored).orElse(0);
                                    Component text = Component.literal("Energy: "+y);
                                    float textOffset = -fontRenderer.width(text) / 2;
                                    OutlineBufferSource bufferIn = Minecraft.getInstance().renderBuffers().outlineBufferSource();
                                    fontRenderer.drawInBatch(text, textOffset, 0, 0xFFFFFF, false, matrix4f, bufferIn, Font.DisplayMode.SEE_THROUGH, 0, 0xFFFFFF);
                                    RenderSystem.enableDepthTest();
                                    poseStack.popPose();

                                    poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//                                for (Direction direction : Direction.values()) {
//                                    List<BakedQuad> quads = getQuads(core, node.getWorldPosition(), direction, RenderType.solid());
//                                    RenderingUtil.renderQuads(poseStack.last(), builder, red, green, blue, 1f,
//                                            quads, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
//                                }
                                    MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
                                    LevelRenderer.renderLineBox(poseStack,
                                            Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(ExoRenderTypes.NO_DEPTH_LINES),
                                            node.getWorldPosition().getX(),
                                            node.getWorldPosition().getY(),
                                            node.getWorldPosition().getZ(),
                                            node.getWorldPosition().getX() + 1,
                                            node.getWorldPosition().getY() + 1,
                                            node.getWorldPosition().getZ() + 1, red, green, blue, 1.0F);


                                    RenderSystem.disableDepthTest();        buffer.endBatch(ExoRenderTypes.NO_DEPTH_LINES);
                                    poseStack.popPose();

                                }
                            }

//drawInBatch(String pText, float pX, float pY, int pColor, boolean pDropShadow, Matrix4f pMatrix, MultiBufferSource pBuffer, Font.DisplayMode pDisplayMode, int pBackgroundColor, int pPackedLightCoords)

//                        for (int i = player.getBlockX() - radius; i < player.getBlockX() + radius; i++) {
//                            for (int j = player.getBlockY() - radius; j < player.getBlockY() + radius; j++) {
//                                for (int k = player.getBlockZ() - radius; k < player.getBlockZ() + radius; k++) {
//                                    BlockPos position = new BlockPos(i, j, k);
//                                    EnergySingleNetwork network1 = network.getNetworkFromPosition(position);
//                                    if (network1 != null) {
//
//                                        if (level.getBlockEntity(position) instanceof EnergyCableEntity entity) {
//                                            continue;
//                                        }
//
//                                        float red = 0;
//                                        float green = 1f;
//                                        float blue = 0;
//
//                                        if (network1.getTier() == PipeTiers.ADVANCED) {
//                                            red = 1f;
//                                            green = 0f;
//                                            blue = 0f;
//                                        }
//
//                                        if (network1.getTier() == PipeTiers.HYPER) {
//                                            red = 0f;
//                                            green = 0f;
//                                            blue = 1f;
//                                        }
//
//                                        Direction facing = player.getDirection();
//                                        BlockPos relative = position.relative(player.getDirection()).above();
//                                        poseStack.pushPose();
//                                        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
//                                        LevelRenderer.renderLineBox(poseStack,
//                                                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
//                                                position.getX(),
//                                                position.getY(),
//                                                position.getZ(),
//                                                position.getX() + 1,
//                                                position.getY()+ 1,
//                                                position.getZ()+ 1, red, green, blue, 1.0F);
//                                        poseStack.popPose();
//                                        RenderSystem.disableDepthTest();
//                                    }
//
//                                }
//                            }
//                        }
                        }
                    }
                });
            }
        }
    }

}