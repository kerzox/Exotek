package mod.kerzox.exotek.common.capability;

import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

public interface ICapabilityIO {

    IOTypes getIOSetting();
    void setIOSetting(IOTypes types);

    default CompoundTag serializeIO() {
        CompoundTag tag = new CompoundTag();
        tag.putString("io_setting", getIOSetting().getSerializedName());
        return tag;
    }

    default void deserializeIO(CompoundTag tag) {
        if (tag.contains("io_setting")) {
            setIOSetting(IOTypes.valueOf(tag.getString("io_setting").toUpperCase()));
        }
    }

}
