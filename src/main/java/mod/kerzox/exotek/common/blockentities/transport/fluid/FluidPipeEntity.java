package mod.kerzox.exotek.common.blockentities.transport.fluid;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.IPipe;
import mod.kerzox.exotek.common.blockentities.transport.PipeNetwork;
import mod.kerzox.exotek.common.blockentities.transport.PipeTiers;
import mod.kerzox.exotek.common.capability.fluid.FluidPipeHandler;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FluidPipeEntity extends BasicBlockEntity implements IPipe<IFluidHandler> {

    private PipeTiers tier;
    private FluidPipeHandler fluidTank;
    private CompoundTag tag = new CompoundTag();
    private PipeNetwork<IFluidHandler> network;

    // cached fluid inventories
    private Map<Direction, LazyOptional<IFluidHandler>> cachedInventories = resetCache();

    public FluidPipeEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.FLUID_PIPE_ENTITY.get(), pos, state);
        createNetwork();
        this.fluidTank = new FluidPipeHandler(this);
    }

    public void tick() {
        if (this.getNetwork().getManager().equals(this)) {
            this.getNetwork().tickManager();
        }
    }

    // called when network is changed
    @Override
    public void update() {
        this.network.onNetworkUpdate(this);
    }

    private void createCached() {
        for (Direction direction : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
            if (be == null) continue;
            LazyOptional<IFluidHandler> cap = be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());
            if (cap.isPresent()) {
                cachedInventories.put(direction, cap);
            }
        }
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) {
            pPlayer.sendSystemMessage(Component.literal("Pipe Tier: " + tier.getSerializedName()));
            if (getNetwork() != null) {
                pPlayer.sendSystemMessage(Component.literal("Network size: " + getNetwork().getPipesInNetwork().size()));
                pPlayer.sendSystemMessage(Component.literal("Consumers: " + getNetwork().getConsumerSize()));
                pPlayer.sendSystemMessage(Component.literal("Fluid Amount: " + getNetwork().getInventory().getFluid().getAmount()));
                pPlayer.sendSystemMessage(Component.literal("Fluid Capacity: " + getNetwork().getInventory().getCapacity()));
                pPlayer.sendSystemMessage(Component.literal("Tier: " + getNetwork().getManager().getTier()));

            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public void setTier(PipeTiers tier) {
        this.tier = tier;
        getNetwork().onAttachment();
        syncBlockEntity();
    }

    public PipeTiers getTier() {
        return tier;
    }

    private Map<Direction, LazyOptional<IFluidHandler>> resetCache() {
        return new HashMap<>() {{
            put(Direction.NORTH, LazyOptional.empty());
            put(Direction.EAST, LazyOptional.empty());
            put(Direction.SOUTH, LazyOptional.empty());
            put(Direction.WEST, LazyOptional.empty());
            put(Direction.UP, LazyOptional.empty());
            put(Direction.DOWN, LazyOptional.empty());
        }};
    }

    @Override
    protected void write(CompoundTag pTag) {
        if (this.getNetwork() != null && this.getNetwork().getManager() == this) {
            pTag.put("network", this.network.serialize());
        }
        pTag.putString("tier", this.tier.getSerializedName());
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        // we want to add the directions we are connected to for the client to render ok
        ListTag directions = new ListTag();
        getPipeConnectable().forEach((direction, iFluidHandlerLazyOptional) -> {
            CompoundTag d = new CompoundTag();
            d.putString("direction", direction.getSerializedName());
            d.putBoolean("connection", iFluidHandlerLazyOptional.isPresent());
            directions.add(d);
        });

        tag.put("pipe_connections", directions);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        // this is just for on load
        createCached();

        if (this.tag.contains("network")) {
            CompoundTag networkData = this.tag.getCompound("network");
            if (networkData.contains("manager")) {
                BlockPos pos = NbtUtils.readBlockPos(networkData.getCompound("manager"));
                if (pos.equals(this.worldPosition)) {
                    this.network.deserialize(networkData);
                    this.network.onNetworkUpdate(this);
                }
            }
        }
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.tag = pTag;
        if (pTag.contains("tier")) {
            setTier(PipeTiers.valueOf(pTag.getString("tier").toUpperCase()));
        }
    }

    public CompoundTag getConnectionTag() {
        return tag;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.fluidTank.getHandler());
    }

    @Override
    public Map<Direction, LazyOptional<IFluidHandler>> getPipeConnectable() {
        return cachedInventories;
    }

    @Override
    public void connectTo(Direction direction, BlockEntity blockEntity) {
        LazyOptional<IFluidHandler> fluidInventory = this.cachedInventories.get(direction);
        LazyOptional<IFluidHandler> connectableCapability = blockEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite());

        if (!connectableCapability.isPresent()) return;
        if (connectableCapability.resolve().isEmpty()) return;

        Optional<IFluidHandler> optionalIFluidHandler = fluidInventory.resolve();
        IFluidHandler handler = optionalIFluidHandler.orElse(null);
        this.cachedInventories.put(direction, connectableCapability);
        // add a listener to reset this directions cached item
        connectableCapability.addListener(fHandler -> {
            System.out.println("Handler removed");
            this.cachedInventories.put(direction, LazyOptional.empty());
            update();
            syncBlockEntity();
        });
        update();
        syncBlockEntity();
    }


    @Override
    public void disconnectFrom(Direction direction) {
        this.getPipeConnectable().put(direction, LazyOptional.empty());
        syncBlockEntity();
    }

    @Override
    public void setNetwork(PipeNetwork<IFluidHandler> network) {
        if (this.network != null) if (this.network.equals(network)) update();
        this.network = network;
    }



    @Override
    public FluidPipeNetwork getNetwork() {
        return (FluidPipeNetwork) this.network;
    }

    @Override
    public BasicBlockEntity getBE() {
        return this;
    }

    @Override
    public PipeNetwork<IFluidHandler> createNetwork() {
        return new FluidPipeNetwork(this);
    }


}
