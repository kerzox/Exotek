package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConveyorBeltEntity3 extends BasicBlockEntity implements IServerTickable, IClientTickable, IConveyorBeltCollision, IConveyorBelt<ConveyorBeltEntity> {

    public static final int CONVEYOR_ITEM_SLOT = 0;
    private ConveyorBeltInventory inventory = new ConveyorBeltInventory(this, 1);
    private LazyOptional<ConveyorBeltInventory> handler = LazyOptional.of(() -> inventory);
    private boolean movingItem = false;
    private boolean stopped = false;

    private int travelTicks = 20;
    private int ticksLeft;
    private int tick;

    private Vec3 positionToMoveTo;

    private Vec3 originalPositionOfEntity;
    private double delta;

    public ConveyorBeltEntity3(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CONVEYOR_BELT_ENTITY.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, handler.cast());
    }

    public void setOriginalPositionOfEntity(Vec3 originalPositionOfEntity) {
        this.originalPositionOfEntity = originalPositionOfEntity;
    }

    public List<ConveyorBeltItemStack> scanForItems() {
        double x = getBlockPos().getX(), y = getBlockPos().getY(), z = getBlockPos().getZ();

        List<ConveyorBeltItemStack> items = new ArrayList<>();

        items = level.getEntitiesOfClass(ConveyorBeltItemStack.class,
                new AABB(x, y, z, x + 1, y + 10 / 16f, z + 1),
                EntitySelector.ENTITY_STILL_ALIVE);

        return items;

    }

    @Override
    public void onLoad() {
        super.onLoad();


    }

    @Override
    public void tick() {
        if (getWorkingItem().isEmpty()) return;
        if (!isCurrentlyMovingItem()) startMovingItem();
        else {
            BlockEntity be = level.getBlockEntity(worldPosition.relative(getBeltDirection()));

            // check for valid output (an actually inventory), if we don't have one then we need to stop the belt;
            AtomicBoolean foundInventory = new AtomicBoolean(false);
            if (be != null) {
                be.getCapability(ForgeCapabilities.ITEM_HANDLER, getBeltDirection().getOpposite()).ifPresent(cap -> {
                    ItemStack simulate = ItemHandlerHelper.insertItem(cap, getWorkingItem(), true);
                    if (!simulate.isEmpty()) {
                        System.out.println("Output Inventory is full stop this conveyor belt");
                        // stop this conveyor belt;
                        foundInventory.set(false);
                    } else {
                        foundInventory.set(true);
                    }
                });
            }

            if (ticksLeft > 0) {
                // this is just to move the item into the middle of the conveyor belt;
                if ((be == null || !foundInventory.get()) && ticksLeft <= travelTicks / 2) {
                    // we sit here until we find a valid output;
                    if (!stopped) System.out.println("Stopping conveyor belt as we have no valid output but we have reached the middle of the animation");
                    stopped = true;
//                    ConveyorBeltItemStack conveyorBeltItemStack = inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT);
//                    if (!conveyorBeltItemStack.isEmpty()) {
//                        delta = 0;
//                    }
                }
                else {
//                    ConveyorBeltItemStack conveyorBeltItemStack = inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT);
//                    if (!conveyorBeltItemStack.isEmpty()) {
//                        double posB = worldPosition.getZ() + 1;
//                        double posA = worldPosition.getZ();
//                        double d = (posB - posA) / (travelTicks);
//                        delta = d;
//                    }
                    ticksLeft--;
                    System.out.println(ticksLeft);
                    syncBlockEntity();
                }
            } else {
                finishMovingItem();
            }

            syncBlockEntity();
        }
        if (!stopped) tick++;
    }

    public Vec3 getPositionVec() {
        return positionToMoveTo;
    }

    public void setPositionToMoveTo(Vec3 delta) {
        this.positionToMoveTo = delta;
    }

    public double getDelta() {
        return delta;
    }

    private void setConveyorStackToMove(ConveyorBeltItemStack conveyorBeltItemStack) {
        if (originalPositionOfEntity == null) originalPositionOfEntity = conveyorBeltItemStack.position();
        double posB = originalPositionOfEntity.z + 1;
        double posA = originalPositionOfEntity.z;
        double deltaX = Math.round((posB - posA) / (travelTicks));
        conveyorBeltItemStack.setDeltaMovement(0, 0, deltaX);

    }

    protected void finishMovingItem() {

        this.movingItem = false;

//        this.delta = 0;

        if (level.getBlockEntity(worldPosition.relative(getBeltDirection())) instanceof IConveyorBelt<?> conveyorBelt) {
            if (conveyorBelt.getInventory().conveyorBeltInsert(CONVEYOR_ITEM_SLOT, inventory.conveyorBeltExtract(CONVEYOR_ITEM_SLOT))) {
                getWorkingItem().shrink(1);
                double posB = worldPosition.getZ() + 1;
                double posA = worldPosition.getZ();
                double d = (posB - posA) / (travelTicks);
                delta = d;

            }
        }

        syncBlockEntity();
    }

    public void setDelta(double delta) {
        this.delta = delta;
    }

    protected void startMovingItem() {

        // ok we are now in the process of moving an item
        this.movingItem = true;

        // now also set the current tick to be the beginning;
        tick = 0;
        ticksLeft = travelTicks;

        syncBlockEntity();

    }

    public boolean isStopped() {
        return stopped;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("tick", this.tick);
        pTag.putInt("ticksLeft", this.ticksLeft);
        pTag.putBoolean("movingItem", this.movingItem);
        pTag.put("itemHandler", this.inventory.serializeNBT());
        pTag.putDouble("delta", this.delta);
        CompoundTag tag = new CompoundTag();
        if (positionToMoveTo != null) {
            tag.putDouble("x", this.positionToMoveTo.x);
            tag.putDouble("y", this.positionToMoveTo.y);
            tag.putDouble("z", this.positionToMoveTo.z);
            pTag.put("vec3", tag);
        }
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.tick = pTag.getInt("tick");
        this.ticksLeft = pTag.getInt("ticksLeft");
        this.movingItem = pTag.getBoolean("movingItem");
        this.inventory.deserializeNBT(pTag.getCompound("itemHandler"));
        this.delta = pTag.getDouble("delta");
        if (pTag.contains("vec3")) {
            CompoundTag vec3 = pTag.getCompound("vec3");
            this.positionToMoveTo = new Vec3(vec3.getDouble("x"), vec3.getDouble("y"), vec3.getDouble("z"));
        }
    }

    public static float lerp(float startValue, float endValue, float t) {
        t = Math.min(1.0f, Math.max(0.0f, t)); // Ensure t is within [0, 1]
        return (int) (startValue + (endValue - startValue) * t);
    }

    public boolean isCurrentlyMovingItem() {
        return movingItem;
    }

    private ItemStack getWorkingItem() {
        return this.inventory.getStackInSlot(CONVEYOR_ITEM_SLOT);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public void onEntityCollision(Entity entity, Level level, BlockPos pos) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack ret = this.inventory.insertItem(CONVEYOR_ITEM_SLOT, itemEntity.getItem(), false);
            if (ret.isEmpty()) {
                itemEntity.discard();
                setOriginalPositionOfEntity(this.inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).position());
            }
        }
        if (entity instanceof ConveyorBeltItemStack conveyorBeltItemStack) {
            if (conveyorBeltItemStack.getCollisionPos().equals(this)) {

            }
        }
    }


    public void setDeltaMovementFromConveyorBeltSpeed(Entity entity) {
        switch (getBeltDirection()) {
            case SOUTH -> entity.setDeltaMovement(0, 0, getSpeed() * (2 / 16f)) ;
            case NORTH -> entity.setDeltaMovement(0, 0, -getSpeed() * (2 / 16f));
            case WEST -> entity.setDeltaMovement(-getSpeed() * (2 / 16f), 0, 0);
            case EAST -> entity.setDeltaMovement(getSpeed() * (2 / 16f), 0, 0);
        }
    }

    @Override
    public void clientTick() {

    }

    public Direction getBeltDirection() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }

    public float getSpeed() {
        return 0.8f;
    }

    @Override
    public ConveyorBeltEntity getBelt() {
        return null;
    }

    @Override
    public ConveyorBeltInventory getInventory() {
        return this.inventory;
    }

}
