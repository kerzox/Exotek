package mod.kerzox.exotek.common.capability.energy.cable_impl;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.block.transport.EnergyCableBlock;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class AbstractLevelNetwork<T extends ILevelSubNetwork> implements ICapabilitySerializable<CompoundTag> {

    protected Level level;
    private HashSet<T> networks = new HashSet<>();
    private Queue<BlockPos> updateSurrounding = new LinkedList<>();
    private Queue<T> toRemove = new LinkedList<>();
    protected HashSet<LevelChunk> updatingNetworkClient = new HashSet<>();
    private Queue<Pair<CapabilityTiers, BlockPos>> updatePosition = new LinkedList<>();


    public void tick() {
        if (!updateSurrounding.isEmpty()) {
            BlockPos position = updateSurrounding.poll();

            for (Direction direction : Direction.values()) {
                T network = getNetworkFromPosition(position.relative(direction));
                if (network != null && getNetworkFromPosition(position) != network) {
                    // cause update in this network.
                    for (LevelNode node : network.getNetwork().getNodes()) {
                        createOrAttachTo(network.getTier(), node.getWorldPosition(), false);
                    }
                }
            }
        }

        if (!updatePosition.isEmpty()) {
            Pair<CapabilityTiers, BlockPos> blockPosPair = updatePosition.poll();
            if (isValid(level.getBlockState(blockPosPair.getSecond()))) {
                createOrAttachTo(blockPosPair.getFirst(), blockPosPair.getSecond(), true);
            }

        }

        if (!toRemove.isEmpty()) {
            T network = toRemove.poll();
            this.networks.remove(network);
        }

        for (T network : this.networks) {
            if (network.size() == 0) {
                markNetworkForDeletion(network);
            }
        }


        onTick();
    }

    protected abstract void createOrAttachTo(CapabilityTiers first, BlockPos second, boolean b);

    private boolean isValid(BlockState state) {
        return true;
    }

    public abstract void onTick();


    public void markNetworkForDeletion(T network) {
        this.toRemove.add(network);
    }


    public void setUpdateSurroundingFromPosition(BlockPos pos) {
        this.updateSurrounding.add(pos);
    }

    public T getNetworkFromPosition(BlockPos pos) {
        for (T network : networks) {
            if (network.isInNetwork(pos)) {
                return network;
            }
        }
        return null;
    }

    public Set<T> getAllNetworksFromPosition(BlockPos pos) {
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
                T network = getNetworkFromPosition(neighbour);
                if (network != null && !visited.contains(neighbour) && network.getTier() == getNetworkFromPosition(current).getTier() && getNetworkFromPosition(current) == network) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }

        }
        return visited;
    }

    public void positionNeedsUpdating(CapabilityTiers tier, BlockPos pos) {
        this.updatePosition.add(Pair.of(tier, pos));
    }

    public HashSet<T> getNetworks() {
        return this.networks;
    }
}
