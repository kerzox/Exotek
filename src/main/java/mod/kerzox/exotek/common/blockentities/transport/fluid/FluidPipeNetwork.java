package mod.kerzox.exotek.common.blockentities.transport.fluid;

import mod.kerzox.exotek.common.blockentities.transport.GraphUtil;
import mod.kerzox.exotek.common.blockentities.transport.IPipe;
import mod.kerzox.exotek.common.blockentities.transport.PipeNetwork;
import mod.kerzox.exotek.common.capability.fluid.FluidPipeHandler;
import mod.kerzox.exotek.common.capability.fluid.PipeNetworkFluidInventory;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class FluidPipeNetwork extends PipeNetwork<IFluidHandler> {

    private Map<FluidPipeEntity, HashSet<IPipe<?>>> cachedTraversed = new HashMap<>();
    private PipeNetworkFluidInventory inventory;
    private boolean recomputeCache = false;

    public FluidPipeNetwork(IPipe<IFluidHandler> manager) {
        super(manager);
        this.inventory = new PipeNetworkFluidInventory(this, 250);
    }

    @Override
    protected void tick() {
        if (inventory != null && inventory.getFluidAmount() > 0) {

            FluidPipeEntity entity = this.inventory.getFocused();

            if (entity != null) {
                computeRouteToConsumersFrom(entity); // compute the traversal cache
                HashSet<IFluidHandler> outputs = getAllOutputs(entity, this.inventory.getFluid());
                if (!outputs.isEmpty()) {
                    int amountPerTick = this.inventory.getFluidAmount() / outputs.size();

                    for (IFluidHandler handler : outputs) {
                        handler.fill(this.inventory.drain(amountPerTick, IFluidHandler.FluidAction.EXECUTE), IFluidHandler.FluidAction.EXECUTE);
                    }
                }

            }
//
//        AtomicInteger currentResourceAmount = new AtomicInteger(Math.min(resource.getAmount(), fluidPipeEntity.getTier().getTransfer()));
//        if (this.network.getCachedTraversed().get(fluidPipeEntity) == null) return 0;
//
//        IFluidHandler smallest = this.network.getSmallestHandlerAmount(fluidPipeEntity);
//
//        if (smallest != null) {
//            return smallest.fill(new FluidStack(resource.getFluid(), currentResourceAmount.get()),
//                    action);
//        }
        }
    }

    public Map<FluidPipeEntity, HashSet<IPipe<?>>> getCachedTraversed() {
        return cachedTraversed;
    }

    public FluidPipeEntity getManager() {
        return (FluidPipeEntity) this.manager;
    }

    @Override
    protected void onAttachment() {
       if (this.manager != null && this.manager.getTier() != null && this.inventory != null) {
           this.inventory.setCapacity(this.getManager().getTier().getTransfer());
       }
    }

//    public IFluidHandler getSmallestHandlerAmount(FluidPipeEntity entity) {
//
//        for (IPipe<?> pipe : cachedTraversed.get(entity)) {
//            for (LazyOptional<?> lazyOptional : pipe.getPipeConnectable().values()) {
//                if (!lazyOptional.isPresent()) continue;
//                if (lazyOptional.resolve().isEmpty()) continue;
//                IFluidHandler cap = ((IFluidHandler) lazyOptional.resolve().get());
//                if (cap instanceof FluidPipeHandler pipeHandler) {
//                    if (pipeHandler.getEntity().getTier() == getManager().getTier()) continue;
//                }
//                for (int i = 0; i < cap.getTanks(); i++) {
//                    if (cap.getFluidInTank(i).getAmount() < min) {
//                        ret = cap;
//                        min = cap.getFluidInTank(i).getAmount();
//                    }
//                }
//            }
//        }
//
//        return
//
//    }

    public HashSet<IFluidHandler> getAllOutputs(FluidPipeEntity entity, FluidStack resource) {
        HashSet<IFluidHandler> outputs = new HashSet<>();
        int min = Integer.MAX_VALUE;
        for (IPipe<?> pipe : cachedTraversed.get(entity)) {
            for (LazyOptional<?> lazyOptional : pipe.getPipeConnectable().values()) {
                if (!lazyOptional.isPresent()) continue;
                if (lazyOptional.resolve().isEmpty()) continue;
                IFluidHandler cap = ((IFluidHandler) lazyOptional.resolve().get());
                if (cap instanceof FluidPipeHandler pipeHandler) {
                    if (pipeHandler.getEntity().getTier() == getManager().getTier()) continue;
                }
                for (int i = 0; i < cap.getTanks(); i++) {
                    if (cap.isFluidValid(i, resource) && cap.getFluidInTank(i).getAmount() != cap.getTankCapacity(i)) {
                        outputs.add(cap);
                    }
                }
            }
        }
        return outputs;
    }

    public IFluidHandler getSmallestHandlerAmount(FluidPipeEntity entity) {
        IFluidHandler ret = null;
        int min = Integer.MAX_VALUE;
        for (IPipe<?> pipe : cachedTraversed.get(entity)) {
            for (LazyOptional<?> lazyOptional : pipe.getPipeConnectable().values()) {
                if (!lazyOptional.isPresent()) continue;
                if (lazyOptional.resolve().isEmpty()) continue;
                IFluidHandler cap = ((IFluidHandler) lazyOptional.resolve().get());
                if (cap instanceof FluidPipeHandler pipeHandler) {
                    if (pipeHandler.getEntity().getTier() == getManager().getTier()) continue;
                }
                for (int i = 0; i < cap.getTanks(); i++) {
                    if (cap.getFluidInTank(i).getAmount() < min) {
                        ret = cap;
                        min = cap.getFluidInTank(i).getAmount();
                    }
                }
            }
        }
        return ret;
    }

    private int calculateTotalConsumerSizeFromCached() {
        int total = 0;
        for (HashSet<IPipe<?>> pipeList : cachedTraversed.values()) {
            for (IPipe<?> pipe : pipeList) {
                for (LazyOptional<?> lazyOptional : pipe.getPipeConnectable().values()) {
                    if (!lazyOptional.isPresent()) continue;
                    if (lazyOptional.resolve().isEmpty()) continue;
                    total += 1;
                }
            }
        }
        return total;
    }

    public void computeRouteToConsumersFrom(FluidPipeEntity fluidPipeEntity) {
        if (this.cachedTraversed.get(fluidPipeEntity) == null || recomputeCache) {
            this.cachedTraversed.clear();
            for (IPipe<IFluidHandler> pipe : getConsumers().keySet()) {
                this.cachedTraversed.computeIfAbsent(fluidPipeEntity, k -> new HashSet<>());
                if (GraphUtil.BreadthFirstSearchCanReach(fluidPipeEntity, pipe, this)) {
                    this.cachedTraversed.get(fluidPipeEntity).add(pipe);
                }
            }
            recomputeCache = false;
        }
    }

    @Override
    protected boolean isValidPipe(IPipe<?> neighbouringPipe) {
        if (neighbouringPipe instanceof FluidPipeEntity pipeEntity) {
            if(pipeEntity.getTier() != ((FluidPipeEntity) this.manager).getTier()) return false;
        }
        return neighbouringPipe.getBE().getCapability(ForgeCapabilities.FLUID_HANDLER).isPresent();
    }

    @Override
    protected void merge(IPipe<IFluidHandler> mergingPipe) {
        PipeNetwork<IFluidHandler> mergingNetwork = mergingPipe.getNetwork();
        CompoundTag tag = mergingNetwork.serialize();
        for (IPipe<IFluidHandler> pipe : mergingNetwork.getPipesInNetwork()) {
            attach(pipe);
        }
        this.deserialize(tag);
        recomputeCache = true;

    }

    @Override
    protected PipeNetwork<IFluidHandler> separateNetworks(IPipe<IFluidHandler> startingFrom) {

        PipeNetwork<IFluidHandler> separated = startingFrom.createNetwork();

        for (IPipe<?> pipe : GraphUtil.DepthFirstSearch(this, startingFrom)) {
            this.pipesInNetwork.remove(pipe);
            separated.attach((IPipe<IFluidHandler>) pipe);
            separated.onNetworkUpdate((IPipe<IFluidHandler>) pipe);
        }

        return separated;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.put("manager", NbtUtils.writeBlockPos(this.manager.getBE().getBlockPos()));
        ListTag list = new ListTag();
        this.pipesInNetwork.forEach((pipe -> {
            list.add(NbtUtils.writeBlockPos(pipe.getBE().getBlockPos()));
        }));
        tag.put("positions", list);
        return tag;
    }

    private void readPositionsFromTag(CompoundTag tag) {
        if (tag.contains("positions")) {
            ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if (getManager().getLevel().getBlockEntity(NbtUtils.readBlockPos(list.getCompound(i))) instanceof FluidPipeEntity entity) {
                    attach(entity);
                }
            }
        }
    }

    @Override
    public void deserialize(CompoundTag tag) {
        readPositionsFromTag(tag);
    }

    @Override
    public void onNetworkUpdate(IPipe<IFluidHandler> updateFrom) {
        updateFrom.getPipeConnectable().forEach((direction, iFluidHandlerLazyOptional) -> {
            // ignore already existing handlers
            iFluidHandlerLazyOptional.ifPresent(cap -> {
                getConsumers().computeIfAbsent(updateFrom, k -> new HashSet<>());
                if (cap instanceof FluidPipeHandler handler) {
                    if (handler.getEntity().getTier() != ((FluidPipeEntity)updateFrom).getTier()) {
                        getConsumers().get(updateFrom).add(iFluidHandlerLazyOptional);
                        iFluidHandlerLazyOptional.addListener(self -> this.queue.add(updateFrom));
                    }
                } else {
                    getConsumers().get(updateFrom).add(iFluidHandlerLazyOptional);
                    iFluidHandlerLazyOptional.addListener(self -> this.queue.add(updateFrom));
                }
            });
        });
    }

    public PipeNetworkFluidInventory getInventory() {
        return inventory;
    }
}
