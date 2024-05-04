package mod.kerzox.exotek.common.blockentities.machine;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.client.gui.menu.SingleBlockMinerMenu;
import mod.kerzox.exotek.common.blockentities.MachineBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackHandlerUtils;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.common.capability.upgrade.UpgradableMachineHandler;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import static mod.kerzox.exotek.common.util.MiningUtil.*;

public class SingleBlockMinerEntity extends MachineBlockEntity implements IServerTickable, MenuProvider {

    private boolean atBottom;
    private int yLevel = getBlockPos().getY() - 1;
    private int tick;

    private int progress = 0;
    private int duration = 20 * 8;
    private int radius = 1;

    private int feTick = 125;
    private int oreCount = 0;

    private boolean stalled;
    private boolean running;

    // client bool
    private boolean showRadius = false;

    private Queue<Pair<BlockPos, BlockState>> blocksToMine = new LinkedList<>();
    private Pair<BlockPos, BlockState> currentBlock;
    private List<ItemStack> workingDrops = new ArrayList<>();

    // a outward facing inventory (insert is only internally)
    private SidedItemStackHandler inventory = new SidedItemStackHandler(6)
            .addOutputs(Direction.values());

    private SidedEnergyHandler energyStorage = new SidedEnergyHandler(32000);
    private UpgradableMachineHandler upgradableMachineHandler = new UpgradableMachineHandler(6) {

        @Override
        protected void onChange() {
            radius = 1;
            for (int i = 2; i < upgradableMachineHandler.getInventory().getSlots(); i++) {
                ItemStack stack = upgradableMachineHandler.getInventory().getStackInSlot(i);
                if (stack.is(ExotekRegistry.Items.RANGE_UPGRADE_ITEM.get())) {
                    radius += stack.getCount();
                    reset();
                }
            }
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

    public SingleBlockMinerEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.MINER_DRILL_ENTITY.get(), pos, state);
        addCapabilities(inventory, energyStorage, upgradableMachineHandler);
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("yLevel", this.yLevel);
        pTag.putInt("progress", this.progress);
        pTag.putInt("radius", this.radius);
        pTag.putInt("oreCount", this.oreCount);
        pTag.putBoolean("atBottom", this.atBottom);
        pTag.putBoolean("stalled", this.stalled);
        pTag.putBoolean("running", this.running);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.yLevel = pTag.getInt("yLevel");
        this.progress = pTag.getInt("progress");
        this.radius = pTag.getInt("radius");
        this.oreCount = pTag.getInt("oreCount");
        this.stalled = pTag.getBoolean("stalled");
        this.atBottom = pTag.getBoolean("atBottom");
        this.running = pTag.getBoolean("running");
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        if (tag.contains("reset")) reset();
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.getMainHandItem().is(ExotekRegistry.Items.SOFT_MALLET_ITEM.get())) {
                reset();
                return true;
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    private void reset() {
        atBottom = false;
        this.yLevel = this.getBlockPos().getY() - 1;
        this.oreCount = filterOres(scanBlocks(level, radius, worldPosition)).size();
        this.blocksToMine.clear();
        this.currentBlock = null;
        this.progress = 0;
        syncBlockEntity();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.oreCount = filterOres(scanBlocks(level, radius, worldPosition)).size();
        syncBlockEntity();
    }

    private boolean hasEnoughEnergy(int energy) {
        return energy >= feTick;
    }

    @Override
    public void tick() {
        super.tick();
        tick = (tick + 1) % 1_728_000;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) pushToNeighbouringInventories(inventory.getStackInSlot(i));
        }

        if (atBottom || !running) return;

        if (!hasEnoughEnergy(this.energyStorage.getEnergy())) return;

        if (blocksToMine.isEmpty()) {
            List<Pair<BlockPos, BlockState>> pairs = scanBlocks(level, this.radius, this.yLevel, this.worldPosition);
            if (pairs.stream().anyMatch(p -> p.getSecond().is(Blocks.VOID_AIR))) {
                atBottom = true;
                return;
            }

            blocksToMine.addAll(filterOres(pairs));
            currentBlock = blocksToMine.poll();
        }

        if (currentBlock == null || currentBlock.getSecond().isAir()) {
            yLevel--;
            return;
        }

        if (progress < duration) {
            this.energyStorage.consumeEnergy(feTick);
            progress++;
        } else {
            // mine block & get drops
            if (workingDrops.isEmpty() && level.getBlockState(currentBlock.getFirst()).equals(currentBlock.getSecond()))
                workingDrops.addAll(getDropsFromState(level, currentBlock.getFirst(), currentBlock.getSecond()));

            Queue<ItemStack> copied = new LinkedList<>(workingDrops);

            // insert each drop into the inventory until we empty the drop queue
            while (!copied.isEmpty()) {
                // get next drop
                ItemStack current = copied.poll();

                // attempt insert
                ItemStack ret = ItemHandlerHelper.insertItem(this.inventory, current, false);

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

            level.destroyBlock(currentBlock.getFirst(), false);

            // move to next block in queue
            currentBlock = blocksToMine.poll();

            // lower the y level by one
            yLevel--;

            // set progress back to zero
            progress = 0;

            // decrement the ore counter for the client
            this.oreCount--;

        }

        syncBlockEntity();
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public boolean isRunning() {
        return running;
    }

    private void pushToNeighbouringInventories(ItemStack stackInSlot) {

        for (Direction direction : Direction.values()) {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
            if (be != null) {
                if (!this.inventory.getOutputs().contains(direction)) continue;
                be.getCapability(ForgeCapabilities.ITEM_HANDLER, direction.getOpposite()).ifPresent(cap -> {
                    ItemStackHandlerUtils.insertAndModifyStack(cap, stackInSlot);
                });
            }
        }

    }

    public boolean isStalled() {
        return stalled;
    }

    public boolean isAtBottom() {
        return atBottom;
    }

    public int getDuration() {
        return duration;
    }

    public int getProgress() {
        return progress;
    }

    public int getFeTick() {
        return feTick;
    }

    public int getOreCount() {
        return oreCount;
    }

    public int getRadius() {
        return radius;
    }

    public int getTick() {
        return tick;
    }

    public int getyLevel() {
        return yLevel;
    }

    public void setShowRadius(boolean showRadius) {
        this.showRadius = showRadius;
    }

    public boolean showRadius() {
        return showRadius;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Miner Menu");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new SingleBlockMinerMenu(p_39954_, p_39955_, p_39956_, this);
    }


}
