package mod.kerzox.exotek.common.util;

import mod.kerzox.exotek.common.block.TieredMachineBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface ITieredMachine {

    public static final String TIERED_MACHINE_TAG = "tiered_state";

    default MachineTier getTier(BlockEntity blockEntity) {
        BlockState state = blockEntity.getLevel().getBlockState(blockEntity.getBlockPos());
        if (state.getBlock() instanceof TieredMachineBlock<?>) return state.getValue(TieredMachineBlock.TIER);
        else return MachineTier.DEFAULT;
    }

    void onTierChanged(MachineTier newTier);
    boolean isSorting();
    void setSorting(boolean sorting);

}
