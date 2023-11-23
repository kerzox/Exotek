package mod.kerzox.exotek.common.capability;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public interface CapabilityHolder<T> {

    Capability<?> getType();
    LazyOptional<T> getCapabilityHandler(Direction direction);
    void invalidate();
}
