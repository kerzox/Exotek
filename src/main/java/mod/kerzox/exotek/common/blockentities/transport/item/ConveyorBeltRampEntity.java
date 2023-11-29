package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.block.transport.IConveyorBeltBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;

public class ConveyorBeltRampEntity extends AbstractConveyorBelt<ConveyorBeltRampEntity> implements IServerTickable, IClientTickable {

    private HashSet<IConveyorBelt<?>> beltsInDirectionFacing = new HashSet<>();
    private Direction verticalDirection = Direction.UP;

    private int count = 0;
    private boolean stop;

    public ConveyorBeltRampEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.CONVEYOR_BELT_RAMP_ENTITY.get(), pos, state);
    }

    @Override
    public ConveyorBeltRampEntity getBelt() {
        return this;
    }

    @Override
    public IConveyorBelt<?> getNextBelt() {
        // get block in direction one above or below
        if (verticalDirection == Direction.UP) {
            if (level.getBlockEntity(worldPosition.relative(getBeltDirection()).above()) instanceof IConveyorBelt<?> belt) {
                return belt;
            }
        } else {
            if (level.getBlockEntity(worldPosition.relative(getBeltDirection().getOpposite())) instanceof IConveyorBelt<?> belt) {
                return belt;
            }
        }

        return null;
    }

    @Override
    public boolean hasBeltInFront() {
        if (verticalDirection == Direction.UP) return level.getBlockState(worldPosition.relative(getBeltDirection()).above()).getBlock() instanceof IConveyorBeltBlock;
        return level.getBlockState(worldPosition.relative(getBeltDirection().getOpposite())).getBlock() instanceof IConveyorBeltBlock;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && !pPlayer.isShiftKeyDown() && pHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.getMainHandItem().getItem() == Items.DIAMOND) {
                setVerticalDirection(verticalDirection == Direction.UP ? Direction.DOWN : Direction.UP);
                syncBlockEntity();
                return true;
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    protected void write(CompoundTag pTag) {
        super.write(pTag);
        pTag.putBoolean("ascending", this.verticalDirection == Direction.UP);
    }

    @Override
    protected void read(CompoundTag pTag) {
        super.read(pTag);
        setVerticalDirection(pTag.getBoolean("ascending") ? Direction.UP : Direction.DOWN);
    }


    public Direction getVerticalDirection() {
        return verticalDirection;
    }

    public void setVerticalDirection(Direction verticalDirection) {
        this.verticalDirection = verticalDirection;
    }

    @Override
    public void clientTick() {

    }

    @Override
    public void tick() {

    }

    public static class Top extends BasicBlockEntity implements IConveyorBelt<ConveyorBeltRampEntity> {

        private ConveyorBeltRampEntity master;

        public Top(BlockPos pos, BlockState state) {
            super(ExotekRegistry.BlockEntities.CONVEYOR_BELT_RAMP_TOP_ENTITY.get(), pos, state);
        }

        @Override
        public void onLoad() {
            super.onLoad();
            if (level.getBlockEntity(worldPosition.below()) instanceof ConveyorBeltRampEntity rampEntity) master = rampEntity;
            else level.destroyBlock(worldPosition, false);
        }

        public ConveyorBeltRampEntity getMaster() {
            if (level.getBlockEntity(worldPosition.below()) instanceof ConveyorBeltRampEntity rampEntity) return master = rampEntity;
            // just delete us
            level.destroyBlock(worldPosition, false);
            return null;
        }

        @Override
        public ConveyorBeltRampEntity getBelt() {
            return getMaster();
        }

        @Override
        public ConveyorBeltInventory getInventory() {
            if (getMaster() == null) return null;
            return getMaster().getInventory();
        }

        @Override
        public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, IConveyorBelt<?> belt, Level level, Vec3 position) {
            return getMaster().onConveyorBeltItemStackCollision(itemStack, belt, level, position);
        }

        @Override
        public void onConveyorBeltItemStackPassed(ConveyorBeltItemStack itemStack, Vec3 itemStackVectorPos, IConveyorBelt<?> belt) {

        }

        @Override
        public IConveyorBelt<?> getNextBelt() {
            return getMaster().getNextBelt();
        }

        @Override
        public boolean hasBeltInFront() {
            return getMaster().hasBeltInFront();
        }

        @Override
        public Direction getBeltDirection() {
            return getMaster().getBeltDirection();
        }

        @Override
        public IConveyorCover getCoverByClickedPosition(Vec3 clickedPos) {
            return getMaster().getCoverByClickedPosition(clickedPos);
        }

        @Override
        public List<IConveyorCover> getCovers() {
            return getMaster().getCovers();
        }

        @Override
        public boolean addCover(int index, IConveyorCover conveyorCover) {
           return this.getMaster().addCover(index, conveyorCover);
        }

    }

}
