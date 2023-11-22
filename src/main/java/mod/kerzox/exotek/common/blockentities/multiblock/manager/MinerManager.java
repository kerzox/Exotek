package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.deposit.OreDeposit;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

public class MinerManager extends AbstractMultiblockManager {

    protected RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.model.idle");
    protected RawAnimation DRILLING = RawAnimation.begin().thenLoop("animation.model.drilling");
    protected RawAnimation RETURN = RawAnimation.begin().thenPlay("animation.model.return");
    protected boolean running = false;
    protected boolean returnHome = false;
    protected boolean hasOrePatch = false;
    protected int duration;
    protected int maxDuration = 20*5;

    private SidedSingleFluidTank handler = new SidedSingleFluidTank(32000);
    private final ItemStackInventory itemStackHandler = new ItemStackInventory(0, 6);
    protected SidedEnergyHandler energyHandler = new SidedEnergyHandler(32000);

    public MinerManager() {
        super("miner");
    }

    @Override
    protected void tick() {

        // check for energy
        if (this.energyHandler.getEnergy() <= 0) return;

        if (!energyHandler.hasEnough(150) && running) {
            running = false;
            returnHome = true;
            sync();
            return;
        };

        energyHandler.consumeEnergy(Math.max(0 , Math.min(150, energyHandler.getEnergy())));

        // find the chunk capability
        IDeposit deposit = getLevel().getChunkAt(getManagingBlockEntity().getFirst()).getCapability(ExotekCapabilities.DEPOSIT_CAPABILITY).orElse(null);
        if (deposit instanceof ChunkDeposit chunkDeposit) {
            if (chunkDeposit.isOreDeposit()) {

                // begin working animation
                if (!hasOrePatch) {
                    hasOrePatch = true;
                    running = true;
                    duration = maxDuration;
                    sync();
                }

                if (duration <= 0) {
                    OreDeposit oreDeposit = chunkDeposit.getOreDeposit();
                    int choice = getLevel().getRandom().nextInt(oreDeposit.getItems().size());
                    int size = oreDeposit.getItems().get(choice).getItemStack().getCount();

                    if (size > 0) {
                        ItemStack ore =  oreDeposit.getItems().get(choice).getItemStack().copy();
                        oreDeposit.getItems().get(choice).getItemStack().shrink(1);
                        for (int i = 0; i < itemStackHandler.getOutputHandler().getSlots(); i++) {
                            ItemStack stack = this.itemStackHandler.getOutputHandler().forceInsertItem(i, new ItemStack(ore.getItem(), 1), true);
                            if (stack.isEmpty()) {
                                this.itemStackHandler.getOutputHandler().forceInsertItem(i, new ItemStack(ore.getItem(), 1), false);
                            }
                        }
                    }
                } else {
                    duration--;
                }

            }
        }
    }

    private boolean isItemHatch(BlockPos pos) {
        if (validationDirection == Direction.SOUTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(2, -1, 1));
        else if (validationDirection == Direction.WEST)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-1, -1, 2));
        else if (validationDirection == Direction.NORTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-2, -1, -1));
        else
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(1, -1, -2));
    }

    private boolean isEnergyHatch(BlockPos pos) {
        if (validationDirection == Direction.SOUTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(2, 0, 1));
        else if (validationDirection == Direction.WEST)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-1, 0, 2));
        else if (validationDirection == Direction.NORTH)
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(-2, 0, -1));
        else
            return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(1, 0, -2));
    }


    private boolean isFluidHatch(BlockPos pos) {
        return pos.subtract(getManagingBlockEntity().getFirst()).equals(new BlockPos(0, -1, 0));
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return isFluidHatch(multiblockEntity.getBlockPos()) ? handler.getHandler(side) : LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return isItemHatch(multiblockEntity.getBlockPos()) ? itemStackHandler.getHandler(side) : LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return isEnergyHatch(multiblockEntity.getBlockPos()) ? energyHandler.getHandler(side) : LazyOptional.empty();
        }
        return LazyOptional.empty();
    }


    public <T extends GeoAnimatable> PlayState animationPredicate(AnimationState<T> tAnimationState) {

        if (running) {
            return tAnimationState.setAndContinue(DRILLING);
        }

        if (returnHome) {
            return tAnimationState.setAndContinue(RETURN);
        }

        return tAnimationState.setAndContinue(IDLE);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return false;
    }

    @Override
    public CompoundTag writeToCache() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("running", this.running);
        return tag;
    }

    @Override
    public void readCache(CompoundTag tag) {
        this.running = tag.getBoolean("running");
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }
}
