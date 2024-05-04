package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.IManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockEntity extends BasicBlockEntity {

    protected IManager multiblockManager;
    protected CompoundTag multiblockData = new CompoundTag();
    protected BlockState mimicState;
    protected boolean hidden;
    public static final ModelProperty<BlockState> MIMIC = new ModelProperty<>();

    protected boolean needsUpdate = false;

    public static final String MULTIBLOCK_COMPOUND = "multiblock_data";

    public MultiblockEntity(BlockEntityType<?> pType, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
    }

    public IManager getMultiblockManager() {
        return multiblockManager;
    }

    public void setMultiblockManager(IManager multiblockManager) {
        this.multiblockManager = multiblockManager;
    }

    public BlockState getMimicState() {
        return mimicState;
    }

    public void setMimicState(BlockState mimicState) {
        this.mimicState = mimicState;
       // needsUpdate = true;
        syncBlockEntity();
    }

    public CompoundTag getMultiblockData() {
        return multiblockData;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (getMultiblockManager() != null) {
            return getMultiblockManager().getCapability(this, cap, side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(MIMIC, mimicState).build();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (mimicState != null){
            pTag.put("mimic", NbtUtils.writeBlockState(mimicState));
        }
        if (getMultiblockManager() != null) {
            pTag.put(MULTIBLOCK_COMPOUND, this.multiblockManager.serialize());
        }
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        BlockState oldMimic = mimicState;
        super.onDataPacket(net, pkt);
        if (oldMimic != mimicState) {
            level.getModelDataManager().requestRefresh(this);
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        super.addToUpdateTag(tag);
        if (mimicState != null){
            tag.put("mimic", NbtUtils.writeBlockState(mimicState));
        }
        if (getMultiblockManager() != null) {
            tag.put(MULTIBLOCK_COMPOUND, this.multiblockManager.serialize());
        }
        tag.putBoolean("update", this.needsUpdate);
        tag.putBoolean("hidden", this.hidden);
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        if (getMultiblockManager() != null) getMultiblockManager().updateFromNetwork(tag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("mimic")) {
            mimicState = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), pTag.getCompound("mimic"));
        }
        if (pTag.contains(MULTIBLOCK_COMPOUND)) {
            multiblockData = pTag.getCompound(MULTIBLOCK_COMPOUND);
        }
        needsUpdate = pTag.getBoolean("update");
        setHiddenInWorld(pTag.getBoolean("hidden"));
        if (level != null && getMultiblockManager() != null) getMultiblockManager().deserialize(multiblockData);
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHiddenInWorld(boolean value) {
        hidden = value;
    }
}
