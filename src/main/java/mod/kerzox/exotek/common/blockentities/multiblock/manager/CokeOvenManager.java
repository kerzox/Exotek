package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
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

public class CokeOvenManager extends AbstractMultiblockManager {

    private ItemStackInventory inventory = new ItemStackInventory(1, 1);

    private int tick = 0;
    public CokeOvenManager() {
        super("coke_oven");
    }

    @Override
    public void tickManager() {
        super.tickManager();
    }



    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) if (getManagingBlockEntity() != null && getManagingBlockEntity().getSecond() != null) return this.getManagingBlockEntity().getSecond().onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
        return false;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, inventory.getHandler(side));
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.put("itemHandler", this.inventory.serialize());
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.inventory.deserialize(tag.getCompound("itemHandler"));
        super.deserialize(tag);
    }
}
