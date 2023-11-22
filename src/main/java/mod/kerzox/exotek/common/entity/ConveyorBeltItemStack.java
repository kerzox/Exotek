package mod.kerzox.exotek.common.entity;

import mod.kerzox.exotek.common.block.transport.IConveyorBeltBlock;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltRampEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.ConveyorBeltSplitter;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
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
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;
import org.joml.Vector3f;

import java.util.Optional;

import static net.minecraft.core.BlockPos.ZERO;

public class ConveyorBeltItemStack extends Entity {

    private static final EntityDataAccessor<ItemStack> DATA_ITEM = SynchedEntityData.defineId(ConveyorBeltItemStack.class,
            EntityDataSerializers.ITEM_STACK);
    private static final EntityDataAccessor<Direction> DATA_DIRECTION = SynchedEntityData.defineId(ConveyorBeltItemStack.class,
            EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<Direction> DATA_PREV_DIRECTION = SynchedEntityData.defineId(ConveyorBeltItemStack.class,
            EntityDataSerializers.DIRECTION);
    private static final EntityDataAccessor<BlockPos> DATA_CURRENT_COLLISION = SynchedEntityData.defineId(ConveyorBeltItemStack.class, EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Vector3f> DATA_VEC3_POS = SynchedEntityData.defineId(ConveyorBeltItemStack.class, EntityDataSerializers.VECTOR3);

    private double totalTime = 0.1f;
    private int totalSteps = 0;

    private static double endOfBlockPixelPerfect = 16 / 16f - 2 / 16f;
    private static double centerOfBlockPixelPerfect = 10 / 16f - 2 / 16f;
    private static double startOfNextBlockPixelPerfect = endOfBlockPixelPerfect + 4 / 16f;

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
        BlockEntity be = level().getBlockEntity(getMarkedPos());
        if (be instanceof IConveyorBelt<?> belt) {
            belt.getInventory().setEntityStackAtSlot(ConveyorBeltEntity.CONVEYOR_ITEM_SLOT, this);
        }
    }

    @Override
    public void onRemovedFromWorld() {
        super.onRemovedFromWorld();
    }

    /*
        This might be the worst code i've written visually and I would bet not optimal.
     */


    @Override
    public void tick() {
        super.tick();

        ItemStack stack = getTransportedStack();
        if (stack.isEmpty()) kill();
        BlockPos pos = this.getMarkedPos();
        BlockEntity be = level().getBlockEntity(getMarkedPos());
        double x = pos.getX(), y = pos.getY(), z = pos.getZ();
        Vec3 center = Vec3.atCenterOf(pos);

        // this is to help fix a desync bug
        if (!level().isClientSide) {
            this.getEntityData().set(DATA_VEC3_POS, position().toVector3f());
        }

        if (level().getBlockState(getOnPos()).getBlock() instanceof IConveyorBeltBlock conveyorBlock) {
        } else {
            if (level().isClientSide) {            // does this fix the visual bug hopefully.
                if (!getDataVec3Pos().equals(position())) {
                    setPos(getDataVec3Pos());
                    reapplyPosition();
                }

            }
        }

        if (!level().isClientSide) {
            if (!(level().getBlockEntity(getMarkedPos()) instanceof IConveyorBelt<?> beltMovingUs)) {
                spawnAsItemEntity();
                return;
            }
        }

        if (be instanceof IConveyorBelt<?> belt) {

            boolean weAreARamp = belt.getBelt() instanceof ConveyorBeltRampEntity;

//            System.out.println(position() + " : " + level().isClientSide);

            double totalBelts = 1;
            Direction beltDirection = belt.getBeltDirection();
            if (weAreARamp) {
                // our belt direction can change depending on our vertical direction.
                beltDirection = ((ConveyorBeltRampEntity) belt).getVerticalDirection() == Direction.DOWN ? beltDirection.getOpposite() : beltDirection;
            }

            double posB = (beltDirection.getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX()) + totalBelts;
            double posA = (beltDirection.getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX());
            double displacement = (posB - posA);

            // calculate delta from the displacement between positions while also including the speed * the pixel difference of the entity)
            double d = (displacement) * ((belt.getBelt().getSpeed() * (2/16f))) / totalBelts;

            // just make sure the delta is negative or positive depending on direction
            if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
                d = d * -1;
            }
            if (beltDirection == Direction.SOUTH || beltDirection == Direction.EAST) {
                d = Math.abs(d);
            }

            // block pos (the colliding block) and entity pos

            int blockAxisPos = (beltDirection.getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX());
            double entityAxisPos = (beltDirection.getAxis() == Direction.Axis.Z ? getZ() : getX());


            // this means we are coming from a belt that was going a different direction we want to now center ourselves
            checkDirectionAndMoveTowardsCenter(pos, beltDirection, d);

            boolean isSplitter = belt.getCovers().stream().anyMatch(c-> c instanceof ConveyorBeltSplitter);

            if (isSplitter) {
                travel(pos, belt, weAreARamp, beltDirection, d, blockAxisPos, entityAxisPos, null, false);
                return;
            }

            if (belt.hasBeltInFront()) {

                IConveyorBelt<?> beltInFront = belt.getNextBelt();

                if (beltInFront == null) return;

                boolean isNextBeltARamp = beltInFront.getBelt() instanceof ConveyorBeltRampEntity;

                boolean hasInputAvailable = beltInFront.getInventory() != null &&
                                ItemHandlerHelper.insertItem(beltInFront.getInventory(), this.getTransportedStack().copy(), true).isEmpty();

                boolean stopped = !hasInputAvailable || beltInFront.getBelt().isStopped();

                /*
                    This code block does two things, it either moves the entity towards the center of the block (smaller delta ie slower movement)
                    or straight up centers the entity (this is usually due to a conveyor getting full while this entity is moving into it
                 */
                if (stopped) {
                    moveToStoppedPosition(pos, d, belt, beltDirection, blockAxisPos, entityAxisPos, weAreARamp, center);
                    return;
                }

                /*
                    If we get here that means we need to continue moving towards the next conveyor belt.
                 */

                travel(pos, belt, weAreARamp, beltDirection, d, blockAxisPos, entityAxisPos, beltInFront, isNextBeltARamp);

            } else { // we didn't find an output (ie a conveyorbelt)
                if (getDirectionPrev().equals(beltDirection)) {
                    if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
                        double test = blockAxisPos + centerOfBlockPixelPerfect ;
                        if (entityAxisPos > test) {
                            if (weAreARamp) {
                                boolean goingUp = ((ConveyorBeltRampEntity) belt.getBelt()).getVerticalDirection() == Direction.UP;

                                if (goingUp) {
                                    if (getY() < pos.getY() + 1 + centerOfBlockPixelPerfect) {
                                        this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                                    }
                                } else {
                                    if (getY() > pos.getY() + centerOfBlockPixelPerfect) {
                                        this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
                                    }
                                }
                            }
                            move(beltDirection, d);
                        } else { // clean up and make sure to move it exactly centered
                            if (weAreARamp) moveTo(center.x, center.y + centerOfBlockPixelPerfect, center.z);
                            else moveTo(Vec3.atCenterOf(pos));
                        }
                    } else {
                        if (entityAxisPos < (blockAxisPos + centerOfBlockPixelPerfect)) {
                            if (weAreARamp) {
                                boolean goingUp = ((ConveyorBeltRampEntity) belt.getBelt()).getVerticalDirection() == Direction.UP;

                                if (goingUp) {
                                    if (getY() < pos.getY() + 1 + centerOfBlockPixelPerfect) {
                                        this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                                    }
                                } else {
                                    if (getY() > pos.getY() + centerOfBlockPixelPerfect) {
                                        this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
                                    }
                                }
                            }
                            move(beltDirection, d);
                        } else { // clean up and make sure to move it exactly centered
                            if (weAreARamp) moveTo(center.x, center.y + centerOfBlockPixelPerfect, center.z);
                            else moveTo(Vec3.atCenterOf(pos));
                        }
                    }
                }
            }


        }
    }

    private void travel(BlockPos pos, IConveyorBelt<?> belt, boolean weAreARamp, Direction beltDirection, double d, int blockAxisPos, double entityAxisPos, IConveyorBelt<?> beltInFront, boolean isNextBeltARamp) {
        boolean hasDestination = false;

        if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
            double test = (blockAxisPos + startOfNextBlockPixelPerfect) - 1 - (4/16f);
            if (entityAxisPos > test) {
                if (isNextBeltARamp && entityAxisPos < test + (2/16f)) {
                    boolean goingUp = ((ConveyorBeltRampEntity) beltInFront.getBelt()).getVerticalDirection() == Direction.UP;
                    if (goingUp) {
                        if (beltDirection.equals(beltInFront.getBeltDirection())) this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                    } else {
                        if (beltDirection.equals(beltInFront.getBeltDirection().getOpposite())) this.setPos(this.getX(), this.getY()
                                + (d < 0 ? d : (d * -1)), this.getZ());
                    }
                }
                move(beltDirection, d);
            } else {
                hasDestination = true;
            }
        } else {
            double bounds = (blockAxisPos + startOfNextBlockPixelPerfect);
            if (entityAxisPos < bounds) {
                if (isNextBeltARamp && entityAxisPos > (blockAxisPos + endOfBlockPixelPerfect)) {
                    boolean goingUp = ((ConveyorBeltRampEntity) beltInFront.getBelt()).getVerticalDirection() == Direction.UP;
                    if (goingUp) {
                        if (beltDirection.equals(beltInFront.getBeltDirection())) this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                    } else {
                        if (beltDirection.equals(beltInFront.getBeltDirection().getOpposite()))
                            this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
                    }
                }
                move(beltDirection, d);
            } else {
                hasDestination = true;
            }
        }

        if (!hasDestination && weAreARamp) {
            boolean goingUp = ((ConveyorBeltRampEntity) belt.getBelt()).getVerticalDirection() == Direction.UP;

            if (goingUp) {
                if (getY() < pos.getY() + 1 + 8 / 16f) {
                    this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                }
            } else {
                double y2 = getY();
                double pos5 = pos.getY() + .5f;
                if (y2 > pos5) {
                    this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
                }
            }
        }

        if (hasDestination) {
            // check if the belt passing the item to another conveyor belt affects the transfer.
            Optional<IConveyorCover> conveyorCover = belt.getBelt().getActiveCovers().stream().filter(IConveyorCover::effectsConveyorExtract).findFirst();

            // if this is present we only try to use the covers transfer
            if (conveyorCover.isPresent()) {
                if (conveyorCover.get().onConveyorExtract(this, position(), beltInFront, belt)) {
                    belt.getInventory().conveyorBeltExtract(0);
                    belt.onConveyorBeltItemStackPassed(this, position(), belt);
                }
            }
            else if (checkInsideBlocksModified(belt)) {
                if (beltInFront instanceof ConveyorBeltRampEntity) {

                } else {
//                            // just move by constant delta
//                            move(beltDirection, d);
                }
            } else {
                if (!level().isClientSide) {
                    if (belt.getInventory().getStackInSlot(0).isEmpty()) {
                        if (belt.getInventory().conveyorBeltInsert(0, this)) {
                            System.out.println("Potential fix for this bug");
                        } else {
                            System.out.println("Failed so just spawn the item?");
                            spawnAsItemEntity();
                        }
                    }
                }
            }
        }
    }

    private void doVerticalMovement(BlockPos pos, ConveyorBeltRampEntity belt, double d, double endPoint) {
        boolean goingUp = belt.getVerticalDirection() == Direction.UP;

        if (goingUp) {
            if (getY() < endPoint) {
                this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
            }
        } else {
            if (getY() > endPoint) {
                this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
            }
        }
    }


    private void setPosByOffset(double x, double y, double z) {
        this.setPos(getX() + x, getY() + y, getZ() + z);
    }

    private void offsetPosByX(double x) {
        setPosByOffset(x, 0, 0);
    }

    private void offsetPosByY(double y) {
        setPosByOffset(0, y, 0);
    }

    private void offsetPosByZ(double z) {
        setPosByOffset(0, 0, z);
    }

    // yuck
    private void checkDirectionAndMoveTowardsCenter(BlockPos pos, Direction beltDirection, double d) {
        if (!getDirectionPrev().getAxis().equals(beltDirection.getAxis())) {
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
    }

    private void moveToStoppedPosition(BlockPos pos,
                                       double d, IConveyorBelt<?> belt,
                                       Direction beltDirection,
                                       int blockAxisPos,
                                       double entityAxisPos,
                                       boolean weAreARamp, Vec3 center) {
        // we just want to move to the center of the block

        if (!weAreARamp) {
            if (!(pos.getX() <= position().x && position().x <= pos.getX() + 1 &&
                    pos.getY() <= position().y && position().y <= pos.getY() + 1 &&
                    pos.getZ() <= position().z && position().z <= pos.getZ() + 1)) {
                moveTo(Vec3.atCenterOf(pos));
            }
        }

        // create a new displacement and delta by current entity pos
        double test1 = (((beltDirection.getAxis() == Direction.Axis.Z ? pos.getZ() : pos.getX()) + 1) -
                (beltDirection.getAxis() == Direction.Axis.Z ? getZ()
                : getX()));

        double d1 = (test1) * (belt.getBelt().getSpeed() * (4 / 16f));


        if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
            d1 = d1 * -1;
        }
        if (beltDirection == Direction.SOUTH || beltDirection == Direction.EAST) {
            d1 = Math.abs(d1);
        }

        if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
            double test = blockAxisPos + centerOfBlockPixelPerfect ;
            if (entityAxisPos > test) {
                if (weAreARamp) {
                    boolean goingUp = ((ConveyorBeltRampEntity) belt.getBelt()).getVerticalDirection() == Direction.UP;

                    if (goingUp) {
                        if (getY() < pos.getY() + 1 + centerOfBlockPixelPerfect) {
                            this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                        }
                    } else {
                        if (getY() > pos.getY() + centerOfBlockPixelPerfect) {
                            this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
                        }
                    }
                }
                move(beltDirection, d);
            } else { // clean up and make sure to move it exactly centered
                if (weAreARamp) moveTo(center.x, center.y + centerOfBlockPixelPerfect, center.z);
                else moveTo(Vec3.atCenterOf(pos));
            }
        } else {
            if (entityAxisPos < (blockAxisPos + centerOfBlockPixelPerfect)) {
                if (weAreARamp) {
                    boolean goingUp = ((ConveyorBeltRampEntity) belt.getBelt()).getVerticalDirection() == Direction.UP;

                    if (goingUp) {
                        if (getY() < pos.getY() + 1 + centerOfBlockPixelPerfect) {
                            this.setPos(this.getX(), this.getY() + Math.abs(d), this.getZ());
                        }
                    } else {
                        if (getY() > pos.getY() + centerOfBlockPixelPerfect) {
                            this.setPos(this.getX(), this.getY() + (d < 0 ? d : (d * -1)), this.getZ());
                        }
                    }
                }
                move(beltDirection, d);
            } else { // clean up and make sure to move it exactly centered
                if (weAreARamp) moveTo(center.x, center.y + centerOfBlockPixelPerfect, center.z);
                else moveTo(Vec3.atCenterOf(pos));
            }
        }
    }


    private void move(Direction beltDirection, double d) {
        if (beltDirection.getAxis() == Direction.Axis.Z) this.setPos(this.getX(), this.getY(), this.getZ() + d);
        else this.setPos(this.getX() + d, this.getY(), this.getZ());
    }


    private boolean canMoveTowards(Direction beltDirection, double delta, double entityAxisPos, double blockAxisPos, double pixelPosition) {
        if (beltDirection == Direction.NORTH ||
                beltDirection == Direction.WEST) return entityAxisPos > (blockAxisPos + pixelPosition);
        else return entityAxisPos < (blockAxisPos + pixelPosition);
    }


    protected boolean checkInsideBlocksModified(IConveyorBelt<?> conveyorBelt) {
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
                            if (blockstate.getBlock() instanceof IConveyorBeltBlock beltBlock) {
                                return beltBlock.onConveyorBeltItemStackCollision(this, conveyorBelt, level(), blockpos$mutableblockpos, position());
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

    //            if (beltDirection == Direction.NORTH || beltDirection == Direction.WEST) {
//                d = (posB - posA) * (-belt.getBelt().getSpeed() * (4 / 16f)) / DepthFirstSearch(belt).size();
//            }
//            if (beltDirection == Direction.SOUTH || beltDirection == Direction.EAST) {
//                d = Math.abs((posB - posA) * (belt.getBelt().getSpeed() * (4 / 16f)) / DepthFirstSearch(belt).size());
//            }

    public BlockPos getPos() {
        return this.getOnPos(.5f);
    }

    private void spawnAsItemEntity() {
        ItemEntity entity = new ItemEntity(level(), getX(), getY(), getZ(), getTransportedStack());
        BlockPos pos = getBlockPosBelowThatAffectsMyMovement();
        this.kill();
        if (getDirectionMoving() != null) {
            switch (getDirectionMoving()) {
                case SOUTH -> entity.setDeltaMovement(0, 0, 2 / 16f);
                case NORTH -> entity.setDeltaMovement(0, 0, -2 / 16f);
                case WEST -> entity.setDeltaMovement(-2 / 16f, 0, 0);
                case EAST -> entity.setDeltaMovement(2 / 16f, 0, 0);
            }
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

    public BlockPos getMarkedPos() {
        return this.getEntityData().get(DATA_CURRENT_COLLISION);
    }

    public Direction getDirectionPrev() {
        return this.getEntityData().get(DATA_PREV_DIRECTION);
    }

    public void setVectorPos(Vector3f pos) {
        this.getEntityData().set(DATA_VEC3_POS, pos);
    }

    public Vec3 getDataVec3Pos() {
        return new Vec3(this.getEntityData().get(DATA_VEC3_POS));
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
        if (DATA_VEC3_POS.equals(dataAccessor)) {
            this.setVectorPos(this.entityData.get(DATA_VEC3_POS));
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
        this.getEntityData().define(DATA_VEC3_POS, position().toVector3f());
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        tag.put("item", getTransportedStack().serializeNBT());
        tag.putString("direction", getDirectionMoving().getSerializedName());
        tag.putString("direction2", getDirectionPrev().getSerializedName());
        tag.put("collisionPos", NbtUtils.writeBlockPos(getMarkedPos()));
        Vector3f f = position().toVector3f();
        CompoundTag tag1 = new CompoundTag();
        tag1.putFloat("x", f.x);
        tag1.putFloat("y", f.y);
        tag1.putFloat("z", f.z);
        tag.put("vectorPos", tag1);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        setItem(ItemStack.of(tag.getCompound("item")));
        setDirection(Direction.byName(tag.getString("direction")));
        setDirection2(Direction.byName(tag.getString("direction2")));
        setBlockPosCollision(NbtUtils.readBlockPos(tag.getCompound("collisionPos")));
        CompoundTag tag1 = tag.getCompound("vectorPos");
        setVectorPos(new Vector3f(tag1.getFloat("x"), tag1.getFloat("y"), tag1.getFloat("z")));
    }


}
