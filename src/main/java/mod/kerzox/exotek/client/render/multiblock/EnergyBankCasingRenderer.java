package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.client.render.types.ExoRenderTypes;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.EnergyBankCasingEntity;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.SyncBlockEntityAtPosition;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;

import java.nio.ByteBuffer;
import java.util.Set;


public class EnergyBankCasingRenderer implements BlockEntityRenderer<EnergyBankCasingEntity> {

    public static final ResourceLocation MODEL_UNFORMED = new ResourceLocation(Exotek.MODID, "block/energy_bank_casing");
    public static final ResourceLocation MODEL_FORMED = new ResourceLocation(Exotek.MODID, "block/energy_bank_casing_formed");
    public static final ResourceLocation MODEL_FRAME = new ResourceLocation(Exotek.MODID, "block/energy_bank_casing_frame");

    public EnergyBankCasingRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(EnergyBankCasingEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        BakedModel model = Minecraft.getInstance().getModelManager().getModel(MODEL_UNFORMED);
        BakedModel model2 = Minecraft.getInstance().getModelManager().getModel(MODEL_FORMED);
        BakedModel model3 = Minecraft.getInstance().getModelManager().getModel(MODEL_FRAME);

        float minX = 0;
        float minY = 0;
        float minZ = 0;

        float maxX = 1;
        float maxY = 1;
        float maxZ = 1;

        float red = 1f;
        float green = 0;
        float blue = 0;
        float alpha = 1f;

        //
        if (!entity.isFormed()) {
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, entity.getBlockState(),
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
            PacketHandler.sendToServer(new SyncBlockEntityAtPosition(entity.getBlockPos()));
            return;
        }

        poseStack.pushPose();
//        Set<Direction> directions = new HashSet<>();
//        for (Direction direction : Direction.values()) {
//            if (entity.getLevel().getBlockEntity(entity.getBlockPos().relative(direction)) instanceof EnergyBankCasingEntity entity1) {
//                directions.add(direction);
//            }
//        }
        Set<Direction> directions = entity.getNeighbouringEntities().keySet();

        if (directions.isEmpty()) {
            // if we are formed and this happens then the client must be behind in updates just quickly sync.
            PacketHandler.sendToServer(new SyncBlockEntityAtPosition(entity.getBlockPos()));
        }

        int tint = 0xFFaaaaaa;
        int packedLight = LightTexture.pack(Minecraft.getInstance().level.getBrightness(LightLayer.BLOCK, entity.getBlockPos()), Minecraft.getInstance().level.getBrightness(LightLayer.SKY, entity.getBlockPos()));

        Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model2, entity.getBlockState(),
                entity.getBlockPos(),
                poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                RenderType.cutout());

        if (entity.getNeighbouringEntities().isEmpty()) return;

        // we dont have a block north (TODO redo this so its rendering more than it has too (this was due to me rendering individual quads on each side nows its blocks but i can't be bothered to fix it.
        if (!directions.contains(Direction.NORTH)) {
            // now check for surrounding

            // draw up/down borders
            if (!directions.contains(Direction.EAST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
               // RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.NORTH, packedLight, 1, minY, minZ, 15 / 16f, maxY, maxZ, tint);
            }

            if (!directions.contains(Direction.WEST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
            }

            // draw borders left to right
            if (!directions.contains(Direction.UP)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              // RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.NORTH, packedLight, 0, 15 / 16f, minZ, 1, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.DOWN)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.NORTH, packedLight, 0, 0, minZ, 1, 1 / 16f, maxZ, tint);
            }

        }
        if (!directions.contains(Direction.SOUTH)) {
            // now check for surrounding

            // draw up/down borders
            if (!directions.contains(Direction.EAST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.SOUTH, packedLight, 1, minY, minZ, 15 / 16f, maxY, maxZ, tint);
            }

            if (!directions.contains(Direction.WEST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.SOUTH, packedLight, 0, minY, minZ, 1 / 16f, maxY, maxZ, tint);
            }

            // draw borders left to right
            if (!directions.contains(Direction.UP)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
               // RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.SOUTH, packedLight, 0, 15 / 16f, minZ, 1, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.DOWN)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.SOUTH, packedLight, 0, 0, minZ, 1, 1 / 16f, maxZ, tint);
            }

        }
        if (!directions.contains(Direction.WEST)) {
            // now check for surrounding

            // draw up/down borders
            if (!directions.contains(Direction.NORTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.WEST, packedLight, minX, minY, minZ, maxX, maxY, 1 / 16f, tint);
            }
            if (!directions.contains(Direction.SOUTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.WEST, packedLight, minX, minY, 1, maxX, maxY, 15 / 16f, tint);
            }

            // draw borders left to right
            if (!directions.contains(Direction.UP)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.WEST, packedLight, 0, 15 / 16f, minZ, 1, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.DOWN)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
               // RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.WEST, packedLight, 0, 0, minZ, 1, 1 / 16f, maxZ, tint);
            }

        }
        if (!directions.contains(Direction.EAST)) {
            // now check for surrounding

            // draw up/down borders
            if (!directions.contains(Direction.NORTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.EAST, packedLight, minX, minY, minZ, maxX, maxY, 1 / 16f, tint);
            }
            if (!directions.contains(Direction.SOUTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
             //   RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.EAST, packedLight, minX, minY, 1, maxX, maxY, 15 / 16f, tint);
            }

            // draw borders left to right
            if (!directions.contains(Direction.UP)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.EAST, packedLight, 0, 15 / 16f, minZ, 1, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.DOWN)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
             //   RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.EAST, packedLight, 0, 0, minZ, 1, 1 / 16f, maxZ, tint);
            }

        }
        if (!directions.contains(Direction.UP)) {
            if (!directions.contains(Direction.EAST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
             //   RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.UP, packedLight, 1, 0, minZ, 15 / 16f, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.WEST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
             //   RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.UP, packedLight, 1 / 16f, 0, minZ, 0, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.NORTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
             //   RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.UP, packedLight, 0, 0, 1 / 16f, 1, maxY, 0, tint);
            }
            if (!directions.contains(Direction.SOUTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
             //   RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.UP, packedLight, 0, 0, 15 / 16f, 1, maxY, maxZ, tint);
            }
        }

        if (!directions.contains(Direction.DOWN)) {
            if (!directions.contains(Direction.EAST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
            //    RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.DOWN, packedLight, 1, 0, minZ, 15 / 16f, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.WEST)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.DOWN, packedLight, 1 / 16f, 0, minZ, 0, maxY, maxZ, tint);
            }
            if (!directions.contains(Direction.NORTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.DOWN, packedLight, 0, 0, 1 / 16f, 1, maxY, 0, tint);
            }
            if (!directions.contains(Direction.SOUTH)) {
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model3, entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());
              //  RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_COLOUR), Direction.DOWN, packedLight, 0, 0, 15 / 16f, 1, maxY, maxZ, tint);
            }
        }



//        if (!directions.contains(Direction.SOUTH)) {
//            if (!directions.contains(Direction.NORTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 1, minY, 1/16f, 0, maxY, 0, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 0, 1, maxY, 1/16f, 0xFF212121);
//            }
//            if (!directions.contains(Direction.SOUTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 15/16f, 1, maxY, 1, 0xFF212121);
//            }
//        }
//        if (!directions.contains(Direction.NORTH)) {
//            if (!directions.contains(Direction.NORTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 1, minY, 1/16f, 0, maxY, 0, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 0, 1, maxY, 1/16f, 0xFF212121);
//            }
//            if (!directions.contains(Direction.SOUTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 15/16f, 1, maxY, 1, 0xFF212121);
//            }
//        }
//
//        if (!directions.contains(Direction.WEST)) {
//            if (!directions.contains(Direction.NORTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 0, minY, 0, 1, maxY, 1/16f, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 0, 1, maxY, 1/16f, 0xFF212121);
//            }
//            if (!directions.contains(Direction.SOUTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 0, minY, 1, 1, maxY, 15/16f, 0xFF212121);
//            }
//
//            RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, minZ, 1/16f, maxY, maxZ, 0xFF212121);
//            RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.NORTH, 0, minY, minZ, 1/16f, maxY, maxZ, 0xFF212121);
//            RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.SOUTH, 0, minY, minZ, 1/16f, maxY, maxZ, 0xFF212121);
//
//        }
//        if (!directions.contains(Direction.EAST)) {
//            if (!directions.contains(Direction.NORTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 1, minY, 1/16f, 0, maxY, 0, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 0, 1, maxY, 1/16f, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 0, minY, 1, 1, maxY, 15/16f, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 15/16f, minY, minZ, 1, maxY, maxZ, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.NORTH, 1, minY, minZ, 15/16f, maxY, maxZ, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.EAST, 1, minY, minZ, 15/16f, maxY, maxZ, 0xFF212121);
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.SOUTH, 1, minY, minZ, 15/16f, maxY, maxZ, 0xFF212121);
//            }
//            if (!directions.contains(Direction.SOUTH)) {
//                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 15/16f, 1, maxY, 1, 0xFF212121);
//            }
//
//
//        }

        poseStack.popPose();


    }

    /*


        if (directions.contains(Direction.EAST) && directions.contains(Direction.WEST)) {
            if (!directions.contains(Direction.NORTH)) {
                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.WEST, 1, minY, 1/16f, 0, maxY, 0, 0xFF212121);
                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 0, 1, maxY, 1/16f, 0xFF212121);
            }
            if (!directions.contains(Direction.SOUTH)) {
                RenderingUtil.drawQuad(poseStack, bufferSource.getBuffer(ExoRenderTypes.SOLID_NO_DEPTH), Direction.UP, 0, minY, 15/16f, 1, maxY, 1, 0xFF212121);
            }
        }

     */

    public static void drawLine(Vec3 start, Vec3 end, float alpha) {
        if (start == null || end == null) return;
        PoseStack stack = RenderSystem.getModelViewStack();

        Matrix4f matrix = stack.last().pose();
        Tesselator tes = Tesselator.getInstance();
        BufferBuilder buffer = tes.getBuilder();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(1);
        buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, (float) start.x, (float) start.y, (float) start.z).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        buffer.vertex(matrix, (float) end.x, (float) end.y, (float) end.z).color(1.0f, 1.0f, 1.0f, alpha).endVertex();
        tes.end();
    }

    public static void addVertex(VertexConsumer renderer, PoseStack stack, float x, float y, float z, float u, float v, int color) {
        renderer.vertex(stack.last().pose(), x, y, z)
                .color(color)
                .uv(u, v)
                .uv2(0, 240)
                .normal(1, 0, 0)
                .endVertex();
    }
}
