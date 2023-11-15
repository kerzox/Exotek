package mod.kerzox.exotek.common.blockentities.transport.item.covers;

import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.client.render.transfer.AbstractConveyorCoverRenderer;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.item.ConveyorBeltCoverItem;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Objects;

public abstract class AbstractConveyorCover implements IConveyorCover {

    protected ConveyorBeltCoverItem item = null;
    private IConveyorBelt<?> conveyorBelt;
    private Direction facing = Direction.NORTH;
    private BlockPos pos;

    public AbstractConveyorCover() {

    }

    @Override
    public AABB getCollision() {
        return new AABB(0,0,0,0,0,0);
    }

    @Override
    public boolean isValid(Direction value, IConveyorBelt<?> conveyorBelt) {
        return true;
    }

    @Override
    public ConveyorBeltCoverItem getItem() {
        return item;
    }

    public void setItem(ConveyorBeltCoverItem item) {
        this.item = item;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        CompoundTag coverData = new CompoundTag();
        writeToData(coverData);
        coverData.putString("facing", facing.getSerializedName().toLowerCase());
        tag.putString("cover_id", item != null ? Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item)).toString() : "empty");
        tag.put("coverData", coverData);
        return tag;
    }

    protected abstract void writeToData(CompoundTag tag);
    protected abstract void readData(CompoundTag tag);

    @Override
    public void deserialize(CompoundTag tag) {
        String id = tag.getString("cover_id");
        CompoundTag coverData = tag.getCompound("coverData");
        readData(coverData);

        if (!id.equals("empty") && Objects.requireNonNull(ForgeRegistries.ITEMS.getValue(new ResourceLocation(id))).asItem() instanceof ConveyorBeltCoverItem item) {
            this.item = item;
        } else {
            this.item = null;
        }

        this.facing = Direction.valueOf(coverData.getString("facing").toUpperCase());
    }


    // make sure this returns false unless you actually want to manipulate the itemstack insert on the conveyors
    @Override
    public boolean onConveyorExtract(ConveyorBeltItemStack itemStack, Vec3 itemStackVectorPos, IConveyorBelt<?> beltPassingItem, IConveyorBelt<?> ourBelt) {
        return false;
    }

    @Override
    public boolean beforeItemStackRender(WrappedPose wp, ItemStack stack, MultiBufferSource bufferSource, int combinedLight, BakedModel model) {
        return false;
    }

    @Override
    public boolean isTicking() {
        return true;
    }

    public Level getLevel() {
        return this.conveyorBelt.getBelt().getLevel();
    }

    public BlockPos getPos() {
        return this.conveyorBelt.getBelt().getBlockPos();
    }

    @Override
    public void setDirection(Direction facing) {
        this.facing = facing;
    }

    @Override
    public Direction getFacing() {
        return facing;
    }

    @Override
    public void setBelt(IConveyorBelt<?> belt) {
        this.conveyorBelt = belt;
    }

    public IConveyorBelt<?> getConveyorImpl() {
        return conveyorBelt;
    }

    protected AbstractConveyorCoverRenderer renderer;

    protected abstract AbstractConveyorCoverRenderer instance();

    @Override
    public AbstractConveyorCoverRenderer getRenderer() {
        if (renderer == null) {
            renderer = instance();
            renderer.init();
        }
        return renderer;
    }

}
