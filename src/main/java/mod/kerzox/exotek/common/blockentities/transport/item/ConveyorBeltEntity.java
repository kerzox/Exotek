package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.util.IClientTickable;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
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
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConveyorBeltEntity extends BasicBlockEntity implements IServerTickable, IClientTickable, IConveyorBeltCollision, IConveyorBelt<ConveyorBeltEntity> {

    public static final int CONVEYOR_ITEM_SLOT = 0;
    private ConveyorBeltInventory inventory = new ConveyorBeltInventory(this, 1);
    private LazyOptional<ConveyorBeltInventory> handler = LazyOptional.of(() -> inventory);

    private boolean stop;

    public ConveyorBeltEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CONVEYOR_BELT_ENTITY.get(), pos, state);
    }

    public boolean isStopped() {
        return stop;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ITEM_HANDLER.orEmpty(cap, handler.cast());
    }

    @Override
    public void tick() {

    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("item", this.inventory.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.inventory.deserializeNBT(pTag.getCompound("item"));
    }

    private ItemStack getWorkingItem() {
        return this.inventory.getStackInSlot(CONVEYOR_ITEM_SLOT);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && !pPlayer.isShiftKeyDown() && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Server Thread"));
            pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
        }
        if (pLevel.isClientSide && pPlayer.isShiftKeyDown() && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Client Thread"));
            pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, Direction beltDirection, Level level, Vec3 itemStackVectorPos) {
            if (level.getBlockEntity(worldPosition.relative(beltDirection.getOpposite())) instanceof IConveyorBelt<?> belt) {
                if (!belt.getInventory().extractItem(CONVEYOR_ITEM_SLOT, 1, true).isEmpty()) {
                    if (getInventory().conveyorBeltInsert(CONVEYOR_ITEM_SLOT, itemStack)) {
                        belt.getInventory().conveyorBeltExtract(CONVEYOR_ITEM_SLOT);
                        System.out.println("We just inserted a stack into another conveyorbelt");
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }

            }

        return false;
    }

    public void onEntityCollision(Entity entity, Level level, BlockPos pos) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack ret = this.inventory.insertItem(CONVEYOR_ITEM_SLOT, itemEntity.getItem(), false);
            if (ret.isEmpty()) {
                itemEntity.discard();
            }
        }
//        if (entity instanceof ConveyorBeltItemStack conveyorBeltItemStack) {
//            if (conveyorBeltItemStack.getCollisionPos().equals(worldPosition)) {
//                //conveyorBeltItemStack.setDeltaMovement(0, 0, 14/16f * (4/16f));
//            } else {
//                if (level.getBlockEntity(worldPosition.relative(getBeltDirection().getOpposite())) instanceof IConveyorBelt<?> belt) {
//                    if (getInventory().conveyorBeltInsert(CONVEYOR_ITEM_SLOT, belt.getInventory().conveyorBeltExtract(CONVEYOR_ITEM_SLOT))) {
//                        System.out.println("We just inserted a stack into another conveyorbelt");
//                    } else {
//
//                    }
//                }
//            }
//        }
    }

    @Override
    public void clientTick() {

    }

    public Direction getBeltDirection() {
        return getBlockState().getValue(HorizontalDirectionalBlock.FACING);
    }

    public float getSpeed() {
        return 0.5f;
    }

    @Override
    public ConveyorBeltEntity getBelt() {
        return this;
    }

    @Override
    public ConveyorBeltInventory getInventory() {
        return this.inventory;
    }

}
