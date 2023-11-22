package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MultiblockInvisibleEntity extends MultiblockEntity implements IClientTickable {

    public MultiblockInvisibleEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.MULTIBLOCK_INVISIBLE_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public void clientTick() {
        if (needsUpdate) {
            this.requestModelDataUpdate();
            needsUpdate = false;
        }
    }

}
