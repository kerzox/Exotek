package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.core.NonNullList;

public interface IConveyorBelt<T extends ConveyorBeltEntity> {

    T getBelt();
    ConveyorBeltInventory getInventory();


}
