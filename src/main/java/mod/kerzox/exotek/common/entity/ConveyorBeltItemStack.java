package mod.kerzox.exotek.common.entity;

import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;

import static net.minecraft.core.BlockPos.ZERO;

public class ConveyorBeltItemStack extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ConveyorBeltItemStack.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Direction> DATA_DIRECTION = SynchedEntityData.defineId(ConveyorBeltItemStack.class,
            EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Direction> DATA_PREV_DIRECTION = SynchedEntityData.defineId(ConveyorBeltItemStack.class,
            EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<BlockPos> DATA_CURRENT_COLLISION = SynchedEntityData.defineId(ConveyorBeltItemStack.class, EntityDataSerializers.BLOCK_POS);

    private double totalTime = 0.1f;
    private int totalSteps = 0;

    public ConveyorBeltItemStack(Level level, Direction direction,
                                 double x,
                                 double y,
                                 double z,
                                 ItemStack stack) {
        super(Registry.Entities.TRANSPORTING_ITEM.get(), level);
        setPos(x, y, z);
        setDirection(direction);
        setDeltaMovement(0, 0, 0);
        setItem(stack);
    }

    public ConveyorBeltItemStack(EntityType<ConveyorBeltItemStack> conveyorBeltItemStackEntityType, Level level) {
        super(conveyorBeltItemStackEntityType, level);
    }

    public boolean isEmpty() {
        if (getTransportedStack() == null) return true;
        if (isRemoved()) return true;
        return getTransportedStack().isEmpty();
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return super.repositionEntityAfterLoad();
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        BlockEntity be = level().getBlockEntity(getCollisionPos());
        if (be instanceof IConveyorBelt<?> belt) {
            belt.getInventory().setEntityStackAtSlot(ConveyorBeltEntity.CONVEYOR_ITEM_SLOT, this);
        }
    }


    @Override
    public void tick() {
        super.tick();
        ItemStack stack = getTransportedStack();
        if (stack.isEmpty()) kill();
        BlockPos pos = this.getCollisionPos();
        BlockEntity be = level().getBlockEntity(getCollisionPos());
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();

        double endOfBlockPixelPerfect = 16 / 16f - 2 / 16f;
        double centerOfBlockPixelPerfect = 10 / 16f - 2 / 16f;
        double startOfNextBlockPixelPerfect = endOfBlockPixelPerfect + 4 / 16f;

        if (level().getBlockEntity(getOnPos()) instanceof IConveyorBelt<?> belt) {
        } else {
            if (!level().isClientSide) {
                if (!(level().getBlockEntity(getCollisionPos()) instanceof IConveyorBelt<?> beltMovingUs)) {
                    spawnAsItemEntity();
                    return;
                }
                spawnAsItemEntity();
            }
        }

        if (be instanceof IConveyorBelt<?> belt) {

            int totalBelts = 1;
            Direction beltDirection = belt.getBelt().getBeltDirection();

            double posB = (belt.getBelt().getBeltDirection().getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX()) + totalBelts;
            double posA = (belt.getBelt().getBeltDirection().getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX());
            double displacement = (posB - posA);

            double d = (displacement) * (belt.getBelt().getSpeed() * (2/16f));

            if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
                d = d * -1;
            }
            if (beltDirection == Direction.SOUTH || beltDirection == Direction.EAST) {
                d = Math.abs(d);
            }

//            if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
//                d = (posB - posA) * (-belt.getBelt().getSpeed() * (4 / 16f)) / DepthFirstSearch(belt).size();
//            }
//            if (beltDirection == Direction.SOUTH || beltDirection == Direction.EAST) {
//                d = Math.abs((posB - posA) * (belt.getBelt().getSpeed() * (4 / 16f)) / DepthFirstSearch(belt).size());
//            }

            int blockAxisPos = (belt.getBelt().getBeltDirection().getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX());
            double entityAxisPos = (belt.getBelt().getBeltDirection().getAxis() == Direction.Axis.Z ? getZ() : getX());


            // this means we are coming from a belt that was going a different direction we want to now center ourselves
            if (!getDirectionPrev().equals(beltDirection)) {
                if (getDirectionPrev() == Direction.SOUTH) {
                    if (beltDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        if (getZ() < (pos.getZ() + centerOfBlockPixelPerfect)) {
                            move(Direction.SOUTH, d);
                        } else {
                            setDirection2(beltDirection);
                            moveTo(getX(), getY(), Vec3.atCenterOf(pos).z);
                        }
                    } else {
                        if (getZ() < (pos.getZ() + centerOfBlockPixelPerfect)) {
                            move(Direction.SOUTH, Math.abs(d));
                        } else {
                            setDirection2(beltDirection);
                            moveTo(getX(), getY(), Vec3.atCenterOf(pos).z);
                        }
                    }
                }
                else if (getDirectionPrev() == Direction.NORTH) {
                    if (beltDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        if (getZ() > (pos.getZ() + centerOfBlockPixelPerfect)) {
                            move(Direction.NORTH, -d);
                        } else {
                            setDirection2(beltDirection);
                            moveTo(getX(), getY(), Vec3.atCenterOf(pos).z);
                        }
                    } else {
                        if (getZ() > (pos.getZ() + centerOfBlockPixelPerfect)) {
                            move(Direction.NORTH, d);
                        } else {
                            setDirection2(beltDirection);
                            moveTo(getX(), getY(), getZ());
                        }
                    }
                }
                else if (getDirectionPrev() == Direction.EAST) {
                    if (beltDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        if (getX() < (pos.getX() + centerOfBlockPixelPerfect)) {
                            move(Direction.EAST, d);
                        } else {
                            setDirection2(beltDirection);
                            moveTo(Vec3.atCenterOf(pos).x, getY(), getZ());
                        }
                    } else {
                        if (getX() < (pos.getX() + centerOfBlockPixelPerfect)) {
                            move(Direction.EAST, Math.abs(d));
                        } else {
                            setDirection2(beltDirection);
                            moveTo(Vec3.atCenterOf(pos).x, getY(), getZ());
                        }
                    }
                }
                else if (getDirectionPrev() == Direction.WEST) {
                    if (beltDirection.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                        if (getX() > (pos.getX() + centerOfBlockPixelPerfect)) {
                            move(Direction.WEST, -d);
                        } else {
                            moveTo(Vec3.atCenterOf(pos).x, getY(), getZ());
                            setDirection2(beltDirection);
                        }
                    } else {
                        if (getX() > (pos.getX() + centerOfBlockPixelPerfect)) {
                            move(Direction.WEST, d);
                        } else {
                            setDirection2(beltDirection);
                            moveTo(Vec3.atCenterOf(pos).x, getY(), getZ());
                        }
                    }
                }
            }

            if (level().getBlockEntity(pos.relative(beltDirection)) instanceof IConveyorBelt<?> beltInFront) {

                ItemStack ret = ItemHandlerHelper.insertItem(beltInFront.getInventory(), this.getTransportedStack().copy(), true);

                boolean stopped = !ret.isEmpty();

                if (stopped) {
                    // we just want to move to the center of the block

                    if (!(pos.getX() <= position().x && position().x <= pos.getX() + 1 &&
                            pos.getY() <= position().y && position().y <= pos.getY() + 1 &&
                                pos.getZ() <= position().z && position().z <= pos.getZ() + 1)) {
                        moveTo(Vec3.atCenterOf(pos));
                    }

                    if (canMove(beltDirection, d, entityAxisPos, blockAxisPos, centerOfBlockPixelPerfect)) {
                        move(beltDirection, d);
                    } else { // clean up and make sure to move it exactly centered
                        moveTo(Vec3.atCenterOf(pos));
                    }

                } else {
                    // we try to go the entire conveyor belt path

                    boolean valid = false;

                    if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {

                        double test = (blockAxisPos + startOfNextBlockPixelPerfect) - 1 - (4/16f);
                        System.out.println(entityAxisPos);
                        System.out.println(test);
                        System.out.println(entityAxisPos > test);
                        if (entityAxisPos > test) {
                            move(beltDirection, d);
                            valid = true;
                        }
                    } else {
                        double test = (blockAxisPos + startOfNextBlockPixelPerfect);
                        System.out.println(entityAxisPos);
                        System.out.println(test);
                        System.out.println(entityAxisPos < test);
                        if (entityAxisPos < (blockAxisPos + startOfNextBlockPixelPerfect)) {
                            move(beltDirection, d);
                            valid = true;
                        }
                    }

                    if (!valid) {
                        // we have reached our destination now we want to move over to the next blockentity;
                        if (checkInsideBlocksModified(beltDirection)) {
                            if (beltDirection != beltInFront.getBelt().getBeltDirection()) {
//                                moveTo(beltInFront.getBelt().getBlockPos().getX() + 0.5f,
//                                        beltInFront.getBelt().getBlockPos().getY() + 0.5f,
//                                        beltInFront.getBelt().getBlockPos().getZ() + 0.5f);
                            } else {
                                move(beltDirection, d);
                            }
                        }
                    }
                }
            } else { // we didn't find an output (ie a conveyorbelt)
                if (getDirectionPrev().equals(beltDirection)) {
                    if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
                        double test = blockAxisPos + centerOfBlockPixelPerfect ;
                        if (entityAxisPos > test) {
                            move(beltDirection, d);
                        } else { // clean up and make sure to move it exactly centered
                            moveTo(Vec3.atCenterOf(pos));
                        }
                    } else {
                        if (entityAxisPos < (blockAxisPos + centerOfBlockPixelPerfect)) {
                            move(beltDirection, d);
                        } else { // clean up and make sure to move it exactly centered
                            moveTo(Vec3.atCenterOf(pos));
                        }
                    }
                }
            }


        }
    }


    private void move(Direction beltDirection, double d) {
        if (beltDirection.getAxis() == Direction.Axis.Z) this.setPos(this.getX(), this.getY(), this.getZ() + d);
        else this.setPos(this.getX() + d, this.getY(), this.getZ());
    }


    private boolean canMove(Direction beltDirection, double delta, double entityAxisPos, double blockAxisPos, double pixelPosition) {
        if (beltDirection == Direction.NORTH ||
                beltDirection == Direction.WEST) return entityAxisPos > (blockAxisPos + pixelPosition);
        else return entityAxisPos < (blockAxisPos + pixelPosition);
    }


    protected boolean checkInsideBlocksModified(Direction beltDirection) {
        AABB aabb = this.getBoundingBox();
        BlockPos blockpos = BlockPos.containing(aabb.minX + 1.0E-7D, aabb.minY + 1.0E-7D, aabb.minZ + 1.0E-7D);
        BlockPos blockpos1 = BlockPos.containing(aabb.maxX - 1.0E-7D, aabb.maxY - 1.0E-7D, aabb.maxZ - 1.0E-7D);
        if (this.level().hasChunksAt(blockpos, blockpos1)) {
            BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();

            for (int i = blockpos.getX(); i <= blockpos1.getX(); ++i) {
                for (int j = blockpos.getY(); j <= blockpos1.getY(); ++j) {
                    for (int k = blockpos.getZ(); k <= blockpos1.getZ(); ++k) {
                        blockpos$mutableblockpos.set(i, j, k);
                        BlockState blockstate = this.level().getBlockState(blockpos$mutableblockpos);

                        try {
                            if (level().getBlockEntity(blockpos$mutableblockpos) instanceof IConveyorBelt<?> belt) {
                                return belt.getBelt().onConveyorBeltItemStackCollision(this, beltDirection, level(), position());
                            } else blockstate.entityInside(this.level(), blockpos$mutableblockpos, this);
                            this.onInsideBlock(blockstate);
                        } catch (Throwable throwable) {
                            CrashReport crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                            CrashReportCategory crashreportcategory = crashreport.addCategory("Block being collided with");
                            CrashReportCategory.populateBlockDetails(crashreportcategory, this.level(), blockpos$mutableblockpos, blockstate);
                            throw new ReportedException(crashreport);
                        }
                    }
                }
            }
        }
        return true;
    }


    public BlockPos getPos() {
        return this.getOnPos(.5f);
    }

    private void spawnAsItemEntity() {
        ItemEntity entity = new ItemEntity(level(), getX(), getY(), getZ(), getTransportedStack());
        BlockPos pos = getBlockPosBelowThatAffectsMyMovement();
        this.kill();
        switch (getDirectionMoving()) {
            case SOUTH -> entity.setDeltaMovement(0, 0, 2 / 16f);
            case NORTH -> entity.setDeltaMovement(0, 0, -2 / 16f);
            case WEST -> entity.setDeltaMovement(-2 / 16f, 0, 0);
            case EAST -> entity.setDeltaMovement(2 / 16f, 0, 0);
        }
        entity.setPos(getX() + getDeltaMovement().x, getY() + getDeltaMovement().y, getZ() + getDeltaMovement().z);
        entity.setDefaultPickUpDelay();
        level().addFreshEntity(entity);
    }

    public void setDirection(Direction direction) {
        this.getEntityData().set(DATA_DIRECTION, direction);
    }

    public void setDirection2(Direction direction) {
        this.getEntityData().set(DATA_PREV_DIRECTION, direction);
    }

    public Direction getDirectionMoving() {
        return this.getEntityData().get(DATA_DIRECTION);
    }

    public void setItem(ItemStack stack) {
        this.getEntityData().set(DATA_ITEM, stack);
    }

    public ItemStack getTransportedStack() {
        return this.getEntityData().get(DATA_ITEM);
    }

    public BlockPos getCollisionPos() {
        return this.getEntityData().get(DATA_CURRENT_COLLISION);
    }

    public Direction getDirectionPrev() {
        return this.getEntityData().get(DATA_PREV_DIRECTION);
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> dataAccessor) {
        super.onSyncedDataUpdated(dataAccessor);
        if (DATA_ITEM.equals(dataAccessor)) {
            this.getTransportedStack().setEntityRepresentation(this);
        }
        if (DATA_DIRECTION.equals(dataAccessor)) {
            this.setDirection(this.entityData.get(DATA_DIRECTION));
        }
        if (DATA_PREV_DIRECTION.equals(dataAccessor)) {
            this.setDirection(this.entityData.get(DATA_PREV_DIRECTION));
        }
        if (DATA_CURRENT_COLLISION.equals(dataAccessor)) {
            this.setBlockPosCollision(this.entityData.get(DATA_CURRENT_COLLISION));
        }
    }

    public void setBlockPosCollision(BlockPos pos) {
        this.getEntityData().set(DATA_CURRENT_COLLISION, pos);
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(DATA_ITEM, ItemStack.EMPTY);
        this.getEntityData().define(DATA_DIRECTION, Direction.NORTH);
        this.getEntityData().define(DATA_CURRENT_COLLISION, new BlockPos(ZERO));
        this.getEntityData().define(DATA_PREV_DIRECTION, Direction.NORTH);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.put("item", getTransportedStack().serializeNBT());
        tag.putString("direction", getDirectionMoving().getSerializedName());
        tag.putString("direction2", getDirectionPrev().getSerializedName());
        tag.put("collisionPos", NbtUtils.writeBlockPos(getCollisionPos()));
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        setItem(ItemStack.of(tag.getCompound("item")));
        setDirection(Direction.byName(tag.getString("direction")));
        setDirection2(Direction.byName(tag.getString("direction2")));
        setBlockPosCollision(NbtUtils.readBlockPos(tag.getCompound("collisionPos")));
    }


}
