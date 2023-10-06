package mod.kerzox.exotek.common.capability;

import mod.kerzox.exotek.common.blockentities.multiblock.validator.IBlueprint;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface IBlueprintCapability {

    void setBlueprint(IBlueprint blueprint);
    IBlueprint getBlueprint();

}
