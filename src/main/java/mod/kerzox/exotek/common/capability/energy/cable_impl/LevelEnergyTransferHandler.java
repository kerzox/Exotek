package mod.kerzox.exotek.common.capability.energy.cable_impl;

import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class LevelEnergyTransferHandler extends EnergyStorage {

    public LevelEnergyTransferHandler(int capacity) {
        super(capacity);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getExtractLimit() {
        return this.maxExtract;
    }

    public int getReceiveLimit() {
        return this.maxReceive;
    }

    public void addEnergy(int amount) {
        if (getMaxEnergyStored() > energy) {
            this.energy += amount;
        }
    }

    public void removeEnergy(int amount) {
        energy -= amount;
    }

}
