package mod.kerzox.exotek.common.capability.energy.cable_impl;

import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static mod.kerzox.exotek.common.capability.ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY;

public class LevelEnergyNetwork extends AbstractLevelNetwork<EnergySubNetwork> implements IEnergyCapabilityLevelNetwork {

    private LazyOptional<LevelEnergyNetwork> handler = LazyOptional.of(() -> this);

    public LevelEnergyNetwork(Level level) {
        this.level = level;
    }

    @Override
    public void onTick() {
        if (!updatingNetworkClient.isEmpty()) {
            if (level.getServer().getPlayerCount() > 0) {
                PacketHandler.sendToAllClients(new LevelNetworkPacket(serializeNBT()));
                updatingNetworkClient.clear();
            }

        }
    }

    public void createOrAttachTo(CapabilityTiers tier, BlockPos chosenPosition, boolean updateNeighbours) {

        // Loop through all the individual networks
        for (EnergySubNetwork individualNetwork : getNetworks()) {

            for (Direction direction : Direction.values()) {

                BlockPos neighbourPosition = chosenPosition.relative(direction);

                if (individualNetwork.isInNetwork(neighbourPosition) && individualNetwork.tier == tier) {
                    // we found a neighbouring position that is in an existing network we should connect to it.

                    // check if we already have a network because we might have to merge.

                    for (EnergySubNetwork network : getAllNetworksFromPosition(chosenPosition)) {
                        if (network != individualNetwork) {
                            // get the energy stored in the network we are going to delete
                            int energyAmount = network.getInternalStorage().getEnergyStored();

                            for (LevelNode node : network.getNetwork().getNodes()) {
                                individualNetwork.attach(node);
                            }


                            int amount = Math.min(energyAmount, individualNetwork.getInternalStorage().getMaxEnergyStored());
                            network.getInternalStorage().removeEnergy(amount);
                            individualNetwork.getInternalStorage().addEnergy(amount);

                            markNetworkForDeletion(network);
                        }
                    }

                    individualNetwork.attach(chosenPosition);
                    if (updateNeighbours) setUpdateSurroundingFromPosition(chosenPosition);
                  //  updatingNetworkClient.add(level.getChunkAt(chosenPosition));
                    return;

                }

            }
        }

        // if we get here we create a network ourselves.

        if (this.getNetworkFromPosition(chosenPosition) == null) {
            createNetwork(tier, chosenPosition);
        }


    }

    public void detach(EnergySubNetwork modifyingNetwork, BlockPos pos) {
        modifyingNetwork.detach(pos);
        List<EnergySubNetwork> newNetworks = new ArrayList<>();

        int energyAmount = modifyingNetwork.getInternalStorage().getEnergyStored();

        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.relative(direction);
            EnergySubNetwork neighbourNetwork = getNetworkFromPosition(neighbour);
            if (modifyingNetwork.isInNetwork(neighbour)) {
                newNetworks.add(separateNetworks(modifyingNetwork, neighbour));
            }

        }

        getNetworks().remove(modifyingNetwork);

        for (EnergySubNetwork newNetwork : newNetworks) {
            //int received = Math.min(energyAmount / newNetworks.size(), newNetwork.getInternalStorage().getMaxEnergyStored());
            int received = newNetwork.getInternalStorage().addEnergyWithReturn(energyAmount);
            energyAmount -= received;
        }

        getNetworks().addAll(newNetworks);


        for (EnergySubNetwork newNetwork : newNetworks) {
            for (LevelNode node : newNetwork.getNetwork().getNodes()) {
             //   updatingNetworkClient.add(level.getChunkAt(node.getWorldPosition()));
            }
        }

      //  setUpdateSurroundingFromPosition(pos);
    }


    public EnergySubNetwork separateNetworks(EnergySubNetwork old, BlockPos startingFrom) {

        EnergySubNetwork separated = EnergySubNetwork.create(this, old.tier);

        for (BlockPos pos : DepthFirstSearch(startingFrom)) {
            // get tag data from this position
            CompoundTag tag = old.getNodeByPosition(pos).serialize();

            old.detach(pos);

            separated.attach(new LevelNode(tag));
        }

        return separated;
    }


    public void createNetwork(CapabilityTiers tier, BlockPos pos) {
        EnergySubNetwork network = EnergySubNetwork.create(this, tier, pos);
        getNetworks().add(network);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ENERGY_LEVEL_NETWORK_CAPABILITY.orEmpty(cap, handler.cast());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        getNetworks().forEach((net -> {
            list.add(net.write());
        }));
        tag.put("networks", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        getNetworks().clear();
        if (nbt.contains("networks")) {
            ListTag list = nbt.getList("networks", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                CompoundTag tag = list.getCompound(i);
                this.getNetworks().add(EnergySubNetwork.create(this, tag));
            }
        }
    }

    public void updateClients(BlockPos chosenPosition) {
        this.updatingNetworkClient.add(level.getChunkAt(chosenPosition));
    }
}
