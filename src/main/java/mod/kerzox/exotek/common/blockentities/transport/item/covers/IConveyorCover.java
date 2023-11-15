package mod.kerzox.exotek.common.blockentities.transport.item.covers;

import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.client.render.transfer.AbstractConveyorCoverRenderer;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.item.ConveyorBeltCoverItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public interface IConveyorCover {
    void setBelt(IConveyorBelt<?> belt);
    void tick(IConveyorBelt<?> belt);
    void setDirection(Direction facing);
    void setItem(ConveyorBeltCoverItem item);
    void onBeltUpdate(IConveyorBelt<?> beltOnTopOf, IConveyorBelt<?> causeOfUpdate, Direction directionFrom);
    Direction getFacing();
    boolean isTicking();
    ConveyorBeltCoverItem getItem();
    IConveyorCover create(ConveyorBeltCoverItem conveyorBeltCoverItem);
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
    boolean isValid(Direction value, IConveyorBelt<?> conveyorBelt);
    AABB getCollision();

    /**
     * Client only function please do not call this on the server it will crash
     * @return a conveyorbelt cover renderer code
     */
    AbstractConveyorCoverRenderer getRenderer();

    default boolean effectsConveyorExtract() {
        return false;
    }

    /**
     * This is called when an itemstack collides with the next belt, so this is called from the belt that would be accepting this item
     * If this returns false the itemstack will not be transferred to the next belt it will be stuck waiting until this returns true
     * @param itemStack
     * @param itemStackVectorPos
     * @param nextBelt this can be null (mostly for the splitter)
     * @param ourBelt
     * @return whether the item can be extracted.
     */
    boolean onConveyorExtract(ConveyorBeltItemStack itemStack, Vec3 itemStackVectorPos, @Nullable IConveyorBelt<?> nextBelt, IConveyorBelt<?> ourBelt);

    /**
     * Called before the itemstack is rendered in the world.
     * @param wp
     * @param stack
     * @param bufferSource
     * @param combinedLight
     * @param model
     * @return whether the itemstack will continue to render after this is called. (false the itemstack will render as well) true cancels the entire render.
     */
    boolean beforeItemStackRender(WrappedPose wp, ItemStack stack, MultiBufferSource bufferSource, int combinedLight, BakedModel model);
}
