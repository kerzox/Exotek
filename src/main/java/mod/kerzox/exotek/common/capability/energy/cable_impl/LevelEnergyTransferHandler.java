package mod.kerzox.exotek.common.capability.energy.cable_impl;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
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

    public int addEnergyWithReturn(int amount) {
        int energyReceived = Math.min(capacity - energy, amount);
        addEnergy(energyReceived);
        return energyReceived;
    }

    @Override
    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("energy", this.energy);
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
        if (nbt instanceof CompoundTag tag) this.energy = tag.getInt("energy");
    }

    public void removeEnergy(int amount) {
        energy -= amount;
    }

}
