package mod.kerzox.exotek.common.block.multiblock;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.DynamicMultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class DynamicMultiblockBlock extends BasicBlock {

    public DynamicMultiblockBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {

        if (pLevel.getBlockEntity(pPos) instanceof DynamicMultiblockEntity entity) {
            if (entity.getMaster() != null) entity.getMaster().detach(entity);
        }

        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

}
