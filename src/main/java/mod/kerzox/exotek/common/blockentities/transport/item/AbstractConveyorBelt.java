package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.block.transport.IConveyorBeltBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.ConveyorBeltPorter;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.EmptyConveyorCover;
import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.item.ConveyorBeltCoverItem;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractConveyorBelt<T extends AbstractConveyorBelt<T>> extends BasicBlockEntity implements IConveyorBelt<T>, IConveyorBeltCollision, IServerTickable {

    public static final int CONVEYOR_ITEM_SLOT = 0;
    protected HashSet<IConveyorBelt<?>> beltsInDirectionFacing = new HashSet<>();
    protected boolean stop;
    protected int count = 0;

    private ConveyorBeltInventory inventory = new ConveyorBeltInventory(this, 1);
    private LazyOptional<ConveyorBeltInventory> handler = LazyOptional.of(() -> inventory);
    private NonNullList<IConveyorCover> covers = NonNullList.withSize(5, new EmptyConveyorCover());

    private boolean regenerateCovers = true;

    public AbstractConveyorBelt(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        for (int i = 0; i < covers.size(); i++) {
            covers.get(i).setBelt(this);
            covers.get(i).setDirection(Direction.values()[i]);

        }
    }

    @Override
    public void onConveyorBeltItemStackPassed(ConveyorBeltItemStack itemStack, Vec3 itemStackVectorPos, IConveyorBelt<?> belt) {

    }

    protected void onItemStackInserted() {

    }

    public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, IConveyorBelt<?> beltPassingItem, Level level, Vec3 itemStackVectorPos) {
        boolean ramp = beltPassingItem instanceof ConveyorBeltRampEntity;
        if (!beltPassingItem.getInventory().extractItem(CONVEYOR_ITEM_SLOT, 1, true).isEmpty()) {
//
//            // check if the belt passing the item to another conveyor belt affects the transfer.
//            Optional<IConveyorCover> conveyorCover = beltPassingItem.getBelt().getActiveCovers().stream().filter(IConveyorCover::effectsConveyorExtract).findFirst();
//
//            // if this is present we only try to use the covers transfer
//            if (conveyorCover.isPresent()) {
//                if (conveyorCover.get().onConveyorExtract(itemStack, itemStackVectorPos, this, beltPassingItem)) {
//                    beltPassingItem.getInventory().conveyorBeltExtract(CONVEYOR_ITEM_SLOT);
//                    beltPassingItem.onConveyorBeltItemStackPassed(itemStack, itemStackVectorPos, this);
//                    return true;
//                }
//                return false;
//            }

            if (getInventory().conveyorBeltInsert(CONVEYOR_ITEM_SLOT, itemStack)) {
                beltPassingItem.getInventory().conveyorBeltExtract(CONVEYOR_ITEM_SLOT);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public IConveyorBelt<?> getNextBelt() {
        if(level.getBlockState(worldPosition.relative(getBeltDirection())).getBlock() instanceof IConveyorBeltBlock beltBlock) {
            if (level.getBlockEntity(worldPosition.relative(getBeltDirection())) instanceof IConveyorBelt<?> belt) {
                return belt;
            }
            // if we get here we probably have a ramp top
            if (level.getBlockEntity(worldPosition.relative(getBeltDirection()).below()) instanceof IConveyorBelt<?> belt) {
                return belt;
            }
        }
        return null;
    }

    public float getSpeed() {
        return 0.5f;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            if (pPlayer.getMainHandItem().isEmpty() && !pPlayer.isShiftKeyDown()) {
                pPlayer.sendSystemMessage(Component.literal("Server Thread"));
                pPlayer.sendSystemMessage(Component.literal("Has a belt in front " + hasBeltInFront()));
                pPlayer.sendSystemMessage(Component.literal("Belts: " + beltsInDirectionFacing.size()));
                pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
                pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
                pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
            }
            if (pPlayer.isShiftKeyDown()) {
                ItemHandlerHelper.giveItemToPlayer(pPlayer, this.inventory.extractItem(0, 1, false), pPlayer.getInventory().selected);
            }
            if (pPlayer.getMainHandItem().getItem() == Items.GOLD_INGOT) {
                pPlayer.sendSystemMessage(Component.literal("Has a cover at position " + getCoverByClickedPosition(pHit.getLocation()).isTicking()));
                if (getCoverByClickedPosition(pHit.getLocation()) instanceof ConveyorBeltPorter porter) {
                    porter.setToInsert(!porter.inserting());
                }
            }
        }
        if (pLevel.isClientSide && pPlayer.isShiftKeyDown() && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Client Thread"));
            pPlayer.sendSystemMessage(Component.literal("Belts: " + getBeltCount()));
            pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
        }
        if (pPlayer.getMainHandItem().getItem() instanceof ConveyorBeltCoverItem coverItem && pHand == InteractionHand.MAIN_HAND) {
            IConveyorCover coverToAdd = coverItem.getCover();
            if (!attemptToAddCover(coverToAdd, level, pPos, pHit.getLocation())) {
                if (!level.isClientSide) pPlayer.sendSystemMessage(Component.literal("Failed to place cover at this position"));
            } else {
                regenerateCovers = false;
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public IConveyorCover getCoverByClickedPosition(Vec3 clickedPos) {

        BlockPos pos = worldPosition;

        Vec3 clicked = clickedPos.subtract(pos.getX(), pos.getY(), pos.getZ());
        double xAxis = clicked.x - .5;
        double zAxis = clicked.z - .5;
        System.out.println(clicked.x < 0.25f);
        System.out.println(clicked.x > 0.75f);

        System.out.println(clicked.z < 0.25f);
        System.out.println(clicked.z > 0.75f);


        if (Math.max(Math.abs(xAxis), Math.abs(zAxis)) == Math.abs(xAxis)) {
            // check center
            if (clicked.x > 0.25f && clicked.x < 0.75f) return getCovers().get(0);
            else return xAxis < 0 ? getCovers().get(3) : getCovers().get(4);
        } else {
            if (clicked.z > 0.25f && clicked.z < 0.75f) return getCovers().get(0);
            else return zAxis < 0 ? getCovers().get(1) : getCovers().get(2);
        }
    }

    private boolean attemptToAddCover(IConveyorCover coverItem, Level level, BlockPos pos, Vec3 clickedPos) {
        Vec3 clicked = clickedPos.subtract(pos.getX(), pos.getY(), pos.getZ());
        double xAxis = clicked.x - .5;
        double zAxis = clicked.z - .5;
        if (Math.max(Math.abs(xAxis), Math.abs(zAxis)) == Math.abs(xAxis)) {
            // check center
            if (clicked.x > 0.25f && clicked.x < 0.75f) return addCover(1, coverItem);
            else return addCover(xAxis < 0 ? 4 : 5, coverItem);
        } else {
            if (clicked.z > 0.25f && clicked.z < 0.75f) return addCover(1, coverItem);
            else return addCover(zAxis < 0 ? 2 : 3, coverItem);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, handler.cast());
    }

    @Override
    public void tick() {
        if (inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).isEmpty() && !this.inventory.getStackInSlot(CONVEYOR_ITEM_SLOT).isEmpty()) {
            this.inventory.setStackInSlot(CONVEYOR_ITEM_SLOT, ItemStack.EMPTY);
        }
        for (IConveyorCover cover : this.covers) {
            if (cover.isTicking()) cover.tick(this);
        }
    }

    @Override
    public ConveyorBeltInventory getInventory() {
        return this.inventory;
    }


    public void onEntityRemoved() {


    }

    @Override
    public List<IConveyorCover> getCovers() {
        return covers;
    }

    public List<IConveyorCover> getActiveCovers() {
        return covers.stream().filter(IConveyorCover::isTicking).collect(Collectors.toList());
    }

    @Override
    public boolean addCover(int index, IConveyorCover conveyorCover) {
        if (covers.get(index - 1).isTicking() && conveyorCover.isTicking() || !conveyorCover.isValid(Direction.values()[index], this)) return false;
        conveyorCover.setBelt(this);
        conveyorCover.setDirection(Direction.values()[index]);
        this.covers.set(index - 1, conveyorCover);
        System.out.println(Direction.values()[index]);
        return true;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        regenerateCovers = true;
        ItemStack ret = getInventory().getStackInSlot(CONVEYOR_ITEM_SLOT);
        if (!ret.isEmpty()) {


        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        // im going to try and remove the entities
      //  getInventory().getConveyorEntityStackAtSlot(0).kill();

    }



    @Override
    public void reviveCaps() {
        super.reviveCaps();
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("item", this.inventory.serializeNBT());
        pTag.putInt("count", this.count);
        pTag.putBoolean("stopped", this.stop);

        ListTag list = new ListTag();
        for (int i = 0; i < covers.size(); i++) {
            CompoundTag tag1 = new CompoundTag();
            tag1.putInt("slot", i);
            tag1.put("data", this.covers.get(i).serialize());
            list.add(tag1);
        }
        pTag.put("covers", list);

    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.inventory.deserializeNBT(pTag.getCompound("item"));
        this.count = pTag.getInt("count");
        this.stop = pTag.getBoolean("stopped");
        ListTag covers = pTag.getList("covers", Tag.TAG_COMPOUND);
        for (int i = 0; i < covers.size(); i++) {
            // this is hilarious
            CompoundTag coverTag = covers.getCompound(i);
            IConveyorCover cover = this.covers.get(i);
            cover.deserialize(coverTag.getCompound("data"));

            if (cover.getItem() != null && this.regenerateCovers) {
                this.regenerateCovers = false;
                this.covers.set(i, cover.getItem().getCover());
                this.covers.get(i).deserialize(coverTag.getCompound("data"));
                this.covers.get(i).setBelt(this);
            }
        }
    }

    // this is any belt that is the same horizontal direction
    @Override
    public boolean hasBeltInFront() {

        if (level.getBlockEntity(worldPosition.relative(getBeltDirection())) instanceof IConveyorBelt<?> belt) {
            if (belt.getBelt() == null) return false;
            // ignore if we are a normal belt as we just want to push into regardless
            if (belt.getBelt() instanceof ConveyorBeltEntity) return true;

            if (belt.getBelt() instanceof ConveyorBeltRampEntity rampEntity) {
                boolean goingUp = rampEntity.getVerticalDirection() == Direction.UP;

                if (goingUp) {
                    return belt.getBelt().getBeltDirection().equals(this.getBeltDirection());
                } else {
                    // its correct in direction
                    if (belt.getBelt().getBeltDirection().equals(this.getBeltDirection().getOpposite())) {
                        // check if the actual ramp entity is below this belt if it is then its a valid ramp to push into.
                        return belt.getBelt().getBlockPos().getY() < this.worldPosition.getY();
                    }
                }

            }
        }

        return false;
    }

    public void onEntityCollision(Entity entity, Level level, BlockPos pos) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack ret = getInventory().insertItem(CONVEYOR_ITEM_SLOT, itemEntity.getItem(), false);
            if (ret.isEmpty()) {
                itemEntity.discard();
                if (itemEntity.getItem().getCount() > 1) itemEntity.getItem().shrink(1);
            }
        }
    }

    public boolean isStopped() {
        return stop;
    }

    public void findBeltsAndReCacheThem() {
        if (level != null) {
            for (IConveyorBelt<?> belt : findAllBeltsConnected(this)) {
                belt.getBelt().cacheTraversedBelts();
            }
        }
    }

    protected void cacheTraversedBelts() {
        this.beltsInDirectionFacing = findBeltsItemsCanFollow(this);
        this.count = beltsInDirectionFacing.size();
        syncBlockEntity();
    }

    public HashSet<IConveyorBelt<?>> getBeltsInDirectionFacing() {
        if (beltsInDirectionFacing.isEmpty() && level != null) cacheTraversedBelts();
        return beltsInDirectionFacing;
    }

    public int getBeltCount() {
        if (level.isClientSide) {
            return this.count;
        }
        return getBeltsInDirectionFacing().size();
    }

    private HashSet<IConveyorBelt<?>> findAllBeltsConnected(IConveyorBelt<?> startingNode) {
        Queue<IConveyorBelt<?>> queue = new LinkedList<>();
        HashSet<IConveyorBelt<?>> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        Direction prev = startingNode.getBeltDirection();

        while (!queue.isEmpty()) {

            IConveyorBelt<?> current = queue.poll();
            for (Direction direction : Direction.values()) {
                BlockPos neighbour = current.getBelt().getBlockPos().relative(direction);
                if (level.getBlockEntity(neighbour) instanceof IConveyorBelt<?> belt) {
                    if (!visited.contains(belt)) {
                        visited.add(belt);
                        queue.add(belt);
                    }
                }
            }
        }

        return visited;
    }

    public Direction getBeltDirection() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }



    private HashSet<IConveyorBelt<?>> findBeltsItemsCanFollow(IConveyorBelt<?> startingNode) {
        Queue<IConveyorBelt<?>> queue = new LinkedList<>();
        HashSet<IConveyorBelt<?>> visited = new HashSet<>();

        queue.add(startingNode);
        visited.add(startingNode);

        Direction prev = startingNode.getBeltDirection();

        while (!queue.isEmpty()) {

            IConveyorBelt<?> current = queue.poll();
            BlockPos neighbour = current.getBelt().getBlockPos().relative(current.getBeltDirection());
            if (level.getBlockEntity(neighbour) instanceof IConveyorBelt<?> belt) {
                if (!visited.contains(belt)) {
                    visited.add(belt);
                    queue.add(belt);
                }
            }
        }

        return visited;
    }


}
