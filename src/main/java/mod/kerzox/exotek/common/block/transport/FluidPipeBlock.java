package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.fluid.FluidPipeEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class FluidPipeBlock extends BasicBlock implements EntityBlock {

    private CapabilityTiers tier;

    public FluidPipeBlock(CapabilityTiers tier, Properties p_49795_) {
        super(p_49795_);
        this.tier =tier;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        FluidPipeEntity be = new FluidPipeEntity(p_153215_, p_153216_);
        return be;
    }

    @Override
    public void onPlace(BlockState state, Level pLevel, BlockPos pPos, BlockState pState, boolean moving) {

        if (pLevel.getBlockEntity(pPos) instanceof FluidPipeEntity notifiedPipe) {
            notifiedPipe.setTier(tier);
            for (Direction direction : Direction.values()) {
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos.relative(direction));
                if (blockEntity != null) {
                    LazyOptional<IFluidHandler> capability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER);
                    if (capability.isPresent()) notifiedPipe.connectTo(direction, blockEntity);
                }
            }
        }

    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (pLevel.getBlockEntity(pPos) instanceof FluidPipeEntity notifiedPipe) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pFromPos);
            if (blockEntity != null) {
                BlockPos pos = pFromPos.subtract(pPos);
                Direction facing = Direction.fromDelta(pos.getX(), pos.getY(), pos.getZ());
                LazyOptional<IFluidHandler> capability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER);

                if (capability.isPresent()) notifiedPipe.connectTo(facing, blockEntity);
                if (blockEntity instanceof FluidPipeEntity fluidPipeEntity) {
                    BlockPos pos1 = pPos.subtract(pFromPos);
                    if (fluidPipeEntity.getNetwork() != null) fluidPipeEntity.getNetwork().attemptConnection(fluidPipeEntity, notifiedPipe);
                    fluidPipeEntity.connectTo(Direction.fromDelta(pos1.getX(), pos1.getY(), pos1.getZ()), notifiedPipe);
                }
            }
        }

    }

    public CapabilityTiers getTier() {
        return tier;
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        if (pLevel.getBlockEntity(pPos) instanceof FluidPipeEntity pipe) {
            pipe.getPipeConnectable().forEach((direction, conn) -> {
                if (pLevel.getBlockEntity(pPos.relative(direction)) instanceof FluidPipeEntity connectedTo) {
                    connectedTo.disconnectFrom(direction.getOpposite());
                }
            });
            if (pipe.getNetwork() != null) pipe.getNetwork().detach(pipe);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
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

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return (pLevel1, pPos, pState1, pBlockEntity) -> {
            if (!pLevel1.isClientSide && pBlockEntity instanceof FluidPipeEntity tick) {
                tick.tick();
            }
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
