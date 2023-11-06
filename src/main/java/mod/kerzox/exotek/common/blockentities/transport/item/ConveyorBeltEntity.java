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
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ConveyorBeltEntity extends AbstractConveyorBelt<ConveyorBeltEntity> implements IServerTickable, IClientTickable {
    private ConveyorBeltInventory inventory = new ConveyorBeltInventory(this, 1);
    private LazyOptional<ConveyorBeltInventory> handler = LazyOptional.of(() -> inventory);

    public ConveyorBeltEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.CONVEYOR_BELT_ENTITY.get(), pos, state);
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
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("item", this.inventory.serializeNBT());
        pTag.putInt("count", this.count);
        pTag.putBoolean("stopped", this.stop);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.inventory.deserializeNBT(pTag.getCompound("item"));
        this.count = pTag.getInt("count");
        this.stop = pTag.getBoolean("stopped");
    }

    private ItemStack getWorkingItem() {
        return this.inventory.getStackInSlot(CONVEYOR_ITEM_SLOT);
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
    public void clientTick() {

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
