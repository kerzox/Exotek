package mod.kerzox.exotek.common.block.multiblock;

import mod.kerzox.exotek.common.block.machine.MachineEntityBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.ManagerMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.RegistryObject;

public class MultiblockBlock<T extends BlockEntity> extends MachineEntityBlock<T> {

    public MultiblockBlock(RegistryObject<BlockEntityType<T>> type, Properties pProperties) {
        super(type, pProperties);
    }

    @Override
    public void attack(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        if (pLevel.getBlockEntity(pPos) instanceof ManagerMultiblockEntity tile) {
            if (tile.getMultiblockManager() != null) {
                if (tile.getMultiblockManager().onPlayerAttack(pLevel, pPlayer, pPos)) {
                    return;
                }
            }
        }
        super.attack(pState, pLevel, pPos, pPlayer);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        if (level.getBlockEntity(pos) instanceof MultiblockEntity entity) {
            if (entity.getMimicState() != null) return entity.getMimicState().getCloneItemStack(target, level, pos, player);
        }
        return ItemStack.EMPTY;
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
        if (pLevel.getBlockEntity(pPos) instanceof MultiblockEntity onClick && pHand == InteractionHand.MAIN_HAND) {
            if (onClick.getMultiblockManager() != null) {
                if (onClick.getMultiblockManager().onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit)) {
                    return InteractionResult.SUCCESS;
                }
            }
        }
        if (pLevel.getBlockEntity(pPos) instanceof MultiblockEntity onClick && pHand == InteractionHand.MAIN_HAND) {
            if (onClick.getMultiblockManager() != null && onClick.getMultiblockManager().getManagingBlockEntity().getSecond() != null) {
                if (!pLevel.isClientSide) NetworkHooks.openScreen((ServerPlayer) pPlayer, onClick.getMultiblockManager().getManagingBlockEntity().getSecond(), onClick.getMultiblockManager().getManagingBlockEntity().getFirst());
                return InteractionResult.SUCCESS;
            }
        }
//        if (pLevel.getBlockEntity(pPos) instanceof MenuProvider menu) {
//            if (pLevel.isClientSide) return InteractionResult.SUCCESS;
//            NetworkHooks.openScreen((ServerPlayer) pPlayer, menu, pPos);
//            return InteractionResult.SUCCESS;
//        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(BlockState pState, Level pLevel, BlockPos pPos, BlockState pOldState, boolean pIsMoving) {
        super.onPlace(pState, pLevel, pPos, pOldState, pIsMoving);
        if (pLevel.getBlockEntity(pPos) instanceof ManagerMultiblockEntity brain) {
            brain.getMultiblockManager().setManagingBlockEntity(brain);
        }
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {

        // we want to replace this block as a real block
//        if (level.getBlockEntity(pos) instanceof ManagerMultiblockEntity entity) {
//            if (entity.getMultiblockManager() != null) entity.getMultiblockManager().disassemble(level, pos);
//        }

        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        // we want to replace this block as a real block
        if (pLevel.getBlockEntity(pPos) instanceof MultiblockEntity entity) {
            if (entity instanceof ManagerMultiblockEntity manager) {
                if (!manager.getMultiblockManager().disassembling()) {
                    manager.getMultiblockManager().disassemble(pLevel, pPos);
                }
                super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
            }
            if (entity.getMultiblockManager() != null) {
                entity.getMultiblockManager().needsRefresh();
                //entity.getMultiblockManager().disassemble(pLevel, pPos);

            } else {
                pLevel.setBlockAndUpdate(pPos, entity.getMimicState());
            }



        }


        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        if (p_60556_.getBlockEntity(p_60557_) instanceof ManagerMultiblockEntity managerMultiblockEntity) {
            return managerMultiblockEntity.getShape();
        }
        return super.getShape(p_60555_, p_60556_, p_60557_, p_60558_);
    }
}
