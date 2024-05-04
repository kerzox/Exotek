package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.ManagerMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.data.MultiblockPattern;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockException;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.IBlueprint;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.*;

import static mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity.MULTIBLOCK_COMPOUND;

public abstract class AbstractMultiblockManager implements IManager {

    private String blueprintName;
    protected IBlueprint blueprint;
    protected CompoundTag data = new CompoundTag();
    protected Map<BlockPos, MultiblockEntity> entityMap = new HashMap<>();
    private Pair<BlockPos, ManagerMultiblockEntity> managerBlockEntity = new Pair<>(null, null);
    protected Direction validationDirection = Direction.NORTH;
    protected boolean refresh;

    protected boolean disassembling = false;

    private Map<BlockState, MultiblockEntity> toUpdate = new HashMap<>();
    private List<BlockPos> positions = new ArrayList<>();

    public AbstractMultiblockManager(String blueprint) {
        this.blueprintName = blueprint;
    }

    @Override
    public void build(Direction facing, Level level, Map<BlockPos, BlockState> blocks, MultiblockPattern pattern) {
        this.validationDirection = facing;
        for (Map.Entry<BlockPos, BlockState> entry : blocks.entrySet()) {
            BlockPos pos = entry.getKey();
            BlockState state = entry.getValue();

            if (state.getBlock() instanceof AirBlock) continue;

            // turn blocks to multiblock tiles
            System.out.println(pos.toShortString());
            if (!(level.getBlockEntity(pos) instanceof ManagerMultiblockEntity)) level.setBlockAndUpdate(pos, ExotekRegistry.Blocks.MULTIBLOCK_INVISIBLE_BLOCK.get().defaultBlockState());

            if (level.getBlockEntity(pos) instanceof MultiblockEntity tile) {
                if (pattern.isSpecialModel()) {
                    tile.setHiddenInWorld(true);
                }
                tile.setMimicState(state);
                tile.setMultiblockManager(this);
                tile.requestModelDataUpdate();
                CompoundTag tag = tile.getMultiblockData();
                if (tag.contains(MULTIBLOCK_COMPOUND)) {
                    System.out.println("Found tag");
                    tile.getMultiblockManager().deserialize(tag);
                }
                toUpdate.put(state, tile);
                tile.syncBlockEntity();
                entityMap.put(pos, tile);
            }
        }
    }

    @Override
    public List<BlockPos> getPositions() {
        return positions;
    }

    @Override
    public void tickManager() {
        if (refresh) {
            try {
                if (getManagingBlockEntity().getSecond() != null)
                if (getManagingBlockEntity().getSecond().getLevel().getBlockEntity(getManagingBlockEntity().getFirst()).equals(getManagingBlockEntity().getSecond())) {
                    MultiblockValidator.attemptMultiblockFormation(
                            this.getBlueprint(),
                            getManagingBlockEntity().getSecond().getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite(),
                            getManagingBlockEntity().getFirst(),
                            getManagingBlockEntity().getSecond().getBlockState(), getManagingBlockEntity().getSecond().getLevel(), null);
                }
            } catch (MultiblockException e) {
                disassembling = true;
                disassemble(getManagingBlockEntity().getSecond().getLevel(), e.getOffendingPosition());
            }
            refresh = false;
        }
        tick();
    }

    protected void tick() {

    }

    public Level getLevel() {
        if (this.getManagingBlockEntity() == null) return null;
        if (this.getManagingBlockEntity().getSecond() == null) return null;
        return this.getManagingBlockEntity().getSecond().getLevel();
    }

    public AABB getRenderingBox() {
        BlockPos[] pos = null;
        if (!getPositions().isEmpty()) {
            pos = calculateMinMax(getPositions().toArray(BlockPos[]::new));
        }
        return pos != null ? new AABB(pos[0], pos[1]) : null;
    }

    protected BlockPos[] calculateMinMax(BlockPos[] positions) {
        int minX = positions[0].getX(), minY = positions[0].getY(), minZ = positions[0].getZ(),
                maxX = positions[0].getX(), maxY = positions[0].getY(), maxZ = positions[0].getZ();

        for (BlockPos pos : positions) {
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();

        }

        BlockPos minimum = new BlockPos(minX, minY, minZ);
        BlockPos maximum = new BlockPos(maxX, maxY, maxZ);
        return new BlockPos[] {minimum, maximum};
    }

    public boolean disassembling() {
        return disassembling;
    }

    @Override
    public Direction getValidationDirection() {
        return this.validationDirection;
    }

    @Override
    public IBlueprint getBlueprint() {
        if (blueprint == null) {
            this.blueprint = MultiblockValidator.getBlueprint(blueprintName);
        }
        return blueprint;
    }

    @Override
    public void disassemble(Level level, BlockPos disassembledAt) {
        disassembling = true;
        for (Map.Entry<BlockPos, MultiblockEntity> entry : getBlocks().entrySet()) {
            BlockPos pos = entry.getKey();
            MultiblockEntity tile = entry.getValue();
            level.setBlockAndUpdate(pos, tile.getMimicState());
        }
    }

    @Override
    public void needsRefresh() {
        this.refresh = true;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        ListTag list = new ListTag();
        entityMap.forEach(((pos, multiblockTileBlockEntity) -> {
            list.add(NbtUtils.writeBlockPos(pos));
        }));
        CompoundTag blueprint = new CompoundTag();
        blueprint.put("blueprintData", getBlueprint().serialize());
        blueprint.putString("direction", this.validationDirection.getSerializedName());
        tag.put("blueprint", blueprint);
        tag.put("positions", list);
        tag.put("cache", writeToCache());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        if (tag.contains("positions")) {
            this.data.put("block_positions", tag.getList("positions", Tag.TAG_COMPOUND));
            readPositionsFromTag(tag);
        }
        if (tag.contains("data")) this.data.put("cache", tag.getCompound("cache"));
        if (tag.contains("blueprint")) this.data.put("blueprint", tag.getCompound("blueprint"));
        this.readBlueprint(tag.getCompound("blueprint"));
        this.readCache(tag.getCompound("cache"));
    }

    private void readPositionsFromTag(CompoundTag tag) {
        if (tag.contains("positions")) {
            positions.clear();
            ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                positions.add(NbtUtils.readBlockPos(list.getCompound(i)));
            }
        }
    }

    public void readBlueprint(CompoundTag tag) {
        String name = tag.getString("blueprintData");
        if (!MultiblockValidator.blueprintExists(name)) return;
        this.blueprint = MultiblockValidator.getBlueprint(name);
    }

    public CompoundTag writeToCache() {
        return new CompoundTag();
    }

    public void readCache(CompoundTag tag) {

    }

    public CompoundTag getFullManagerData() {
        return data;
    }

    public CompoundTag getCache() {
        return data.getCompound("cache");
    }

    @Override
    public Map<BlockPos, MultiblockEntity> getBlocks() {
        return entityMap;
    }

    @Override
    public void sync() {
        this.managerBlockEntity.getSecond().syncBlockEntity();
    }

    @Override
    public void setManagingBlockEntity(ManagerMultiblockEntity entity) {
        this.managerBlockEntity = Pair.of(entity.getBlockPos(), entity);
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {

    }

    @Override
    public Pair<BlockPos, ManagerMultiblockEntity> getManagingBlockEntity() {
        return this.managerBlockEntity;
    }
}
