package mod.kerzox.exotek.common.capability.energy;

import mod.kerzox.exotek.common.capability.IStrictInventory;
import net.minecraft.core.Direction;
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

    protected void internalAddEnergy(int amount) {
        if (getMaxEnergyStored() > energy) {
            this.energy += amount;
        }
    }

    protected void internalRemoveEnergy(int amount) {
        energy -= amount;
    }

}
