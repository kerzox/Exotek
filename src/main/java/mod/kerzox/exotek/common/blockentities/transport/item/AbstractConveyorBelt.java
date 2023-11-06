package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.royawesome.jlibnoise.module.modifier.Abs;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public abstract class AbstractConveyorBelt<T extends AbstractConveyorBelt<T>> extends BasicBlockEntity implements IConveyorBelt<T>, IConveyorBeltCollision {

    public static final int CONVEYOR_ITEM_SLOT = 0;
    protected HashSet<IConveyorBelt<?>> beltsInDirectionFacing = new HashSet<>();
    protected boolean stop;
    protected int count = 0;

    public AbstractConveyorBelt(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, Direction beltDirection, Level level, Vec3 itemStackVectorPos) {
        if (level.getBlockEntity(worldPosition.relative(beltDirection.getOpposite())) instanceof IConveyorBelt<?> belt) {
            if (!belt.getInventory().extractItem(CONVEYOR_ITEM_SLOT, 1, true).isEmpty()) {
                if (getInventory().conveyorBeltInsert(CONVEYOR_ITEM_SLOT, itemStack)) {
                    belt.getInventory().conveyorBeltExtract(CONVEYOR_ITEM_SLOT);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }

        return false;
    }

    public void onEntityCollision(Entity entity, Level level, BlockPos pos) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack ret = getInventory().insertItem(CONVEYOR_ITEM_SLOT, itemEntity.getItem(), false);
            if (ret.isEmpty()) {
                itemEntity.discard();
            } else {
                itemEntity.getItem().shrink(1);
            }
        }
    }

    public boolean isStopped() {
        return stop;
    }

    public void findBeltsAndReCacheThem() {
        if (level != null) {
            for (IConveyorBelt<?> belt : findAllBeltsConnected(this)) {
                belt.getBelt().cacheTraversedBelts();
            }
        }
    }

    protected void cacheTraversedBelts() {
        this.beltsInDirectionFacing = findBeltsItemsCanFollow(this);
        this.count = beltsInDirectionFacing.size();
        syncBlockEntity();
    }

    public HashSet<IConveyorBelt<?>> getBeltsInDirectionFacing() {
        if (beltsInDirectionFacing.isEmpty() && level != null) cacheTraversedBelts();
        return beltsInDirectionFacing;
    }

    public int getBeltCount() {
        if (level.isClientSide) {
            return this.count;
        }
        return getBeltsInDirectionFacing().size();
    }

    private HashSet<IConveyorBelt<?>> findAllBeltsConnected(IConveyorBelt<?> startingNode) {
        Queue<IConveyorBelt<?>> queue = new LinkedList<>();
        HashSet<IConveyorBelt<?>> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        Direction prev = startingNode.getBelt().getBeltDirection();

        while (!queue.isEmpty()) {

            IConveyorBelt<?> current = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos neighbour = current.getBelt().getBlockPos().relative(direction);
                if (level.getBlockEntity(neighbour) instanceof IConveyorBelt<?> belt) {
                    if (!visited.contains(belt)) {
                        visited.add(belt);
                        queue.add(belt);
                    }
                }
            }
        }

        return visited;
    }

    public Direction getBeltDirection() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }

    public float getSpeed() {
        return 0.8f;
    }

    private HashSet<IConveyorBelt<?>> findBeltsItemsCanFollow(IConveyorBelt<?> startingNode) {
        Queue<IConveyorBelt<?>> queue = new LinkedList<>();
        HashSet<IConveyorBelt<?>> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        Direction prev = startingNode.getBelt().getBeltDirection();

        while (!queue.isEmpty()) {

            IConveyorBelt<?> current = queue.poll();
            BlockPos neighbour = current.getBelt().getBlockPos().relative(current.getBelt().getBeltDirection());
            if (level.getBlockEntity(neighbour) instanceof IConveyorBelt<?> belt) {
                if (!visited.contains(belt)) {
                    visited.add(belt);
                    queue.add(belt);
                }
            }
        }

        return visited;
    }
}
