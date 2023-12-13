package mod.kerzox.exotek.common.capability.energy.cable_impl;

import mod.kerzox.exotek.common.capability.energy.ForgeEnergyStorage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class LevelEnergyTransferHandler extends ForgeEnergyStorage {

    public LevelEnergyTransferHandler(int capacity) {
        super(capacity);
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void addEnergy(int amount) {
        if (getMaxEnergyStored() > energy) {
            this.energy += amount;
        }
    }

    public long addEnergyWithReturn(int amount) {
        long energyReceived = Math.min(capacity - energy, amount);
        addEnergy(energyReceived);
        return energyReceived;
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putLong("energy", this.energy);
        return tag;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int ret = super.receiveEnergy(maxReceive, simulate);
        onContentsChanged();
        return ret;
    }

    protected void onContentsChanged() {

    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int ret = super.extractEnergy(maxExtract, simulate);
        onContentsChanged();
        return ret;
    }

    public void consumeEnergy(int amount) {
        this.energy -= Math.min(amount, this.energy);
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if (nbt instanceof CompoundTag tag) this.energy = tag.getLong("energy");
    }

    public void removeEnergy(int amount) {
        energy -= amount;
    }

}
