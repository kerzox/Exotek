package mod.kerzox.exotek.common.blockentities.transport.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public interface IConveyorBeltCollision {

    void onEntityCollision(Entity entity, Level level, BlockPos pos);

}
