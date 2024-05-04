package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.deposit.OreDeposit;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.common.capability.upgrade.UpgradableMachineHandler;
import mod.kerzox.exotek.registry.ConfigConsts;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static mod.kerzox.exotek.common.util.MiningUtil.*;

public class MinerManager extends AbstractMultiblockManager {

    protected RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.model.idle");
    protected RawAnimation DRILLING = RawAnimation.begin().thenLoop("animation.model.drilling");
    protected RawAnimation RETURN = RawAnimation.begin().thenPlay("animation.model.return");

    protected boolean canWork = false;

    protected boolean running = false;
    protected boolean returnHome = false;
    protected boolean hasOrePatch = false;
    protected int mode = 0; // 0 = in world blocks, 1 = chunk deposit
    protected int duration;
    protected int maxDuration = 20*5;
    private SidedSingleFluidTank handler = new SidedSingleFluidTank(32000);
    private SidedItemStackHandler itemStackHandler = new SidedItemStackHandler(6)
            .addOutputs(Direction.values());
    protected SidedEnergyHandler energyHandler = new SidedEnergyHandler(32000);

    private final UpgradableMachineHandler upgradableMachineHandler = new UpgradableMachineHandler(6) {

        @Override
        protected void onChange() {
            radius = 5;
            for (int i = 2; i < upgradableMachineHandler.getInventory().getSlots(); i++) {
                ItemStack stack = upgradableMachineHandler.getInventory().getStackInSlot(i);
                if (stack.is(ExotekRegistry.Items.RANGE_UPGRADE_ITEM.get())) {
                    radius += stack.getCount();
                }
            }
            update = true;
        }

        // Move this to tags
        @Override
        protected boolean isUpgradeValid(int slot, ItemStack stack) {
            if (stack.getItem() == ExotekRegistry.Items.FORTUNE_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == ExotekRegistry.Items.SILK_TOUCH_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == ExotekRegistry.Items.RANGE_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == ExotekRegistry.Items.SPEED_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == ExotekRegistry.Items.ENERGY_UPGRADE_ITEM.get()) return true;
            else return stack.getItem() == ExotekRegistry.Items.ANCHOR_UPGRADE_ITEM.get();
        }


    };
    private int progress = 0;
    private int radius = 5;
    private boolean showRadius = false;
    private int oreCount = 0;
    private boolean stalled;
    private boolean update;
    private boolean atBottom;
    private Queue<Pair<BlockPos, BlockState>> blocksToMine = new LinkedList<>();
    private Pair<BlockPos, BlockState> currentBlock;
    private List<ItemStack> workingDrops = new ArrayList<>();

    private int currentYLevel = Integer.MAX_VALUE;

    public MinerManager() {
        super("miner");
    }

    @Override
    protected void tick() {
        if (update) {
            update = false;
            sync();
        }

        for (int i = 0; i < this.itemStackHandler.getSlots(); i++) {
            BlockPos pos = getItemHatch();
            ItemStack stack = this.itemStackHandler.internalExtractItem(i, 1, false);
            ItemEntity entity = new ItemEntity(getLevel(), pos.getX(), pos.getY(), pos.getZ(), stack);
            getLevel().addFreshEntity(entity);

        }

        if (this.energyHandler.getEnergy() <= 0) return;

        if (running) {
            if (mode == 0) {
                if (!energyHandler.hasEnough(ConfigConsts.INDUSTRIAL_MINING_DRILL_FE_TICK)) {
                    reset();
                    running = false;
                    returnHome = true;
                    sync();
                    return;
                }
            } else {
                if (!energyHandler.hasEnough((int) Math.round(ConfigConsts.INDUSTRIAL_MINING_DRILL_FE_TICK * ConfigConsts.INDUSTRIAL_MINING_DRILL_MODIFIER))) {
                    reset();
                    running = false;
                    returnHome = true;
                    sync();
                    return;
                }
            }
        }

        if (canWork) {
            if (mode == 0) mineRealBlocks();
            else mineChunkDeposit();
        }

    }

    private void mineRealBlocks() {

        if (atBottom) {
            this.running = false;
            this.returnHome = true;
            return;
        }

        if (!running) {
            running = true;
            duration = maxDuration;
            this.currentYLevel = this.getManagingBlockEntity().getFirst().getY() - 2;
        }

        // find ores
        if (blocksToMine.isEmpty()) {
            List<Pair<BlockPos, BlockState>> pairs = scanBlocks(this.getLevel(), this.radius, this.getYLevel(), getCenterPos());
            if (pairs.stream().anyMatch(p -> p.getSecond().is(Blocks.VOID_AIR))) {
                atBottom = true;
                return;
            }

            blocksToMine.addAll(filterOres(pairs));
            currentBlock = blocksToMine.poll();
        }

        if (currentBlock == null || currentBlock.getSecond().isAir()) {
            currentYLevel--;
            return;
        }

        if (progress < duration) {
            consumeEnergy();
            progress++;
        } else {
            // mine block & get drops
            if (workingDrops.isEmpty() && getLevel().getBlockState(currentBlock.getFirst()).equals(currentBlock.getSecond()))
                workingDrops.addAll(getDropsFromState(getLevel(), currentBlock.getFirst(), currentBlock.getSecond()));

            Queue<ItemStack> copied = new LinkedList<>(workingDrops);

            // insert each drop into the inventory until we empty the drop queue
            while (!copied.isEmpty()) {
                // get next drop
                ItemStack current = copied.poll();

                // attempt insert
                ItemStack ret = ItemHandlerHelper.insertItem(this.itemStackHandler, current, false);

                // remove from the working drops list if we managed to completely insert this itemstack
                if (ret.isEmpty()) workingDrops.remove(current);
            }

            /*
                if our working drops is not completely empty we want to just wait until we can fully insert.
                This could be changed so that the working drops just constantly adds itemstacks to the drop queue and
                at a later tick will insert. But i want the miner to just wait until there is room.
             */

            if (!workingDrops.isEmpty()) {
                stalled = true;
                return;
            }

            stalled = false;

            getLevel().destroyBlock(currentBlock.getFirst(), false);

            // move to next block in queue
            currentBlock = blocksToMine.poll();

            // lower the y level by one
            currentYLevel--;

            // set progress back to zero
            progress = 0;

            // decrement the ore counter for the client
            this.oreCount--;

        }

    }

    private void mineChunkDeposit() {
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

                consumeEnergy();

                if (duration <= 0) {
                    OreDeposit oreDeposit = chunkDeposit.getOreDeposit();
                    int choice = getLevel().getRandom().nextInt(oreDeposit.getItems().size());
                    int size = oreDeposit.getItems().get(choice).getItemStack().getCount();

                    if (size > 0) {
                        ItemStack ore =  oreDeposit.getItems().get(choice).getItemStack().copy();
                        oreDeposit.getItems().get(choice).getItemStack().shrink(1);
                        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
                            ItemStack stack = this.itemStackHandler.insertItem(i, new ItemStack(ore.getItem(), 1), true);
                            if (stack.isEmpty()) {
                                this.itemStackHandler.insertItem(i, new ItemStack(ore.getItem(), 1), false);
                            }
                        }
                    }
                } else {
                    duration--;
                }

            }
        }
    }

    public BlockPos getCenterPos() {
        Direction facing = validationDirection.getOpposite();
        BlockPos pos = getManagingBlockEntity() != null ? getManagingBlockEntity().getFirst() : new BlockPos(0, 0, 0);
        if (pos == null) pos = new BlockPos(0, 0, 0);

        if (facing == Direction.NORTH) {
            pos = pos.offset(1, 0, 1);
        }

        if (facing == Direction.SOUTH) {
            pos = pos.offset(-1, 0, -1);
        }

        if (facing == Direction.EAST) {
            pos = pos.offset(-1, 0, 1);
        }

        if (facing == Direction.WEST) {
            pos = pos.offset(1, 0, -1);
        }
        return pos;
    }
    public void reset() {
        this.oreCount = filterOres(scanBlocks(getLevel(), radius, getCenterPos())).size();
        this.currentYLevel = this.getManagingBlockEntity().getFirst().getY() - 2;
        this.atBottom = false;
        this.running  = false;
    }

    public int getYLevel() {
        return currentYLevel;
    }

    private void consumeEnergy() {
        energyHandler.consumeEnergy(Math.max(0 , Math.min(
                mode == 0 ? ConfigConsts.INDUSTRIAL_MINING_DRILL_FE_TICK
                : Math.round(ConfigConsts.INDUSTRIAL_MINING_DRILL_FE_TICK * ConfigConsts.INDUSTRIAL_MINING_DRILL_MODIFIER), energyHandler.getEnergy())));
    }

    private BlockPos getItemHatch() {
        if (validationDirection == Direction.SOUTH)
            return getManagingBlockEntity().getFirst().offset(new BlockPos(2, -1, 1));
        else if (validationDirection == Direction.WEST)
            return getManagingBlockEntity().getFirst().offset(new BlockPos(-1, -1, 2));
        else if (validationDirection == Direction.NORTH)
            return getManagingBlockEntity().getFirst().offset(new BlockPos(-2, -1, -1));
        else
            return getManagingBlockEntity().getFirst().offset(new BlockPos(1, -1, -2));
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
            return side == null ? handler.getHandler(side) : isFluidHatch(multiblockEntity.getBlockPos()) ? handler.getHandler(side) : LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return side == null ? itemStackHandler.getCapability(cap, side) : isItemHatch(multiblockEntity.getBlockPos()) ? itemStackHandler.getCapability(cap, side) : LazyOptional.empty();
        }
        if (cap == ForgeCapabilities.ENERGY) {
            return side == null ? energyHandler.getHandler(side) : isEnergyHatch(multiblockEntity.getBlockPos()) ? energyHandler.getHandler(side) : LazyOptional.empty();
        }
        if (cap == ExotekCapabilities.UPGRADABLE_MACHINE) {
            return upgradableMachineHandler.getHandler().cast();
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
    public void updateFromNetwork(CompoundTag tag) {
        if (tag.contains("reset")) reset();
        if (tag.contains("start")) {
            canWork = !canWork;
        }
        if (tag.contains("mode")) this.mode = tag.getInt("mode");
        if (tag.contains("showRadius")) this.showRadius = !this.showRadius;
    }

    @Override
    public CompoundTag writeToCache() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("running", this.running);
        tag.putInt("mode", this.mode);
        tag.putBoolean("can_work", this.canWork);
        tag.put("upgrades", this.upgradableMachineHandler.serialize());
        tag.putInt("yLevel", this.currentYLevel);
        tag.putInt("progress", this.progress);
        tag.putInt("radius", this.radius);
        tag.putInt("oreCount", this.oreCount);
        tag.putBoolean("atBottom", this.atBottom);
        tag.putBoolean("stalled", this.stalled);
        tag.putBoolean("showRadius", this.showRadius);
        tag.put("energy", this.energyHandler.serialize());
        tag.put("item", this.itemStackHandler.serialize());
        tag.putInt("duration", this.duration);
        return tag;
    }

    @Override
    public void readCache(CompoundTag tag) {
        int radius = tag.getInt("radius");
        this.running = tag.getBoolean("running");
        this.canWork = tag.getBoolean("can_work");
        this.mode = tag.getInt("mode");
        this.currentYLevel = tag.getInt("yLevel");
        this.progress = tag.getInt("progress");
        this.radius = radius == 0 ? 5 : radius;
        this.oreCount = tag.getInt("oreCount");
        this.stalled = tag.getBoolean("stalled");
        this.duration = tag.getInt("duration");
        this.showRadius = tag.getBoolean("showRadius");
        this.atBottom = tag.getBoolean("atBottom");
        this.energyHandler.deserialize(tag.getCompound("energy"));
        this.itemStackHandler.deserialize(tag.getCompound("item"));
        if (tag.contains("upgrades")) this.upgradableMachineHandler.deserialize(tag.getCompound("upgrades"));
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    public int getMode() {
        return this.mode;
    }

    public boolean isRunning() {
        return running;
    }

    public boolean isAtBottom() {
        return atBottom;
    }

    public boolean isStalled() {
        return stalled;
    }

    public boolean canWork() {
        return canWork;
    }

    public int getOreCount() {
        return oreCount;
    }

    public int getProgress() {
        return progress;
    }

    public int getDuration() {
        return duration;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    public boolean showRadius() {
        return showRadius;
    }

    public int getRadius() {
        return radius;
    }
}
