package mod.kerzox.exotek.common.blockentities.transport;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.PipeBlock;
import net.minecraftforge.common.util.LazyOptional;

import java.util.*;

/*
Using a java graph library because fuck writing an A* graph search lmao


 */

public abstract class PipeNetwork<T> {

    protected IPipe<T> manager;
    protected HashSet<IPipe<T>> pipesInNetwork = new HashSet<>();
    protected Map<IPipe<T>, HashSet<LazyOptional<T>>> consumers = new HashMap<>();
    protected Queue<IPipe<T>> queue = new LinkedList<>();

    public PipeNetwork(IPipe<T> manager) {
        this.manager = manager;
        attach(manager);
    }

    public void tickManager() {
        handleUpdateQueue();
        tick();
    }

    protected void handleUpdateQueue() {
        while(!queue.isEmpty()) {
            IPipe<T> current = queue.poll();
            if (consumers.get(current) != null) {
                consumers.remove(current);
                onNetworkUpdate(current);
            }
        }
    }

    public IPipe<T> getManager() {
        return manager;
    }

    protected abstract void tick();

    public HashSet<IPipe<T>> getPipesInNetwork() {
        return pipesInNetwork;
    }

    /** Override this for attaching
     *
     * @param networkPipe
     * @param connectingPipe
     */
    public void attemptConnection(IPipe<T> networkPipe, IPipe<T> connectingPipe) {
       if (isValidPipe(connectingPipe)) merge(connectingPipe);
    }

    /** internal attach attemptConnection function is whats used for adding pipes
     *
     * @param pipe
     */
    public void attach(IPipe<T> pipe) {
        this.pipesInNetwork.add(pipe);
        pipe.setNetwork(this);
        onAttachment();
        onNetworkUpdate(pipe);
        pipe.getBE().syncBlockEntity();
    }

    public void detach(IPipe<T> pipe) {
        pipesInNetwork.remove(pipe);
        List<PipeNetwork<T>> managers = new ArrayList<>();
        BasicBlockEntity be = pipe.getBE();

        for (Direction inDirection : Direction.values()) {
            if (be.getLevel().getBlockEntity(be.getBlockPos().relative(inDirection)) instanceof IPipe<?> neighbouringPipe &&
                    this.pipesInNetwork.contains(neighbouringPipe) && isValidPipe(neighbouringPipe)) {
                // at this point it should be safe to cast
                managers.add(separateNetworks((IPipe<T>) neighbouringPipe));
            }
        }
    }

    public Map<IPipe<T>, HashSet<LazyOptional<T>>> getConsumers() {
        return consumers;
    }

    public int getConsumerSize() {
        int total = 0;
        for (HashSet<LazyOptional<T>> value : consumers.values()) {
            total += value.size();
        }
        return total;
    }

    protected abstract void onAttachment();
    protected abstract boolean isValidPipe(IPipe<?> neighbouringPipe);
    protected abstract void merge(IPipe<T> pipe);
    protected abstract PipeNetwork<T> separateNetworks(IPipe<T> startingFrom);
    public abstract CompoundTag serialize();
    public abstract void deserialize(CompoundTag tag);
    public abstract void onNetworkUpdate(IPipe<T> updateFrom);

}
