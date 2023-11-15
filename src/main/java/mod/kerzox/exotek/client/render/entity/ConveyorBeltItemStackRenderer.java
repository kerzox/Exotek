package mod.kerzox.exotek.client.render.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class ConveyorBeltItemStackRenderer extends EntityRenderer<ConveyorBeltItemStack> {

    private ItemRenderer renderer;

    public ConveyorBeltItemStackRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
        renderer = ctx.getItemRenderer();
    }

    @Override
    public ResourceLocation getTextureLocation(ConveyorBeltItemStack p_114482_) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    protected int getSkyLightLevel(ConveyorBeltItemStack p_114509_, BlockPos p_114510_) {
        return super.getSkyLightLevel(p_114509_, p_114510_);
    }

    @Override
    protected int getBlockLightLevel(ConveyorBeltItemStack p_114496_, BlockPos p_114497_) {
        return super.getBlockLightLevel(p_114496_, p_114497_);
    }

    @Override
    public boolean shouldRender(ConveyorBeltItemStack p_114491_, Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return super.shouldRender(p_114491_, p_114492_, p_114493_, p_114494_, p_114495_);
    }

    @Override
    public Vec3 getRenderOffset(ConveyorBeltItemStack p_114483_, float p_114484_) {
        return super.getRenderOffset(p_114483_, p_114484_);
    }

    @Override
    public void render(ConveyorBeltItemStack entity,
                       float p_114486_,
                       float partialTicks,
                       PoseStack poseStack,
                       MultiBufferSource bufferSource,
                       int combinedLight) {
        WrappedPose wp = WrappedPose.of(poseStack);
        ItemStack stack = entity.getTransportedStack();
        if (stack.isEmpty()) return;
        BakedModel model = renderer.getModel(stack, Minecraft.getInstance().level, null, entity.getId());

        boolean coverBlocking = false;

        if (Minecraft.getInstance().level.getBlockEntity(entity.getMarkedPos()) instanceof IConveyorBelt<?> belt) {
            for (IConveyorCover cover : belt.getBelt().getActiveCovers()) {
                if (cover.beforeItemStackRender(wp, stack, bufferSource, combinedLight, model)) {
                    coverBlocking = true;
                }
            }
        }

        if (coverBlocking) return;

        wp.push();

        wp.translate(.5f, .5f, .5f);
        wp.asStack().scale(.5f, .5f, .5f);
        wp.translate(-.5f, -.5f, -.5f);
        wp.rotateX(90);
        wp.translate(-.5f, -.5f, 1.5f);

        renderer.render(stack, ItemDisplayContext.FIXED,
                true,
                wp.asStack(), bufferSource, combinedLight, OverlayTexture.NO_OVERLAY, model);

        wp.pop();
        super.render(entity, p_114486_, partialTicks, poseStack, bufferSource, combinedLight);
    }

    @Override
    protected boolean shouldShowName(ConveyorBeltItemStack p_114504_) {
        return super.shouldShowName(p_114504_);
    }

    @Override
    public Font getFont() {
        return super.getFont();
    }

    @Override
    protected void renderNameTag(ConveyorBeltItemStack p_114498_, Component p_114499_, PoseStack p_114500_, MultiBufferSource p_114501_, int p_114502_) {
        super.renderNameTag(p_114498_, p_114499_, p_114500_, p_114501_, p_114502_);
    }
}
