package mod.kerzox.exotek.client.render.event;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.common.blockentities.multiblock.data.BlockPredicate;
import mod.kerzox.exotek.common.blockentities.multiblock.data.MultiblockPattern;
import mod.kerzox.exotek.common.blockentities.transport.PipeTiers;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_SKY;
import static net.minecraftforge.client.event.RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS;

public class RenderLevelNetworks {

    @SubscribeEvent
    public void onLevelRender(RenderLevelStageEvent event) {
        if (event.getStage() == AFTER_SOLID_BLOCKS) {
            Player player = Minecraft.getInstance().player;
            PoseStack poseStack = event.getPoseStack();
            int renderTick = event.getRenderTick();
            ClientLevel level = Minecraft.getInstance().level;
            float partialTicks = event.getPartialTick();
            if (player != null && player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_2_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_3_ITEM.get()
                    || player.getMainHandItem().getItem() == Registry.Items.WRENCH_ITEM.get()) {


                // get view position
                Camera renderInfo = Minecraft.getInstance().gameRenderer.getMainCamera();
                Vec3 projectedView = renderInfo.getPosition();
                HitResult ray = event.getCamera().getEntity().pick(20.0D, 0.0F, false);

                PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));

                int radius = 25;

                level.getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
                    if (capability instanceof LevelEnergyNetwork network && network.getNetworks().size() != 0) {

                        for (int i = player.getBlockX() - radius; i < player.getBlockX() + radius; i++) {
                            for (int j = player.getBlockY() - radius; j < player.getBlockY() + radius; j++) {
                                for (int k = player.getBlockZ() - radius; k < player.getBlockZ() + radius; k++) {
                                    BlockPos position = new BlockPos(i, j, k);
                                    EnergySingleNetwork network1 = network.getNetworkFromPosition(position);
                                    if (network1 != null) {
                                        float red = 0;
                                        float green = 1f;
                                        float blue = 0;

                                        if (network1.getTier() == PipeTiers.ADVANCED) {
                                            red = 1f;
                                            green = 0f;
                                            blue = 0f;
                                        }

                                        if (network1.getTier() == PipeTiers.HYPER) {
                                            red = 0f;
                                            green = 0f;
                                            blue = 1f;
                                        }

                                        Direction facing = player.getDirection();
                                        BlockPos relative = position.relative(player.getDirection()).above();
                                        poseStack.pushPose();
                                        poseStack.translate(-projectedView.x, -projectedView.y, -projectedView.z);
                                        LevelRenderer.renderLineBox(poseStack,
                                                Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.LINES),
                                                position.getX(),
                                                position.getY(),
                                                position.getZ(),
                                                position.getX() + 1,
                                                position.getY()+ 1,
                                                position.getZ()+ 1, red, green, blue, 1.0F);
                                        poseStack.popPose();
                                    }
                                }
                            }
                        }
                    }
                });
            }
        }
    }


}
