package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.block.ExotekBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltRampEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBeltCollision;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ConveyorBeltRampBlock extends ConveyorBeltBlock implements EntityBlock {

    public ConveyorBeltRampBlock(Properties p_49795_) {
        super(p_49795_);
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
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        BlockState rampTop = ExotekRegistry.Blocks.CONVEYOR_BELT_RAMP_TOP_BLOCK.get().getStateForPlacement(pContext);
        if (pContext.getLevel().getBlockState(pContext.getClickedPos().above()).getBlock() instanceof AirBlock) {
            pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos().above(), rampTop);
        } else {
            return null;
        }
        return state;
    }

    public boolean hasConnection(LevelAccessor level, BlockPos pos, BlockState state) {
        if (level.getBlockState(pos.above()).getBlock() instanceof Top top) {
            return true;
        }
        return false;
    }



    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        BlockState state = super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
        if (!hasConnection(p_60544_, p_60545_, p_60541_) && !p_60544_.isClientSide()) {
            p_60544_.destroyBlock(p_60545_, true);
        }
        return state;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new ConveyorBeltRampEntity(p_153215_, p_153216_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return Block.box(0, 0, 0, 16, 24, 16);
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

    public static class Top extends ExotekBlock implements IConveyorBeltBlock, EntityBlock {

        public static final BooleanProperty CONNECTION = BooleanProperty.create("connected");

        public Top(Properties p_49795_) {
            super(p_49795_);
        }

        @Override
        public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
            super.entityInside(state, level, pos, entity);
            if (level.getBlockEntity(pos.below()) instanceof IConveyorBeltCollision belt) belt.onEntityCollision(entity, level, pos);
        }

        @Override
        public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, IConveyorBelt<?> conveyorBelt, Level level, BlockPos.MutableBlockPos blockPos, Vec3 position) {
            if (level.getBlockEntity(blockPos.below()) instanceof IConveyorBelt<?> belt) {
                return belt.onConveyorBeltItemStackCollision(itemStack, conveyorBelt, level, position);
            }
            return false;
        }

        @Override
        public RenderShape getRenderShape(BlockState p_60550_) {
            return RenderShape.INVISIBLE;
        }

        @Override
        public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
            return Block.box(0, 0, 0, 16, 8, 16);
        }

        @Override
        public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
            return Shapes.empty();
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
            pBuilder.add(CONNECTION);
            super.createBlockStateDefinition(pBuilder);
        }

        @Override
        public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
            return checkConnection(pLevel, pCurrentPos, pState);
        }

        private BlockState checkConnection(LevelAccessor pLevel, BlockPos pCurrentPos, BlockState pState) {
            if (pLevel.getBlockState(pCurrentPos.below()).getBlock() instanceof ConveyorBeltRampBlock ramp) {
                return pState.setValue(CONNECTION, true);
            }
            if (!pLevel.isClientSide()) pLevel.destroyBlock(pCurrentPos, false);
            return pState;
        }

        @Override
        public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
            BlockState state = super.getStateForPlacement(pContext);
            return checkConnection(pContext.getLevel(), pContext.getClickedPos(), state);
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
            return new ConveyorBeltRampEntity.Top(p_153215_, p_153216_);
        }
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
