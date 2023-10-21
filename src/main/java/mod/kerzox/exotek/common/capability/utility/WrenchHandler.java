package mod.kerzox.exotek.common.capability.utility;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WrenchHandler implements ICycleItem, ICapabilitySerializable<CompoundTag> {

    public enum Modes {
        ITEM,
        FLUID,
        ENERGY,
        GAS,
        WRENCH,
        DEBUG
    }

    private Modes current = Modes.WRENCH;
    private int index = 0;
    private LazyOptional<ICycleItem> handler = LazyOptional.of(() -> this);

    public WrenchHandler() {

    }

    public Modes getMode() {
        return current;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ExotekCapabilities.CYCLE_ITEM_CAPABILITY.orEmpty(cap, handler);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("index", index);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.index = nbt.getInt("index");
        if (index < 0) {
            index = Modes.values().length - 1;
        }
        if (index >  Modes.values().length - 1) {
            index = 0;
        }
        current = Modes.values()[this.index];
    }

    @Override
    public void cycle(boolean reverse) {
        if(reverse) {
            index--;
        } else {
            index++;
        }
        if (index < 0) {
            index = Modes.values().length - 1;
        }
        if (index >  Modes.values().length - 1) {
            index = 0;
        }
        this.current = Modes.values()[index];
    }
}
