package mod.kerzox.exotek.common.blockentities.multiblock;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MultiblockInvisibleEntity extends MultiblockEntity implements IClientTickable {

    public MultiblockInvisibleEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.MULTIBLOCK_INVISIBLE_ENTITY.get(), pPos, pBlockState);
    }

    @Override
    public void clientTick() {
        if (needsUpdate) {
            this.requestModelDataUpdate();
            needsUpdate = false;
        }
    }

}
