package mod.kerzox.exotek.common.capability;

import net.minecraft.nbt.CompoundTag;

public interface ICapabilitySerializer {

    public CompoundTag serialize();

    public void deserialize(CompoundTag tag);

}
