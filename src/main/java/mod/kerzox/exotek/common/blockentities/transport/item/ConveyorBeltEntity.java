package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class ConveyorBeltEntity extends AbstractConveyorBelt<ConveyorBeltEntity> implements IServerTickable, IClientTickable {
    public ConveyorBeltEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.CONVEYOR_BELT_ENTITY.get(), pos, state);
    }

    @Override
    public void clientTick() {

    }


    @Override
    public ConveyorBeltEntity getBelt() {
        return this;
    }




}
