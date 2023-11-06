package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.block.ExotekBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBeltCollision;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class ConveyorBeltBlock extends ExotekBlock implements EntityBlock {

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
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
        BlockState state = p_60566_;
        if (p_60567_.getBlockEntity(p_60568_) instanceof ConveyorBeltEntity conveyorBeltEntity) {
            Direction facing = conveyorBeltEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

            Direction left = facing.getCounterClockWise();
            Direction right = facing.getClockWise();

            if (p_60567_.getBlockEntity(p_60568_.relative(left)) instanceof ConveyorBeltEntity conveyorBeltEntity1) {
                Direction otherConveyorDirection = conveyorBeltEntity1.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                if (otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == left.getOpposite()) state = state.setValue(LEFT_WALL, false);
            } else {
                state = state.setValue(LEFT_WALL, true);
            }
            if (p_60567_.getBlockEntity(p_60568_.relative(right)) instanceof ConveyorBeltEntity conveyorBeltEntity1) {
                Direction otherConveyorDirection = conveyorBeltEntity1.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                if (otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == right.getOpposite()) state = state.setValue(RIGHT_WALL, false);
            } else {
                state = state.setValue(RIGHT_WALL, true);
            }
            p_60567_.setBlockAndUpdate(p_60568_, state);
        }
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        BlockState state = super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
        if (p_60544_.getBlockEntity(p_60545_) instanceof ConveyorBeltEntity conveyorBeltEntity) {
            Direction facing = conveyorBeltEntity.getBlockState().getValue(HorizontalDirectionalBlock.FACING);

            Direction left = facing.getCounterClockWise();
            Direction right = facing.getClockWise();

            if (p_60544_.getBlockEntity(p_60545_.relative(left)) instanceof ConveyorBeltEntity conveyorBeltEntity1) {
                Direction otherConveyorDirection = conveyorBeltEntity1.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                if (otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == left.getOpposite()) state = state.setValue(LEFT_WALL, false);
            } else {
                state = state.setValue(LEFT_WALL, true);
            }
            if (p_60544_.getBlockEntity(p_60545_.relative(right)) instanceof ConveyorBeltEntity conveyorBeltEntity1) {
                Direction otherConveyorDirection = conveyorBeltEntity1.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
                if (otherConveyorDirection.getAxis() != facing.getAxis() && otherConveyorDirection == right.getOpposite()) state = state.setValue(RIGHT_WALL, false);
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
        if (pContext.getPlayer() != null  && pContext.getPlayer().isShiftKeyDown()) return super.getStateForPlacement(pContext).setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection().getOpposite());
        else return super.getStateForPlacement(pContext).setValue(HorizontalDirectionalBlock.FACING, pContext.getHorizontalDirection());
    }

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return EntityBlock.super.getListener(p_221121_, p_221122_);
    }

    public static class Item extends BlockItem {

        private CapabilityTiers tier;

        public Item(Block block, CapabilityTiers tier, Properties p_40566_) {
            super(block, p_40566_);
            this.tier = tier;
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
