package mod.kerzox.exotek.common.capability.deposit;

import net.minecraft.nbt.CompoundTag;

public interface IDepositResource {

    void serialize(CompoundTag tag);
    void deserialize(CompoundTag tag);

}
