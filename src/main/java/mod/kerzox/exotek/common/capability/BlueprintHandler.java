package mod.kerzox.exotek.common.capability;

import mod.kerzox.exotek.common.blockentities.multiblock.data.BlockPredicate;
import mod.kerzox.exotek.common.blockentities.multiblock.data.MultiblockPattern;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.IBlueprint;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator;
import mod.kerzox.exotek.common.datagen.MultiblockPatternGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlueprintHandler implements IBlueprintCapability, ICapabilitySerializable<CompoundTag> {

    private LazyOptional<IBlueprintCapability> handler = LazyOptional.of(() -> this);
    private CompoundTag blueprintTag = new CompoundTag();
    private IBlueprint blueprint;
    private Direction facing;
    private BlockPos pos;
    private int index;

    private BlockPos[] positions;

    private boolean render;

    public BlueprintHandler() {

    }

    public Direction getFacing() {
        return facing;
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public void setRender(boolean render) {
        this.render = render;
    }

    public boolean isRendering() {
        return render;
    }

    @Override
    public void setBlueprint(IBlueprint blueprint) {
        this.blueprint = blueprint;
    }

    @Override
    public IBlueprint getBlueprint() {
        if (blueprint != null) return blueprint;
        return MultiblockValidator.getBlueprint(blueprintTag.getString("name"));
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return this.handler.cast();
    }

    @Override
    public CompoundTag serializeNBT() {
        if (blueprint == null) return new CompoundTag();
        blueprintTag = blueprint.serialize();
        return blueprintTag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.blueprint = MultiblockValidator.getBlueprint(nbt.getString("name"));
        if (blueprint != null) {
            blueprint.deserialize(nbt);
        }
        blueprintTag = nbt;
    }

    public void addPosition(Direction facing, Level level, BlockPos pos) {
        if (positions == null) {
            positions = new BlockPos[2];
        };
        if (positions[0] != null && positions[0].equals(pos) || positions[1] != null && positions[1].equals(pos)) return;
        positions[index] = pos;
        if (positions[0] != null && positions[1] != null) {
            // do multiblock save
            BlockPos[] pos1 = calculateMinMax();
            MultiblockPatternGenerator.saveToStructureFile(level, facing, positions, pos1[0], pos1[1]);

            positions = null;

        }
        index++;
        if (index > 1) index = 0;

    }

    private BlockPos[] calculateMinMax() {
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


    public void setPosition(BlockPos clickedPos) {
        this.pos = clickedPos;
    }

    public BlockPos getPos() {
        return pos;
    }
}
