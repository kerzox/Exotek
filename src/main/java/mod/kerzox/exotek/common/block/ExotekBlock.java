package mod.kerzox.exotek.common.block;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.jetbrains.annotations.Nullable;

public class ExotekBlock extends BasicBlock {

    public ExotekBlock(Properties p_49795_) {
        super(p_49795_);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(DirectionalBlock.FACING, Direction.NORTH)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DirectionalBlock.FACING);
        super.createBlockStateDefinition(pBuilder);
    }

    @Override
    public BlockState rotate(BlockState pState, Rotation pRotation) {
        return pState.setValue(DirectionalBlock.FACING, pRotation.rotate(pState.getValue(DirectionalBlock.FACING)));
    }

    @Override
    public BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(DirectionalBlock.FACING)));
    }


    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(DirectionalBlock.FACING, pContext.getNearestLookingDirection().getOpposite());
    }

}
