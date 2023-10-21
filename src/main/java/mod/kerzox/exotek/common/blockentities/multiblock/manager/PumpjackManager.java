package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.PumpjackEntity;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PumpjackManager extends AbstractMultiblockManager  {

    private SidedSingleFluidTank handler = new SidedSingleFluidTank(32000);
    protected RecipeInventoryWrapper recipeInventoryWrapper = new RecipeInventoryWrapper(handler);
    protected SidedEnergyHandler energyHandler = new SidedEnergyHandler(32000);

    protected int feTick = 1;
    protected boolean running;

    protected boolean updateAnimationOnNextTick = false;

    public PumpjackManager() {
        super("pump_jack");
    }

    @Override
    protected void tick() {
        doRecipe();
        if (updateAnimationOnNextTick) {
            updateAnimationOnNextTick = false;
            running = false;
            getPumpjackEntity().setRunning(false);

        }
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        pPlayer.sendSystemMessage(Component.literal(energyHandler.getEnergy()+" ENERGY"));
        pPlayer.sendSystemMessage(Component.literal(handler.getFluid().getAmount()+" FLUID").append(handler.getFluid().getTranslationKey()));
        return false;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    private boolean isCapabilityHatch(BlockPos pos) {
        if (validationDirection == Direction.SOUTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-1, 0, 5));
        else if (validationDirection == Direction.WEST)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-5, 0, -1));
        else if (validationDirection == Direction.NORTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(1, 0, -5));
        else
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(5, 0, 1));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        if (isCapabilityHatch(multiblockEntity.getBlockPos())) {
            if (cap == ForgeCapabilities.FLUID_HANDLER) {
                return handler.getHandler(side);
            }
            if (cap == ForgeCapabilities.ENERGY) {
                return energyHandler.getHandler(side);
            }
        }
        return LazyOptional.empty();
    }



    public PumpjackEntity getPumpjackEntity() {
        return (PumpjackEntity) this.getManagingBlockEntity().getSecond();
    }

    public void doRecipe() {
        // ignore if our tank is full
        if (handler.getFluidAmount() == handler.getCapacity()) return;

        // get deposit from the chunk
        ChunkDeposit deposit = ChunkDeposit.getDepositFromPosition(getLevel(), getManagingBlockEntity().getFirst());

        if (deposit == null) return;
        if (!deposit.isFluidDeposit()) return;

        // get result fluid
        FluidStack result = deposit.getFluids().get(0);

        if (result.isEmpty()) {
            getPumpjackEntity().setRunning(false);
            return;
        }

        // hasn't starting running but we have a working recipe
        if (!running) {
            running = true;
            getPumpjackEntity().setRunning(true);
        }

        // do power consume TODO ideally we want the machine to stop working until energy returns this will be looked into after
        if (!energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
            updateAnimationOnNextTick = true;
            running = false;
            return;
        };

        energyHandler.consumeEnergy(feTick);
        // check if output tank is valid
        if (!this.handler.getFluid().isEmpty()) {
            if (!this.handler.getFluid().isFluidEqual(result)) return;
        }

        // now fill output tank
        this.handler.fill(new FluidStack(result.getFluid(), 1), IFluidHandler.FluidAction.EXECUTE);
        result.shrink(1);

    }

    public Level getLevel() {
        return getManagingBlockEntity().getSecond().getLevel();
    }
}
