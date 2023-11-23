package mod.kerzox.exotek.common.blockentities.machine.generator;

import mod.kerzox.exotek.Config;
import mod.kerzox.exotek.client.gui.menu.BurnableGeneratorMenu;
import mod.kerzox.exotek.common.blockentities.ContainerisedBlockEntity;
import mod.kerzox.exotek.common.blockentities.MachineBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class BurnableGeneratorEntity extends MachineBlockEntity implements IServerTickable, MenuProvider {

    private SidedEnergyHandler energyHandler = new SidedEnergyHandler(128000) {
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };
    private ItemStackInventory itemStackInventory = new ItemStackInventory(1, 1);
    private final int feTick = Config.BURNABLE_GENERATOR_FE_GEN_PER_TICK;
    private int burnTime;

    public BurnableGeneratorEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.BURNABLE_GENERATOR_ENTITY.get(), pos, state);
        addCapabilities(energyHandler, itemStackInventory);
    }

    @Override
    public void tick() {
        ItemStack item = itemStackInventory.getInputHandler().getStackInSlot(0);
        if (energyHandler.isFull()) return;
        if (burnTime > 0) {
            energyHandler.addEnergy(feTick);
            burnTime--;
        } else {
            ItemStack copied = item.copy();
            if (!copied.isEmpty()) {
                burnTime = ForgeHooks.getBurnTime(copied, RecipeType.SMELTING);
                if (burnTime > 0) item.shrink(1);
            }
        }
        AtomicInteger currentEnergy = new AtomicInteger(this.energyHandler.getEnergy());
        if (currentEnergy.get() > 0) {
            for (Direction direction : Direction.values()) {
                if (this.energyHandler.getOutputs().contains(direction)) {
                    pushToBlockEntities(currentEnergy, direction);
                    pushToLevelInventories(currentEnergy, this.getBlockPos().relative(direction));
                }
            }
        }
    }

    private void pushToBlockEntities(AtomicInteger currentEnergy, Direction direction) {
        if (level.getBlockEntity(worldPosition.relative(direction)) != null) {
            level.getBlockEntity(worldPosition.relative(direction)).getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(cap -> {
                if (cap.canReceive()) {
                    int received = cap.receiveEnergy(Math.min(currentEnergy.get(), 500), false);
                    currentEnergy.addAndGet(-received);
                    this.energyHandler.consumeEnergy(received);
                }
            });
        }
    }

    private void pushToLevelInventories(AtomicInteger currentEnergy, BlockPos position) {
        level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {
            if (cap instanceof LevelEnergyNetwork network) {
                EnergySubNetwork single = network.getNetworkFromPosition(position);
                if (single != null) {
                    int received = single.getInternalStorage().receiveEnergy(Math.min(currentEnergy.get(), 500), false);
                    currentEnergy.addAndGet(-received);
                    this.energyHandler.consumeEnergy(received);
                }
            }
        });
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Burnable Generator");
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        tag.put("energyHandler", this.energyHandler.serialize());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new BurnableGeneratorMenu(p_39954_, p_39955_, p_39956_, this);
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemStackInventory.invalidate();
        super.invalidateCaps();
    }
}
