package mod.kerzox.exotek.common.blockentities;

import mod.kerzox.exotek.common.capability.CapabilityHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class MachineBlockEntity extends BasicBlockEntity  {

    private List<CapabilityHolder<?>> capabilities = new ArrayList<>();

    public MachineBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    protected void addCapabilities(CapabilityHolder<?>... capabilityHolder) {
        capabilities = List.of(capabilityHolder);
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        
    }

    @Override
    public void invalidateCaps() {
        for (CapabilityHolder<?> holder : capabilities) {
            holder.invalidate();
        }
        super.invalidateCaps();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        for (CapabilityHolder<?> holder : capabilities) {
            if (holder.getType() == cap) return (LazyOptional<T>) holder.getCapabilityHandler(side);
        }
        return super.getCapability(cap, side);
    }
}
