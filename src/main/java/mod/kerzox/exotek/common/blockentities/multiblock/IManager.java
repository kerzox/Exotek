package mod.kerzox.exotek.common.blockentities.multiblock;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.blockentities.multiblock.data.MultiblockPattern;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.IBlueprint;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface IManager {

    void tickManager();
    void sync();
    boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit);
    boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos);
    CompoundTag serialize();
    void deserialize(CompoundTag tag);
    void build(Direction facing, Level level, Map<BlockPos, BlockState> blocks, MultiblockPattern pattern);
    void disassemble(Level level, BlockPos disassembledAt);
    Map<BlockPos, MultiblockEntity> getBlocks();
    @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side);
    IBlueprint getBlueprint();
    void setManagingBlockEntity(ManagerMultiblockEntity entity);
    Pair<BlockPos, ManagerMultiblockEntity> getManagingBlockEntity();
    Direction getValidationDirection();
    List<BlockPos> getPositions();
    void needsRefresh();
}
