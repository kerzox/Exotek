package mod.kerzox.exotek.common.capability.energy;

import mod.kerzox.exotek.common.capability.IStrictInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.energy.EnergyStorage;

import java.util.HashSet;

public class ForgeEnergyStorage extends EnergyStorage {

    public ForgeEnergyStorage(int capacity) {
        super(capacity);
    }

    public ForgeEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public ForgeEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public ForgeEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        super(capacity, maxReceive, maxExtract, energy);
    }

    public void internalAddEnergy(int amount) {
        if (getMaxEnergyStored() > energy) {
            this.energy += Math.min(capacity - energy, amount);
        }
    }

    public void read(CompoundTag tag) {
        this.energy = tag.getInt("energy");
    }

    public void internalRemoveEnergy(int amount) {
        energy -= amount;
    }

}
