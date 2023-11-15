package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.block.ExotekBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltRampEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBeltCollision;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.ConveyorBeltSplitter;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ConveyorBeltBlock extends ExotekBlock implements EntityBlock, IConveyorBeltBlock {

    private static final BooleanProperty RIGHT_WALL = BooleanProperty.create("right_wall");
    private static final BooleanProperty LEFT_WALL = BooleanProperty.create("left_wall");

    public ConveyorBeltBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(RIGHT_WALL, true).setValue(LEFT_WALL, true)
        );
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(RIGHT_WALL, LEFT_WALL);
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        if (level.getBlockEntity(pos) instanceof IConveyorBeltCollision belt) belt.onEntityCollision(entity, level, pos);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pLevel.getBlockEntity(pPos) instanceof BasicBlockEntity onClick && pHand == InteractionHand.MAIN_HAND) {
            if (onClick.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit)) {
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        if (p_60516_.getBlockEntity(p_60517_) instanceof ConveyorBeltEntity conveyorBeltEntity) {
            conveyorBeltEntity.onEntityRemoved();
            conveyorBeltEntity.findBeltsAndReCacheThem();
        }
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }

    @Override
    public void onPlace(BlockState p_60566_, Level level, BlockPos pos, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, level, pos, p_60569_, p_60570_);
        BlockState state = p_60566_;
        if (level.getBlockEntity(pos) instanceof ConveyorBeltEntity conveyorBeltEntity) {
            conveyorBeltEntity.findBeltsAndReCacheThem();
            Direction facing = conveyorBeltEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
            Direction left = facing.getCounterClockWise();
            Direction right = facing.getClockWise();

            if (level.getBlockState(pos.relative(left)).getBlock() instanceof IConveyorBeltBlock beltBlock) {
                Direction otherConveyorDirection = level.getBlockState(pos.relative(left)).getValue(HorizontalDirectionalBlock.FACING);
                if (otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == left.getOpposite()) state = state.setValue(LEFT_WALL, false);
            } else {
                state = state.setValue(LEFT_WALL, true);
            }
            if (level.getBlockState(pos.relative(right)).getBlock() instanceof IConveyorBeltBlock beltBlock) {
                Direction otherConveyorDirection = level.getBlockState(pos.relative(right)).getValue(HorizontalDirectionalBlock.FACING);
                if (otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == right.getOpposite()) state = state.setValue(RIGHT_WALL, false);
            } else {
                state = state.setValue(RIGHT_WALL, true);
            }
            level.setBlockAndUpdate(pos, state);
        }
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (pLevel.getBlockEntity(pPos) instanceof IConveyorBelt<?> ourBelt && pLevel.getBlockEntity(pFromPos) instanceof IConveyorBelt<?> neighbour) {
            BlockPos pos = pFromPos.subtract(pPos);
            Direction facing = Direction.fromDelta(pos.getX(), pos.getY(), pos.getZ());
            ourBelt.getCovers().forEach(c->c.onBeltUpdate(ourBelt, neighbour, facing));
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        BlockState state = super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
        if (p_60544_.getBlockEntity(p_60545_) instanceof IConveyorBelt<?> conveyorBeltEntity) {
            Direction facing = conveyorBeltEntity.getBelt().getBlockState().getValue(HorizontalDirectionalBlock.FACING);

            Direction left = facing.getCounterClockWise();
            Direction right = facing.getClockWise();

            boolean hasSplitter = conveyorBeltEntity.getCovers().stream().anyMatch(c -> c instanceof ConveyorBeltSplitter);

            if (p_60544_.getBlockState(p_60545_.relative(left)).getBlock() instanceof IConveyorBeltBlock beltBlock) {
                Direction otherConveyorDirection = p_60544_.getBlockState(p_60545_.relative(left)).getValue(HorizontalDirectionalBlock.FACING);
                if ((otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == left.getOpposite()) || hasSplitter) state = state.setValue(LEFT_WALL, false);
            } else {
                state = state.setValue(LEFT_WALL, true);
            }
            if (p_60544_.getBlockState(p_60545_.relative(right)).getBlock() instanceof IConveyorBeltBlock beltBlock) {
                Direction otherConveyorDirection = p_60544_.getBlockState(p_60545_.relative(right)).getValue(HorizontalDirectionalBlock.FACING);
                if ((otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == right.getOpposite()) || hasSplitter) state = state.setValue(RIGHT_WALL, false);
            } else {
                state = state.setValue(RIGHT_WALL, true);
            }

        }
        return state;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ConveyorBeltEntity(p_153215_, p_153216_);
    }

    //Block.box(0, 0, 0, 16, 8, 16);
    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        double x = p_60557_.getX(), y = p_60557_.getY(), z = p_60557_.getZ();
        return Block.box(0, 0, 0, 16, 8, 16);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (pLevel1, pPos, pState1, pBlockEntity) -> {
            if (!pLevel1.isClientSide && pBlockEntity instanceof IServerTickable tick) {
                tick.tick();
            }
            if (pLevel1.isClientSide && pBlockEntity instanceof IClientTickable tick) {
                tick.clientTick();
            }
        };
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        Vec3 clicked = pContext.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
        double xAxis = clicked.x - .5;
        double zAxis = clicked.z - .5;
//
//        System.out.println(xAxis + " : " + zAxis + " : " + 0.5f + " : " + -0.5f);
//        System.out.println(clicked.x < 0.25f);
//        System.out.println(clicked.x > 0.75f);
        Direction facing = Direction.NORTH;

        // check which side is larger as that is the dominant direction axis
        if (Math.max(Math.abs(xAxis), Math.abs(zAxis)) == Math.abs(xAxis)) {
            facing =  xAxis < 0 ? Direction.WEST : Direction.EAST;
        } else {
            facing =  zAxis < 0 ? Direction.NORTH : Direction.SOUTH;
        }

        return super.getStateForPlacement(pContext).setValue(HorizontalDirectionalBlock.FACING, facing);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return EntityBlock.super.getListener(p_221121_, p_221122_);
    }

    @Override
    public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, IConveyorBelt<?> conveyorBelt, Level level, BlockPos.MutableBlockPos blockPos, Vec3 position) {
        if (level.getBlockEntity(blockPos) instanceof IConveyorBelt<?> belt) {
            return belt.onConveyorBeltItemStackCollision(itemStack, conveyorBelt, level, position);
        }
        return false;
    }

    public static class Item extends BlockItem {

        private CapabilityTiers tier;

        public Item(Block block, CapabilityTiers tier, Properties p_40566_) {
            super(block, p_40566_);
            this.tier = tier;
        }

        @Override
        protected boolean placeBlock(BlockPlaceContext p_40578_, BlockState p_40579_) {
            Level level = p_40578_.getLevel();
            BlockPos pos = p_40578_.getClickedPos();
            Vec3 clicked = p_40578_.getClickLocation().subtract(pos.getX(), pos.getY(), pos.getZ());
            double xAxis = clicked.x - .5;
            double zAxis = clicked.z - .5;

            System.out.println(xAxis + " : " + zAxis);
            Direction facing = Direction.NORTH;

            // check which side is larger as that is the dominant direction axis
            if (Math.max(Math.abs(xAxis), Math.abs(zAxis)) == Math.abs(xAxis)) {
                facing =  xAxis < 0 ? Direction.WEST : Direction.EAST;
            } else {
                facing =  zAxis < 0 ? Direction.NORTH : Direction.SOUTH;
            }

            if (level.getBlockEntity(pos.above().relative(facing)) instanceof IConveyorBelt<?> conveyorBelt) {
                if (conveyorBelt.getBelt() instanceof ConveyorBeltRampEntity) return super.placeBlock(p_40578_, p_40579_);
                BlockState state = Registry.Blocks.CONVEYOR_BELT_RAMP_BLOCK.get().getStateForPlacement(p_40578_);
                if (state != null) level.setBlockAndUpdate(pos, state);
                else return super.placeBlock(p_40578_, p_40579_);
                return false;
            }

            if (level.getBlockEntity(pos.above().relative(facing.getOpposite())) instanceof IConveyorBelt<?> conveyorBelt) {
                if (conveyorBelt.getBelt() instanceof ConveyorBeltRampEntity) return super.placeBlock(p_40578_, p_40579_);
                BlockState state = Registry.Blocks.CONVEYOR_BELT_RAMP_BLOCK.get().getStateForPlacement(p_40578_);
                if (state != null) level.setBlockAndUpdate(pos, state);
                else return super.placeBlock(p_40578_, p_40579_);
                return false;
            }

            return super.placeBlock(p_40578_, p_40579_);
        }

        @Override
        public InteractionResult useOn(UseOnContext ctx) {
            return super.useOn(ctx);
        }

        public CapabilityTiers getTier() {
            return tier;
        }
    }

}
