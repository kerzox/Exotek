package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidTankManager extends AbstractMultiblockManager {

    private SidedSingleFluidTank tank = new SidedSingleFluidTank(350000 * 25);

    public FluidTankManager() {
        super("fluid_tank");
        this.tank.addOutput(Direction.values());
    }

    public SidedSingleFluidTank getTank() {
        return tank;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return false;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    @Override
    public CompoundTag writeToCache() {
        CompoundTag tag = super.writeToCache();
        tag.put("fluidHandler", this.tank.serialize());
        return tag;
    }

    @Override
    public void readCache(CompoundTag tag) {
        this.tank.deserialize(tag.getCompound("fluidHandler"));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, tank.getHandler(side));
    }
}
