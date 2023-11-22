package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AlternatorManager extends AbstractMultiblockManager {

    private float scaleCoefficient = 0.015f;
    private float shift = 0;
    private SidedEnergyHandler handler = new SidedEnergyHandler(1000000);

    public void addEnergyFromKinetic(int rotationSpeed) {
        int power = (int) Math.round(scaleCoefficient * Math.pow(rotationSpeed, 2) + shift);
        handler.addEnergy(power);
    }

    public AlternatorManager() {
        super("alternator");
        handler.addOutput(Direction.values());
    }

    @Override
    protected void tick() {
        if (handler.getEnergy() > 0) {
            BlockEntity blockEntity = getLevel().getBlockEntity(getManagingBlockEntity().getFirst().below().relative(validationDirection, 2));
            if (blockEntity != null) {
                blockEntity.getCapability(ForgeCapabilities.ENERGY, validationDirection).ifPresent(cap -> {
                    if (cap.canReceive()) {
                        int amount = cap.receiveEnergy(handler.getEnergy(), false);
                        handler.consumeEnergy(amount);
                    }
                });
            }
        }
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
        tag.put("energyHandler", this.handler.serialize());
        return tag;
    }

    @Override
    public void readCache(CompoundTag tag) {
        if (this.handler != null) this.handler.deserialize(tag.getCompound("energyHandler"));
    }

    public SidedEnergyHandler getHandler() {
        return handler;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ENERGY.orEmpty(cap, this.handler.getHandler(side));
    }
}
