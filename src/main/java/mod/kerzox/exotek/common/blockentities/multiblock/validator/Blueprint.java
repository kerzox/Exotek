package mod.kerzox.exotek.common.blockentities.multiblock.validator;

import mod.kerzox.exotek.common.blockentities.multiblock.data.MultiblockPattern;
import mod.kerzox.exotek.common.capability.IBlueprintCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class Blueprint implements IBlueprint {

    public static final Capability<IBlueprintCapability> BLUEPRINT_CAPABILITY = get(new CapabilityToken<>(){});

    private MultiblockPattern pattern;

    public Blueprint(MultiblockPattern pattern) {
        this.pattern = pattern;
    }


    @Override
    public String getMultiblockName() {
        return pattern.getName();
    }

    @Override
    public MultiblockPattern getPattern() {
        return pattern;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        tag.putString("name", this.getMultiblockName());

        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {

    }
}
