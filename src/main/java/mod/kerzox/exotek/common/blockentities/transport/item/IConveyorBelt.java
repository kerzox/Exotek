package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.royawesome.jlibnoise.module.modifier.Abs;

public interface IConveyorBelt<T extends AbstractConveyorBelt> {

    T getBelt();
    ConveyorBeltInventory getInventory();
    boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, Direction beltDirection, Level level, Vec3 position);
}
