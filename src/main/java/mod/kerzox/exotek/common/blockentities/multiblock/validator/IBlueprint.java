package mod.kerzox.exotek.common.blockentities.multiblock.validator;

import mod.kerzox.exotek.common.blockentities.multiblock.validator.data.MultiblockPattern;
import net.minecraft.nbt.CompoundTag;

public interface IBlueprint {

    String getMultiblockName();
    MultiblockPattern getPattern();
    CompoundTag serialize();
    void deserialize(CompoundTag tag);

}
