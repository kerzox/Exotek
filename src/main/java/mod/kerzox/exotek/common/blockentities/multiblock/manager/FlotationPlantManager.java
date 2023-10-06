package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FlotationPlantManager extends AbstractMultiblockManager {

    public FlotationPlantManager() {
        super("froth_flotation");
    }

    @Override
    public void tickManager() {

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
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        return null;
    }
}
