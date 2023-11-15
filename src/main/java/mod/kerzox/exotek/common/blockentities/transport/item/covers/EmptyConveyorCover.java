package mod.kerzox.exotek.common.blockentities.transport.item.covers;

import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.client.render.transfer.AbstractConveyorCoverRenderer;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.item.ConveyorBeltCoverItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public class EmptyConveyorCover extends AbstractConveyorCover {

    public EmptyConveyorCover() {
        this.item = null;
    }

    @Override
    protected void writeToData(CompoundTag tag) {

    }

    @Override
    protected void readData(CompoundTag tag) {

    }

    @Override
    public void tick(IConveyorBelt<?> belt) {

    }

    @Override
    public void onBeltUpdate(IConveyorBelt<?> beltOnTopOf, IConveyorBelt<?> causeOfUpdate, Direction directionFrom) {

    }

    @Override
    public boolean isTicking() {
        return false;
    }

    @Override
    protected AbstractConveyorCoverRenderer instance() {
        return new AbstractConveyorCoverRenderer(null) {
            @Override
            public void init() {
                // this just does nothing
            }

            @Override
            protected void render(ConveyorBeltEntity entity, IConveyorCover cover, float partialTicks, WrappedPose poseStack, MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {

            }
        };
    }

    @Override
    public IConveyorCover create(ConveyorBeltCoverItem conveyorBeltCoverItem) {
        return new EmptyConveyorCover();
    }
}
