package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public interface IConveyorBeltBlock {
    boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, IConveyorBelt<?> conveyorBelt, Level level, BlockPos.MutableBlockPos blockpos$mutableblockpos, Vec3 position);
}
