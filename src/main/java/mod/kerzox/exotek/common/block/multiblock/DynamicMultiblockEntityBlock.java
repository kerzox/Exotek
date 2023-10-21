package mod.kerzox.exotek.common.block.multiblock;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.block.ExotekBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.DynamicMultiblockEntity;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;
import org.w3c.dom.Entity;

public class DynamicMultiblockEntityBlock<T extends BlockEntity> extends ExotekBlock implements EntityBlock {

    protected boolean usingBER = false;
    protected RegistryObject<BlockEntityType<T>> type;

    public DynamicMultiblockEntityBlock(RegistryObject<BlockEntityType<T>> type, boolean usingBER, Properties p_49795_) {
        super(p_49795_);
        this.type = type;
        this.usingBER = usingBER;
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return usingBER ? RenderShape.ENTITYBLOCK_ANIMATED : super.getRenderShape(p_60550_);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (level.getBlockEntity(pos) instanceof ICustomCollisionShape hasCustomShape) {
            return hasCustomShape.getShape() != null ? hasCustomShape.getShape() : super.getShape(state, level, pos, ctx);
        }
        return super.getShape(state, level, pos, ctx);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return this.type.get().create(pPos, pState);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        if (pLevel.getBlockEntity(pPos) instanceof DynamicMultiblockEntity entity) {
            if (entity.getMaster() != null && !pLevel.isClientSide) entity.getMaster().detach(entity);
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }


    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (FluidUtil.getFluidHandler(pPlayer.getItemInHand(pHand)).isPresent()) {
            if (!pLevel.isClientSide) {
                if (FluidUtil.interactWithFluidHandler(pPlayer, pHand, pLevel, pHit.getBlockPos(), pHit.getDirection())) {
                    return InteractionResult.SUCCESS;
                }
            }
            return InteractionResult.SUCCESS;
        }
        if (pLevel.getBlockEntity(pPos) instanceof BasicBlockEntity onClick && pHand == InteractionHand.MAIN_HAND) {
            if (onClick.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit)) {
                return InteractionResult.SUCCESS;
            }
        }
        if (pLevel.getBlockEntity(pPos) instanceof MenuProvider menu) {
            if (pLevel.isClientSide) return InteractionResult.SUCCESS;
            NetworkHooks.openScreen((ServerPlayer) pPlayer, menu, pPos);
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
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

    @Nullable
    @Override
    public <T extends BlockEntity> GameEventListener getListener(ServerLevel p_221121_, T p_221122_) {
        return EntityBlock.super.getListener(p_221121_, p_221122_);
    }

}
