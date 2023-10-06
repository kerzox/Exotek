package mod.kerzox.exotek.common.block;

import mod.kerzox.exotek.common.blockentities.WorkstationEntity;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WorkstationBlock extends BasicEntityBlock {

    public WorkstationBlock(Properties p_49795_) {
        super(p_49795_);
    }

    

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new WorkstationEntity(p_153215_, p_153216_);
    }
}
