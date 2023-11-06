package mod.kerzox.exotek.common.capability.energy.cable_impl;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.registry.Material;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class LevelNetwork implements ILevelNetwork, ICapabilitySerializable<CompoundTag> {

    private LazyOptional<ILevelNetwork> handler = LazyOptional.of(()-> this);
    private Map<String, AbstractLevelNetwork<?>> capabilityNetworks = new HashMap<>();
    private Level level;

    public LevelNetwork(Level level) {
        this.level = level;
        capabilityNetworks.put("energy", new LevelEnergyNetwork(level));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ExotekCapabilities.LEVEL_NETWORK_CAPABILITY.orEmpty(cap, handler);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("energy", this.capabilityNetworks.get("energy").serializeNBT());
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("energy")) this.capabilityNetworks.get("energy").deserializeNBT(nbt.getCompound("energy"));
    }
}
