package mod.kerzox.exotek.common.blockentities.machine;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.client.gui.menu.SingleBlockMinerMenu;
import mod.kerzox.exotek.common.blockentities.ContainerisedBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.ForgeEnergyStorage;
import mod.kerzox.exotek.common.capability.item.ItemStackHandlerUtils;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.common.capability.upgrade.UpgradableMachineHandler;
import mod.kerzox.exotek.common.item.MachineUpgradeItem;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class SingleBlockMinerEntity extends ContainerisedBlockEntity implements IServerTickable {

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

    private ForgeEnergyStorage energyStorage = new ForgeEnergyStorage(32000);
    private LazyOptional<ForgeEnergyStorage> energyHandler = LazyOptional.of(() -> energyStorage);
    private UpgradableMachineHandler upgradableMachineHandler = new UpgradableMachineHandler(6) {

        @Override
        protected void onChange() {
            radius = 1;
            for (int i = 2; i < upgradableMachineHandler.getInventory().getSlots(); i++) {
                ItemStack stack = upgradableMachineHandler.getInventory().getStackInSlot(i);
                if (stack.is(Registry.Items.RANGE_UPGRADE_ITEM.get())) {
                    radius += stack.getCount();
                    reset();
                }
            }
        }

        // Move this to tags
        @Override
        protected boolean isUpgradeValid(int slot, ItemStack stack) {
            if (stack.getItem() == Registry.Items.FORTUNE_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == Registry.Items.SILK_TOUCH_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == Registry.Items.RANGE_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == Registry.Items.SPEED_UPGRADE_ITEM.get()) return true;
            else if (stack.getItem() == Registry.Items.ENERGY_UPGRADE_ITEM.get()) return true;
            else return stack.getItem() == Registry.Items.ANCHOR_UPGRADE_ITEM.get();
        }


    };

    public SingleBlockMinerEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.MINER_DRILL_ENTITY.get(), pos, state);
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("itemHandler", this.inventory.serializeNBT());
        pTag.put("energyHandler", this.energyStorage.serializeNBT());
        pTag.put("upgrades", this.upgradableMachineHandler.serializeNBT());
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
        this.inventory.deserializeNBT(pTag.getCompound("itemHandler"));
        this.energyStorage.deserializeNBT(pTag.get("energyHandler"));
        this.upgradableMachineHandler.deserializeNBT(pTag.getCompound("upgrades"));
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
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return this.inventory.getCapability(cap, side);
        else if (cap == ForgeCapabilities.ENERGY) return this.energyHandler.cast();
        else if (cap == ExotekCapabilities.UPGRADABLE_MACHINE) return this.upgradableMachineHandler.getHandler();
        return super.getCapability(cap, side);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.getMainHandItem().is(Registry.Items.SOFT_MALLET_ITEM.get())) {
                reset();
                return true;
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    /**
     * This will scan for all blocks below in the defined radius until you hit voic air
     *
     * @param radius (Radius is x blocks from the world position. (1 is 3x3, 2 (5x5) etc).
     * @return blocks found
     */

    private List<Pair<BlockPos, BlockState>> scanBlocks(int radius) {
        return scanBlocks(radius, Integer.MAX_VALUE);
    }

    /**
     * This will scan for all blocks below in the defined radius until you reach a certain y level
     *
     * @param radius (Radius is x blocks from the world position. (1 is 3x3, 2 (5x5) etc).
     * @return blocks found
     */

    private List<Pair<BlockPos, BlockState>> scanBlocks(int radius, int stopAt) {
        List<Pair<BlockPos, BlockState>> blocks = new ArrayList<>();
        BlockPos pos = this.worldPosition;
        int y = pos.getY() - 1;
        while (y != Math.min(stopAt - 1, pos.getY())) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
                for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
                    BlockPos bPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(bPos);
                    blocks.add(Pair.of(bPos, state));
                    if (state.is(Blocks.VOID_AIR)) return blocks;
                }
            }
            y--;
        }
        return blocks;
    }

    private List<Pair<BlockPos, BlockState>> filterOres(List<Pair<BlockPos, BlockState>> pairs) {
        return pairs.stream().filter((pair) ->
                pair.getSecond().is(Tags.Blocks.ORES)).collect(Collectors.toList());
    }

    /**
     * TODO add optionals such as fortune and silk touch.
     * Get drops from provided blockState and blockPos
     *
     * @param pos   blocks position in world
     * @param state block state
     * @return drops associated with this block from a netherite pickaxe.
     */

    private List<ItemStack> getDropsFromState(BlockPos pos, BlockState state) {

        LootParams.Builder builder = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE))
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));

        return state.getDrops(builder);
    }

    private void reset() {
        atBottom = false;
        this.yLevel = this.getBlockPos().getY() - 1;
        this.oreCount = filterOres(scanBlocks(radius)).size();
        this.blocksToMine.clear();
        this.currentBlock = null;
        this.progress = 0;
        syncBlockEntity();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        this.oreCount = filterOres(scanBlocks(radius)).size();
        syncBlockEntity();
    }

    private boolean hasEnoughEnergy(int energy) {
        return energy >= feTick;
    }

    @Override
    public void tick() {

        tick = (tick + 1) % 1_728_000;

        for (int i = 0; i < inventory.getSlots(); i++) {
            if (!inventory.getStackInSlot(i).isEmpty()) pushToNeighbouringInventories(inventory.getStackInSlot(i));
        }

        if (atBottom || !running) return;

        if (!hasEnoughEnergy(this.energyStorage.getEnergyStored())) return;

        if (blocksToMine.isEmpty()) {
            List<Pair<BlockPos, BlockState>> pairs = scanBlocks(this.radius, this.yLevel);
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
            this.energyStorage.internalRemoveEnergy(feTick);
            progress++;
        } else {
            // mine block & get drops
            if (workingDrops.isEmpty() && level.getBlockState(currentBlock.getFirst()).equals(currentBlock.getSecond()))
                workingDrops.addAll(getDropsFromState(currentBlock.getFirst(), currentBlock.getSecond()));

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
