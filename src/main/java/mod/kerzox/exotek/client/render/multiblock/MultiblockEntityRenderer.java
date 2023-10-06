package mod.kerzox.exotek.client.render.multiblock;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockInvisibleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;

// ended up using a ber due to multiblock entities possible block models, being either tinted blocks or transparent blocks.

public class MultiblockEntityRenderer implements BlockEntityRenderer<MultiblockInvisibleEntity> {


    public MultiblockEntityRenderer(BlockEntityRendererProvider.Context pContext) {

    }

    @Override
    public void render(MultiblockInvisibleEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

        BlockState mimic = entity.getMimicState();

        if (mimic != null && !entity.isHidden()) {
            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, model, mimic,
                    entity.getBlockPos(),
                    poseStack, bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                    RenderType.cutout());
        }

    }
}
