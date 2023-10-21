package mod.kerzox.exotek.client.render.event;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.client.render.pipe.EnergyCableRenderer;
import mod.kerzox.exotek.common.blockentities.multiblock.DynamicMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.EnergyBankCasingEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.util.MultiblockException;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.lighting.ForgeModelBlockRenderer;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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

    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_PARTICLES) {
            Player player = Minecraft.getInstance().player;
            PoseStack poseStack = event.getPoseStack();
            int renderTick = event.getRenderTick();
            ClientLevel level = Minecraft.getInstance().level;
            float partialTicks = event.getPartialTick();
            ItemStack item = player.getMainHandItem();
            if (player != null && player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_2_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_3_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.WRENCH_ITEM.get()) {


                // get view position
                Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
                Vec3 projectedView = renderInfo.getPosition();
                HitResult ray = event.getCamera().getEntity().pick(20.0D, 0.0F, false);

                // PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));

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


                level.getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
                    if (capability instanceof LevelEnergyNetwork network && network.getNetworks().size() != 0) {

                        for (EnergySingleNetwork sub : network.getNetworks()) {

                            for (LevelNode node : sub.getNetwork().getNodes()) {

                                BlockPos cablePos = node.getWorldPosition();

                                if (calculateDistance(position.getX(), position.getY(), position.getZ(), cablePos.getX(), cablePos.getY(), cablePos.getZ()) < radius) {

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
                                    poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                                    VertexConsumer builder = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.solid());
//                                for (Direction direction : Direction.values()) {
//                                    List<BakedQuad> quads = getQuads(core, node.getWorldPosition(), direction, RenderType.solid());
//                                    RenderingUtil.renderQuads(poseStack.last(), builder, red, green, blue, 1f,
//                                            quads, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
//                                }

                                    LevelRenderer.renderLineBox(poseStack,
                                            Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                                            node.getWorldPosition().getX(),
                                            node.getWorldPosition().getY(),
                                            node.getWorldPosition().getZ(),
                                            node.getWorldPosition().getX() + 1,
                                            node.getWorldPosition().getY() + 1,
                                            node.getWorldPosition().getZ() + 1, red, green, blue, 1.0F);
                                    poseStack.popPose();

                                }
                            }



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