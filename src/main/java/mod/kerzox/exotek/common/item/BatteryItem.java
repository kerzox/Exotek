package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.Config;
import mod.kerzox.exotek.common.capability.energy.ForgeEnergyStorage;
import mod.kerzox.exotek.registry.ConfigConsts;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryItem extends ElectricalItem {

    public BatteryItem(Properties p_41383_) {
        super(p_41383_, 0, 0, 0);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilitySerializable<CompoundTag>() {

            @Override
            public CompoundTag serializeNBT() {
                CompoundTag tag = new CompoundTag();
                tag.put("energy", storage.serializeNBT());
                return tag;
            }

            @Override
            public void deserializeNBT(CompoundTag nbt) {
                storage.read(nbt);
            }

            private ForgeEnergyStorage storage = new ForgeEnergyStorage(ConfigConsts.BATTERY_CAPACITY,
                    ConfigConsts.BATTERY_EXTRACT_LIMIT, ConfigConsts.BATTERY_INSERT_LIMIT) {

                @Override
                public int getEnergyStored() {
                    // deserializeNBT(stack.getTag().getCompound(ENERGY_NBT_ITEM_TAG).get("energy"));
                    return super.getEnergyStored();
                }

                public int getEnergyStoredServer() {
                    return super.getEnergyStored();
                }

                @Override
                public Tag serializeNBT() {
                    return IntTag.valueOf(this.getEnergyStoredServer());
                }

                @Override
                public void deserializeNBT(Tag nbt) {
                    if (nbt instanceof IntTag) super.deserializeNBT(nbt);
                }

                @Override
                protected void onContentsChanged() {
                    CompoundTag energy = new CompoundTag();
                    energy.put("energy", serializeNBT());
                    stack.getOrCreateTag().put(ENERGY_NBT_ITEM_TAG, energy);
                }
            };
            private LazyOptional<ForgeEnergyStorage> handler = LazyOptional.of(() -> storage);

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.ENERGY.orEmpty(cap, handler.cast());
            }

        };
    }

}
