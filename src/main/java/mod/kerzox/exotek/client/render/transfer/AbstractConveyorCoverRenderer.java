package mod.kerzox.exotek.client.render.transfer;

import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.royawesome.jlibnoise.module.combiner.Min;

public abstract class AbstractConveyorCoverRenderer {

    protected ResourceLocation resourceLocation;
    private Minecraft mc = Minecraft.getInstance();
    private Level level = Minecraft.getInstance().level;

    public AbstractConveyorCoverRenderer(ResourceLocation location) {
        this.resourceLocation = location;
    }

    public BakedModel getModel() {
        return Minecraft.getInstance().getModelManager().getModel(resourceLocation);
    }

    public abstract void init();

    protected void preRender(ConveyorBeltEntity entity, IConveyorCover cover, float partialTicks, WrappedPose poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
        if (getModel() != null && !getModel().equals(mc.getModelManager().getMissingModel())) render(entity, cover, partialTicks, poseStack, bufferSource, combinedLight, combinedOverlay);
    }

    protected abstract void render(ConveyorBeltEntity entity, IConveyorCover cover, float partialTicks, WrappedPose poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay);

}
