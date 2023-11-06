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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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

import static mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity.CONVEYOR_ITEM_SLOT;

public class ConveyorBeltRampEntity extends AbstractConveyorBelt<ConveyorBeltRampEntity> implements IServerTickable, IClientTickable {

    private ConveyorBeltInventory inventory = new ConveyorBeltInventory(this, 1);
    private LazyOptional<ConveyorBeltInventory> handler = LazyOptional.of(() -> inventory);
    private HashSet<IConveyorBelt<?>> beltsInDirectionFacing = new HashSet<>();

    private int count = 0;
    private boolean stop;

    public ConveyorBeltRampEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CONVEYOR_BELT_RAMP_ENTITY.get(), pos, state);
    }

    @Override
    public ConveyorBeltRampEntity getBelt() {
        return this;
    }

    @Override
    public ConveyorBeltInventory getInventory() {
        return inventory;
    }

    public boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, Direction beltDirection, Level level, Vec3 itemStackVectorPos) {
        if (level.getBlockEntity(worldPosition.relative(beltDirection.getOpposite())) instanceof IConveyorBelt<?> belt) {
            if (!belt.getInventory().extractItem(CONVEYOR_ITEM_SLOT, 1, true).isEmpty()) {
                if (getInventory().conveyorBeltInsert(CONVEYOR_ITEM_SLOT, itemStack)) {
                    belt.getInventory().conveyorBeltExtract(CONVEYOR_ITEM_SLOT);
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }

        }

        return false;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && !pPlayer.isShiftKeyDown() && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Server Thread"));
            pPlayer.sendSystemMessage(Component.literal("Belts: " + beltsInDirectionFacing.size()));
            pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
        }
        if (pLevel.isClientSide && pPlayer.isShiftKeyDown() && pHand == InteractionHand.MAIN_HAND) {
            pPlayer.sendSystemMessage(Component.literal("Client Thread"));
            pPlayer.sendSystemMessage(Component.literal("Belts: " + getBeltCount()));
            pPlayer.sendSystemMessage(Component.literal("Items: " + inventory.getStackInSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entities: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT)));
            pPlayer.sendSystemMessage(Component.literal("Item Entity Item: " + inventory.getConveyorEntityStackAtSlot(CONVEYOR_ITEM_SLOT).getTransportedStack()));
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public void onEntityCollision(Entity entity, Level level, BlockPos pos) {
        if (entity instanceof ItemEntity itemEntity) {
            ItemStack ret = this.inventory.insertItem(CONVEYOR_ITEM_SLOT, itemEntity.getItem(), false);
            if (ret.isEmpty()) {
                itemEntity.discard();
            }
        }
    }

    @Override
    public void clientTick() {

    }

    @Override
    public void tick() {

    }
}
