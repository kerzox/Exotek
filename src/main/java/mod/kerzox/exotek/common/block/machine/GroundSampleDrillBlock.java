package mod.kerzox.exotek.common.block.machine;

import mod.kerzox.exotek.common.blockentities.machine.GroundSampleDrillEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class GroundSampleDrillBlock extends MachineEntityBlock<GroundSampleDrillEntity> {

    public GroundSampleDrillBlock(Properties properties) {
        super(ExotekRegistry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.getType(), properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
