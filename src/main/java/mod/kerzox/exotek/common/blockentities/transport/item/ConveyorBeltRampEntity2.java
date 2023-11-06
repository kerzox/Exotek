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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConveyorBeltRampEntity2 extends BasicBlockEntity implements IServerTickable, IClientTickable, IConveyorBeltCollision {

    private Set<ConveyorBeltItemStack> stacksOnConveyor = new HashSet<>();

    private IItemHandler itemHandler = new IItemHandler() {

        public ConveyorBeltItemStack createEntity(ItemStack stack) {

            float x = getBlockPos().getX() + 0.5f;
            float y = getBlockPos().getY() + 0.5f;
            float z = getBlockPos().getZ() + 0.5f;

            ConveyorBeltItemStack conveyorBeltItemStack = new ConveyorBeltItemStack(
                    getLevel(),
                    getBeltDirection(),
                    x, y, z,
                    stack.copy());

            //setDeltaMovementFromConveyorBeltSpeed(conveyorBeltItemStack);
            System.out.println("new entity");
            stack.shrink(conveyorBeltItemStack.getTransportedStack().getCount());
            getLevel().addFreshEntity(conveyorBeltItemStack);
            return conveyorBeltItemStack;
        }

        @Override
        public int getSlots() {
            return scanForItems().size() == 0 ? 1 : scanForItems().size();
        }

        @Override
        public @NotNull ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {

            if (scanForItems().isEmpty()) {
                if (!simulate) createEntity(stack);
                return ItemStack.EMPTY;
            }

            return stack;
        }

        @Override
        public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
            for (ConveyorBeltItemStack conveyorBeltItemStack : scanForItems()) {
                if (!simulate) {
                    ItemStack stack = conveyorBeltItemStack.getTransportedStack().copy();
                    conveyorBeltItemStack.getTransportedStack().shrink(Math.min(stack.getCount(), amount));
                    stop = false;
                    return stack;
                }
                return conveyorBeltItemStack.getTransportedStack().copy();
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return false;
        }
    };

    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);
    private boolean stop;
    private int c_tick;

    private ConveyorBeltItemStack prevStack;

    public ConveyorBeltRampEntity2(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CONVEYOR_BELT_RAMP_ENTITY.get(), pos, state);
    }

    public List<ConveyorBeltItemStack> scanForItems() {
        double x = getBlockPos().getX(), y = getBlockPos().getY(), z = getBlockPos().getZ();

        List<ConveyorBeltItemStack> items = new ArrayList<>();

        items = level.getEntitiesOfClass(ConveyorBeltItemStack.class,
                new AABB(x, y, z, x + 1, y + 10 / 16f, z + 1),
                EntitySelector.ENTITY_STILL_ALIVE);

        return items;

    }

    public boolean isStopped() {
        return this.stop;
    }

    public void setDeltaMovementFromConveyorBeltSpeed(Entity entity) {
        switch (getBeltDirection()) {
            case SOUTH -> entity.setDeltaMovement(0, 0, getSpeed() * (2 / 16f)) ;
            case NORTH -> entity.setDeltaMovement(0, 0, -getSpeed() * (2 / 16f));
            case WEST -> entity.setDeltaMovement(-getSpeed() * (2 / 16f), getSpeed() * (8 / 16f), 0);
            case EAST -> entity.setDeltaMovement(getSpeed() * (2 / 16f), 0, 0);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, handler.cast());
    }

    @Override
    public void tick() {
        if (scanForItems().isEmpty()) stop = false;
    }


    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Items: " + scanForItems().size()));
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public void onEntityCollision(Entity entity, Level level, BlockPos pos) {
        if (!level.isClientSide) {
            if (entity instanceof ItemEntity itemStack) {
                ItemStack ret = this.itemHandler.insertItem(0, itemStack.getItem(), false);
                if (ret.isEmpty()) itemStack.discard();
            }
            if (entity instanceof ConveyorBeltItemStack stack) {
//                if (touchingMiddleOfBelt().contains(stack)) {
//                    if (level.getBlockEntity(pos.relative(getBeltDirection())) instanceof ConveyorBeltEntity belt) {
//                        ItemStack simulate = belt.itemHandler.insertItem(0, stack.getTransportedStack(), true);
//                        if (simulate.isEmpty()) {
//                            stop = false;
//                        } else {
//                            stop = true;
//                        }
//                    } else {
//                        stop = true;
//                    }
//                }
                if (level.getBlockEntity(pos.relative(getBeltDirection())) instanceof ConveyorBeltRampEntity2 belt) {
                    stop = belt.stop;
                } else {

                    stop = true;
                }
                syncBlockEntity();
            }
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (!isStopped()) {
                setDeltaMovementFromConveyorBeltSpeed(livingEntity);
            }
        }
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putBoolean("stopped", this.stop);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.stop = pTag.getBoolean("stopped");
    }

    public int getRenderTick() {
        return c_tick;
    }

    @Override
    public void clientTick() {
        c_tick++;
//        for (ConveyorBeltItemStack beltItemStack : touchingMiddleOfBelt()) {
//            setDeltaMovementFromConveyorBeltSpeed(beltItemStack);
//        }
    }

    public Direction getBeltDirection() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }

    public float getSpeed() {
        return 0.8f;
    }
}
