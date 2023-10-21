package mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic;

import mod.kerzox.exotek.client.gui.menu.SolarPanelMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.DynamicMultiblockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class SolarPanelEntity extends DynamicMultiblockEntity implements IServerTickable, MenuProvider, ICustomCollisionShape {

    private Master master = createMaster();

    public SolarPanelEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.SOLAR_PANEL_ENTITY.get(), pos, state);
    }

    @Override
    public void setMaster(Master master) {
        this.master = master;
    }

    @Override
    public Master getMaster() {
        return this.master;
    }

    @Override
    public void tick() {
        if (getMaster() != null) {
            if (getMaster().getTile() == this) {
                getMaster().tick();
            }
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (getMaster() != null && !pLevel.isClientSide && pPlayer.isShiftKeyDown()) {
            pPlayer.sendSystemMessage(Component.literal("Master Interface: " + getMaster()));
            pPlayer.sendSystemMessage(Component.literal("Master Tile: " + getMaster().getTile()));
            pPlayer.sendSystemMessage(Component.literal("Master Tile Position: " + getMaster().getTile().getBlockPos().toShortString()));
            pPlayer.sendSystemMessage(Component.literal("Network Size: " + getMaster().getEntities().size()));
            pPlayer.sendSystemMessage(Component.literal("Energy Stored: " + getMaster().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)));
            pPlayer.sendSystemMessage(Component.literal("Energy Capacity: " + getMaster().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0)));
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Solar Panel");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new SolarPanelMenu(p_39954_, p_39955_, p_39956_, this);
    }

    @Override
    public Master createMaster() {
        return new Master(this) {

            private int capacity = 8000;
            private SidedEnergyHandler energyHandler = new SidedEnergyHandler(capacity, capacity, Direction.DOWN);
            private Master prev = this;

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.ENERGY.orEmpty(cap, energyHandler.getHandler(side));
            }

            @Override
            protected boolean preAttachment(DynamicMultiblockEntity toAttach) {

                Master old = toAttach.getMaster();

                if (prev != null) {
                    if (old != null && prev != old && this != old) {
                        int amount = old.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
                        energyHandler.addEnergy(amount);
                    }
                }

                prev = old;

                return true;

            }

            @Override
            protected void onAttachment(DynamicMultiblockEntity attached) {
                if (energyHandler != null) {
                    int totalSize = getEntities().size();
                    energyHandler.setCapacity(totalSize * capacity);
                    energyHandler.setExtract(totalSize * capacity);
                }
            //    attached.syncBlockEntity();
            }

            @Override
            protected boolean preDetachment(DynamicMultiblockEntity toDetach) {
                return true;
            }

            @Override
            public void tick() {
                if (level == null) return;

                for (DynamicMultiblockEntity entity : getEntities()) {

                    if (entity instanceof SolarPanelEntity solarPanelEntity) {
                        boolean sky = level.canSeeSky(solarPanelEntity.getBlockPos().above());

                        if (sky && level.isDay()) {
                            energyHandler.addEnergy(5);
                          //  entity.syncBlockEntity();
                        }

                        AtomicInteger currentEnergy = new AtomicInteger(this.energyHandler.getEnergy());

                        if (currentEnergy.get() > 0) {
                            pushToBottom(currentEnergy, solarPanelEntity);
                        }
                    }

                }

            }

            public void pushToBottom(AtomicInteger currentEnergy, SolarPanelEntity solarPanelEntity) {
                BlockEntity entity = level.getBlockEntity(solarPanelEntity.getBlockPos().relative(Direction.DOWN));
                if (entity != null) {
                    entity.getCapability(ForgeCapabilities.ENERGY, Direction.UP).ifPresent(cap -> {
                        if (cap.canReceive()) {
                            int received = cap.receiveEnergy(Math.min(currentEnergy.get(), 500), false);
                            currentEnergy.addAndGet(-received);
                            this.energyHandler.consumeEnergy(received);
                        }
                    });
                }
            }

            @Override
            public CompoundTag write() {
                CompoundTag tag = super.write();
                tag.put("energy", this.energyHandler.serialize());
                return tag;
            }

            @Override
            public void read(CompoundTag pTag) {
                energyHandler.deserialize(pTag.getCompound("energy"));
                super.read(pTag);
            }
        };
    }

    @Override
    public VoxelShape getShape() {
        return Block.box(0, 0, 0, 16, 3, 16);
    }
}
