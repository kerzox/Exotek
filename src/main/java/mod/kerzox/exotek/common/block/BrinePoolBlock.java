package mod.kerzox.exotek.common.block;

import mod.kerzox.exotek.common.block.multiblock.DynamicMultiblockEntityBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic.BrinePoolEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class BrinePoolBlock extends DynamicMultiblockEntityBlock<BrinePoolEntity> {

    public BrinePoolBlock(Properties p_49795_) {
        super(ExotekRegistry.BlockEntities.BRINE_POOL_ENTITY.getType(), true, p_49795_);
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
        p_60567_.scheduleTick(p_60568_, p_60566_.getBlock(), 1);
//        p_60567_.getBlockTicks().schedule(ScheduledTick.probe(p_60566_.getBlock(), p_60568_));
    }

    @Override
    public void tick(BlockState p_222945_, ServerLevel p_222946_, BlockPos p_222947_, RandomSource p_222948_) {
        super.tick(p_222945_, p_222946_, p_222947_, p_222948_);
        if (p_222946_.getBlockEntity(p_222947_) instanceof BrinePoolEntity entity) {
            entity.tick();
            p_222946_.scheduleTick(p_222947_, p_222945_.getBlock(), p_222946_.random.nextInt(20*45));
        }
    }
}
