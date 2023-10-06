package mod.kerzox.exotek.common.capability.energy.cable_impl;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.block.transport.EnergyCableBlock;
import mod.kerzox.exotek.common.blockentities.transport.PipeTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static mod.kerzox.exotek.common.capability.ExotekCapabilities.LEVEL_NETWORK_CAPABILITY;

public class LevelEnergyNetwork implements ILevelNetwork, ICapabilitySerializable<CompoundTag> {

    private HashSet<EnergySingleNetwork> networks = new HashSet<>();
    private LazyOptional<LevelEnergyNetwork> handler = LazyOptional.of(() -> this);
    private Queue<BlockPos> updateSurrounding = new LinkedList<>();
    private Queue<EnergySingleNetwork> toRemove = new LinkedList<>();
    protected Level level;
    protected HashSet<LevelChunk> updatingNetworkClient = new HashSet<>();
    private Queue<Pair<PipeTiers, BlockPos>> updatePosition = new LinkedList<>();

    public LevelEnergyNetwork(Level level) {
        this.level = level;
    }

    public void tick() {

        if (!updateSurrounding.isEmpty()) {
            BlockPos position = updateSurrounding.poll();

            for (Direction direction : Direction.values()) {
                EnergySingleNetwork network = getNetworkFromPosition(position.relative(direction));
                if (network != null && getNetworkFromPosition(position) != network) {
                    // cause update in this network.
                    for (BlockPos pos : network.getNetwork()) {
                        createOrAttachTo(network.tier, pos, false);
                    }
                }
            }

        }

        if (!toRemove.isEmpty()) {
            EnergySingleNetwork network = toRemove.poll();
            System.out.println("Network is being removed. " + network);
            this.networks.remove(network);
        }

        for (EnergySingleNetwork network : this.networks) {
            if (network.size() == 0) markNetworkForDeletion(network);
        }

        if (!updatingNetworkClient.isEmpty()) {
            // send all clients in range an update

            updatingNetworkClient.clear();

        }

        if (!updatePosition.isEmpty()) {
            // do stuff mostly check if this blockpos is still a energy cable block or not
            Pair<PipeTiers, BlockPos> blockPosPair = updatePosition.poll();
            if (level.getBlockState(blockPosPair.getSecond()).getBlock() instanceof EnergyCableBlock cableBlock) {
                createOrAttachTo(blockPosPair.getFirst(), blockPosPair.getSecond(), true);
            }

        }

    }

    public void positionNeedsUpdating(PipeTiers tier, BlockPos pos) {
        this.updatePosition.add(Pair.of(tier, pos));
    }

    public void createOrAttachTo(PipeTiers tier, BlockPos chosenPosition, boolean updateNeighbours) {

        // Loop through all the individual networks
        for (EnergySingleNetwork individualNetwork : networks) {

            for (Direction direction : Direction.values()) {

                BlockPos neighbourPosition = chosenPosition.relative(direction);

                if (individualNetwork.isInNetwork(neighbourPosition) && individualNetwork.tier == tier) {
                    // we found a neighbouring position that is in an existing network we should connect to it.

                    // check if we already have a network because we might have to merge.

                    for (EnergySingleNetwork network : getAllNetworksFromPosition(chosenPosition)) {
                        if (network != individualNetwork) {
                            for (BlockPos blockPos : network.getNetwork()) {
                                individualNetwork.attach(blockPos);
                            }
                            markNetworkForDeletion(network);
                        }
                    }

                    individualNetwork.attach(chosenPosition);
                    if (updateNeighbours) setUpdateSurroundingFromPosition(chosenPosition);
                    updatingNetworkClient.add(level.getChunkAt(chosenPosition));
                    return;

                }

            }
        }

        // if we get here we create a network ourselves.

        createNetwork(tier, chosenPosition);


    }

    public void detach(EnergySingleNetwork modifyingNetwork, BlockPos pos) {
        modifyingNetwork.detach(pos);
        List<EnergySingleNetwork> newNetworks = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            BlockPos neighbour = pos.relative(direction);
            EnergySingleNetwork neighbourNetwork = getNetworkFromPosition(neighbour);
            if (modifyingNetwork.isInNetwork(neighbour)) {
                newNetworks.add(separateNetworks(modifyingNetwork, neighbour));
            }

        }

        this.networks.remove(modifyingNetwork);

        this.networks.addAll(newNetworks);

        for (EnergySingleNetwork newNetwork : newNetworks) {
            for (BlockPos blockPos : newNetwork.getNetwork()) {
                updatingNetworkClient.add(level.getChunkAt(blockPos));
            }
        }

      //  setUpdateSurroundingFromPosition(pos);
    }


    private EnergySingleNetwork separateNetworks(EnergySingleNetwork old, BlockPos startingFrom) {

        EnergySingleNetwork separated = EnergySingleNetwork.create(this, old.tier, startingFrom);

        for (BlockPos pos : DepthFirstSearch(startingFrom)) {
            old.detach(pos);
            separated.attach(pos);
        }

        return separated;
    }


    private void markNetworkForDeletion(EnergySingleNetwork network) {
        this.toRemove.add(network);
    }

    /**
     * Use this to add a position in the world to an update queue.
     * On the next tick all surrounding positions that aren't in the same network will be updated.
     * Can potentially cause stack overflows is only used in certain areas BE CAREFUL.
     * @param pos
     */

    private void setUpdateSurroundingFromPosition(BlockPos pos) {
        this.updateSurrounding.add(pos);
    }

    public void createNetwork(PipeTiers tier, BlockPos pos) {
        EnergySingleNetwork network = EnergySingleNetwork.create(this, tier, pos);
        this.networks.add(network);
        updatingNetworkClient.add(level.getChunkAt(pos));
    }

    public EnergySingleNetwork getNetworkFromPosition(BlockPos pos) {
        for (EnergySingleNetwork network : networks) {
            if (network.isInNetwork(pos)) {
                return network;
            }
        }
        return null;
    }

    public Set<EnergySingleNetwork> getAllNetworksFromPosition(BlockPos pos) {
        return networks.stream().filter(n -> n.isInNetwork(pos)).collect(Collectors.toSet());
    }

    public HashSet<BlockPos> DepthFirstSearch(BlockPos startingNode) {
        Queue<BlockPos> queue = new LinkedList<>();
        HashSet<BlockPos> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        while (!queue.isEmpty()) {

            BlockPos current = queue.poll();

            for (Direction direction : Direction.values()) {
                BlockPos neighbour = current.relative(direction);
                EnergySingleNetwork network = getNetworkFromPosition(neighbour);
                if (network != null && !visited.contains(neighbour) && getNetworkFromPosition(current) == network) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }

        }
        return visited;
    }

    public HashSet<EnergySingleNetwork> getNetworks() {
        return networks;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return LEVEL_NETWORK_CAPABILITY.orEmpty(cap, handler.cast());
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        networks.forEach((net -> {
            list.add(net.write());
        }));
        tag.put("networks", list);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.networks.clear();
        if (nbt.contains("networks")) {
            ListTag list = nbt.getList("networks", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                this.networks.add(EnergySingleNetwork.create(this, list.getCompound(i)));
            }
        }

//        for (EnergySingleNetwork newNetwork : networks) {
//            for (BlockPos blockPos : newNetwork.getNetwork()) {
//                updatingNetworkClient.add(level.getChunkAt(blockPos));
//            }
//        }

    }
}
