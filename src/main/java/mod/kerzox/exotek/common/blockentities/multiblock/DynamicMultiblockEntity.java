package mod.kerzox.exotek.common.blockentities.multiblock;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.util.NBTExotekUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This is for structures like fluid tanks that can be placed together to form a larger homogeneous struct
 */

public abstract class DynamicMultiblockEntity extends BasicBlockEntity {

    public DynamicMultiblockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        for (DynamicMultiblockEntity entity : getNeighbouringEntities().values()) {
            getMaster().attach(entity);
        }
    }

    public Map<Direction, DynamicMultiblockEntity> getNeighbouringEntities() {
        Map<Direction, DynamicMultiblockEntity> dynamicMultiblockEntityMap = new HashMap<>();
        if (getMaster() != null) {
            for (Direction direction : Direction.values()) {
                if (level.getBlockEntity(worldPosition.relative(direction)) instanceof DynamicMultiblockEntity entity) {
                    if (getMaster().isValidEntity(entity)) dynamicMultiblockEntityMap.put(direction, entity);
                }
            }
        }
        return dynamicMultiblockEntityMap;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("manager", getMaster().write());
    }

    @Override
    protected void read(CompoundTag pTag) {
        getMaster().read(pTag.getCompound("manager"));
    }

    public abstract void setMaster(Master master);

    public abstract Master getMaster();

    public abstract Master createMaster();

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (getMaster() != null) return getMaster().getCapability(cap, side);
        return super.getCapability(cap, side);
    }

    public abstract static class Master implements ICapabilityProvider {

        protected DynamicMultiblockEntity master;
        protected HashSet<BlockPos> blockPositions = new HashSet<>();
        protected HashSet<DynamicMultiblockEntity> entities = new HashSet<>();
        protected boolean refreshEntityList = false;

        public Master(DynamicMultiblockEntity master) {
            this.master = master;
            this.blockPositions.add(master.worldPosition);
            master.setMaster(this);
        }

        /**
         * Add extra conditions to stop attachment to a entity
         * Use this for any capability data being transferred / merged as it is safe it you return true.
         * @param toAttach
         * @return boolean
         */

        protected abstract boolean preAttachment(DynamicMultiblockEntity toAttach);

        protected void onAttachment(DynamicMultiblockEntity attached) {

        }

        protected boolean preDetachment(DynamicMultiblockEntity toDetach) {
            return true;
        }

        /**
         * Be careful as detaching entity is possibly being removed from the level and the manager will probably be removed aswell.
         * This is really only for select few situations
         * @param detached
         * @param masters
         */

        protected void onDetachment(DynamicMultiblockEntity detached, HashSet<Master> masters) {

        }

        private void merge(DynamicMultiblockEntity toMerge) {

            for (DynamicMultiblockEntity entity : toMerge.getMaster().getEntities()) {
                if (!preAttachment(entity)) continue;
                this.getEntities().add(entity);
                entity.setMaster(this);
                onAttachment(entity);
            }

        }

        public void attach(DynamicMultiblockEntity toAttach) {

            refreshEntityList = true;

            if (toAttach.getMaster() != null && toAttach.getMaster() != this) {
                merge(toAttach);
                return;
            }

            if (!preAttachment(toAttach)) return;
            this.getEntities().add(toAttach);
            toAttach.setMaster(this);
            onAttachment(toAttach);

        }

        public void detach(DynamicMultiblockEntity toDetach) {

            refreshEntityList = true;

            if (!preDetachment(toDetach)) return;
            this.entities.remove(toDetach);
            this.blockPositions.remove(toDetach.worldPosition);
            HashSet<Master> masters = new HashSet<>();

            for (DynamicMultiblockEntity neighbour : toDetach.getNeighbouringEntities().values()) {
                masters.add(separateNetworks(neighbour));
            }

            onDetachment(toDetach, masters);

        }

        public Master separateNetworks(DynamicMultiblockEntity startingFrom) {

            Master manager = startingFrom.createMaster();

            for (DynamicMultiblockEntity entity : traverse(startingFrom)) {
                entity.setMaster(manager);
                manager.entities.add(entity);
                manager.onAttachment(entity);
                entity.setChanged();
            }

            return manager;
        }

        public HashSet<DynamicMultiblockEntity> traverse(DynamicMultiblockEntity startingNode) {
            Queue<DynamicMultiblockEntity> queue = new LinkedList<>();
            HashSet<DynamicMultiblockEntity> visited = new HashSet<>();

            queue.add(startingNode);
            visited.add(startingNode);

            while (!queue.isEmpty()) {

                DynamicMultiblockEntity current = queue.poll();

                for (DynamicMultiblockEntity neighbour : current.getNeighbouringEntities().values()) {
                    if (!visited.contains(neighbour) && getEntities().contains(neighbour)) {
                        visited.add(neighbour);
                        queue.add(neighbour);
                    }
                }

            }
            return visited;
        }

        public void markNetworkForDisk() {
            for (DynamicMultiblockEntity entity : getEntities()) {
                entity.setChanged();
            }
        }

        protected boolean isValidEntity(DynamicMultiblockEntity entity) {
            return true;
        }

        public DynamicMultiblockEntity getTile() {
            return master;
        }

        public HashSet<BlockPos> getBlockPositions() {
            return blockPositions;
        }

        public CompoundTag write() {
            CompoundTag pTag = new CompoundTag();
            NBTExotekUtils.saveBlockPositions(pTag, getEntities().stream().map(BlockEntity::getBlockPos).collect(Collectors.toList()));
            return pTag;
        }

        public void read(CompoundTag pTag) {
            blockPositions.clear();
            blockPositions.addAll(NBTExotekUtils.getBlockPositions(pTag));
            refreshEntityList = true;
        }

        public HashSet<DynamicMultiblockEntity> getEntities() {

            Level level = getTile().getLevel();

            if (level != null && (entities.isEmpty() || refreshEntityList || level.isClientSide) && !blockPositions.isEmpty()) {
                if (level.isClientSide) entities.clear();
                refreshEntityList = false;
                for (BlockPos position : blockPositions) {
                    if (level.getBlockEntity(position) instanceof DynamicMultiblockEntity dynEntity) {
                        if (isValidEntity(dynEntity)) entities.add(dynEntity);
                    }
                }
            }

            return entities;
        }

        public abstract void tick();
    }

}
