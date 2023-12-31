package mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic;

import mod.kerzox.exotek.client.gui.menu.multiblock.EnergyBankMenu;
import mod.kerzox.exotek.common.block.EnergyCellBlock;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockException;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.ConfigConsts;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This is a special dynamic multiblock that actually needs a structure,
 * But unlike the normal structure that has hard limits this structure can be a variable height and width.
 */

public class EnergyBankCasingEntity extends DynamicMultiblockEntity implements MenuProvider, IServerTickable {

    private boolean formed = false;
    private Master master = createMaster();
    private CompoundTag tag;
    private boolean ignoreChecks = false;

    private boolean port = false;

    private MultiblockException exception;

    public EnergyBankCasingEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.ENERGY_BANK_CASING.get(), pos, state);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            if (formed && pPlayer.isShiftKeyDown()) {
                setPort(!isPort());
                syncBlockEntity();
                return true;
            }
//            if (exception != null && !formed && pPlayer.getMainHandItem().getItem() != getBlockState().getBlock().asItem()) {
//                pPlayer.sendSystemMessage(Component.literal(exception.getMessage()));
//            }
            //  pPlayer.sendSystemMessage(Component.literal("Master: " + getMaster()));
//            pPlayer.sendSystemMessage(Component.literal("Network: " + getMaster().getEntities().size()));
//            pPlayer.sendSystemMessage(Component.literal("Formed: " + formed));
//            pPlayer.sendSystemMessage(Component.literal("Energy Stored: " + getMaster().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0)));
//            pPlayer.sendSystemMessage(Component.literal("Energy Capacity: " + getMaster().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0)));
//
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public void onLoad() {
        ignoreChecks = formed;
        super.onLoad();
        getMaster().onLoad();
    }

    public Map<Direction, DynamicMultiblockEntity> getConnectedNeighbours() {
        Map<Direction, DynamicMultiblockEntity> dynamicMultiblockEntityMap = new HashMap<>();
        if (getMaster() != null) {
            for (Direction direction : Direction.values()) {
                if (level.getBlockEntity(worldPosition.relative(direction)) instanceof EnergyBankCasingEntity entity) {
                    if (getMaster().getEntities().contains(entity)) dynamicMultiblockEntityMap.put(direction, entity);
                }
            }
        }
        return dynamicMultiblockEntityMap;
    }

    public boolean isPort() {
        return port;
    }

    public void setPort(boolean port) {
        this.port = port;
    }

    @Override
    public Map<Direction, DynamicMultiblockEntity> getNeighbouringEntities() {
        Map<Direction, DynamicMultiblockEntity> dynamicMultiblockEntityMap = new HashMap<>();
        if (getMaster() != null) {
            for (Direction direction : Direction.values()) {
                if (level.getBlockEntity(worldPosition.relative(direction)) instanceof EnergyBankCasingEntity entity) {
                    if (!entity.formed && !formed) {
                        dynamicMultiblockEntityMap.put(direction, entity);
                    } else if (entity.getMaster().equals(getMaster()) || (getMaster().getEntities().contains(entity))) {
                        dynamicMultiblockEntityMap.put(direction, entity);
                    }
                }
            }
        }
        return dynamicMultiblockEntityMap;
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        super.updateFromNetwork(tag);
        if (tag.contains("port")) {
            this.port = tag.getBoolean("port");
        }
    }

    @Override
    protected void write(CompoundTag pTag) {
        super.write(pTag);
        pTag.putBoolean("formed", this.formed);
        pTag.putBoolean("port", this.port);
    }

    @Override
    protected void read(CompoundTag pTag) {
        tag = pTag;
        super.read(pTag);
        this.formed = pTag.getBoolean("formed");
        this.port = pTag.getBoolean("port");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return isFormed() ? (isPort() || side == null) ? super.getCapability(cap, side) : LazyOptional.empty() : LazyOptional.empty();
    }

    @Override
    public void setMaster(Master master) {
        this.master = master;
    }

    public CompoundTag getTag() {
        return tag;
    }

    @Override
    public Master getMaster() {
        return this.master;
    }

    @Override
    public Master createMaster() {
        return new Master(this) {
            private HashSet<BlockPos> cellPositions = new HashSet<>();
            private SidedEnergyHandler energyHandler = new SidedEnergyHandler(0) {

                @Override
                protected void addSides() {
                    addInput(Direction.values());
                    addOutput(Direction.values());
                }

                @Override
                protected void onContentsChanged() {
                    getEntities().forEach(BasicBlockEntity::syncBlockEntity);
                }
            };

            private SidedItemStackHandler itemHandler = new SidedItemStackHandler(2) {

                @Override
                protected void addSides() {


                }

                @Override
                public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                    if (side == null) return getCombinedHandler().cast();
                    return getHandler().cast();
                }

                @Override
                public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                    return stack.getCapability(ForgeCapabilities.ENERGY).isPresent();
                }

                /*
                    Making so the fill slot is the only slot that can be automated.
                    On insert from a get capability call (that has a side that isn't null) we only want to insert at slot 1
                 */

                @Override
                public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
                    if (slot == 1) return super.insertItem(slot, stack, simulate);
                    else return stack;
                }

                /*
                    Only extract on slot 1 if our itemstack has a full energy storage.
                 */

                @Override
                public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                    if (slot == 0) return super.extractItem(slot, amount, simulate);
                    else {
                        ItemStack stack = getStackInSlot(slot);
                        int energy = stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
                        int maxEnergy = stack.getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0);
                        // extract if the stored energy is at capacity or if the capacity is invalid.
                        if (energy == maxEnergy || maxEnergy == 0)
                            return super.internalExtractItem(slot, amount, simulate);
                    }
                    return ItemStack.EMPTY;
                }
            };


            protected int minimumWidth = 3;
            protected int minimumLength = 3;
            protected int minimumHeight = 3;
            protected int maximumWidth = 15;
            protected int maximumLength = 15;
            protected int maximumHeight = 15;

            private boolean checkStructure;
            private BlockPos[] structureMinMax;

            // will have to define a structure.
            /*
                 the structure will be a rectangular box of variable height and width.
                 The insides will matter for the structure so inside will be the cells

             */

            @Override
            protected void onAttachment(DynamicMultiblockEntity attached) {
                checkStructure = true;
            }

            public void tryValidateStructure(BlockPos[] structureMinMax) throws MultiblockException {

                // energy that was stored in the blocks
                int energyFromTag = 0;
                long energyStoredFromCasing = 0;

                cellPositions.clear();
                // first find the casings

                int width = structureMinMax[1].getX() - structureMinMax[0].getX() + 1;
                int height = structureMinMax[1].getY() - structureMinMax[0].getY() + 1;
                int length = structureMinMax[1].getZ() - structureMinMax[0].getZ() + 1;

                if (width < minimumWidth || width > maximumWidth)
                    throw new MultiblockException("Multiblock structure width is wrong: " + width + " should be " + minimumWidth + " - " + maximumWidth, null);
                if (height < minimumHeight || height > maximumHeight)
                    throw new MultiblockException("Multiblock structure height is wrong: " + height + " should be " + minimumHeight + " - " + maximumHeight, null);
                if (length < minimumLength || length > maximumLength)
                    throw new MultiblockException("Multiblock structure length is wrong: " + length + " should be " + minimumLength + " - " + maximumLength, null);

                for (int x = structureMinMax[0].getX(); x <= structureMinMax[1].getX(); x++) {
                    for (int y = structureMinMax[0].getY(); y <= structureMinMax[1].getY(); y++) {
                        for (int z = structureMinMax[0].getZ(); z <= structureMinMax[1].getZ(); z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockEntity entity = level.getBlockEntity(pos);
                            int relativeX = x - (structureMinMax[0].getX() - 1);
                            int relativeY = y - (structureMinMax[0].getY() - 1);
                            int relativeZ = z - (structureMinMax[0].getZ() - 1);

                            // walls, top and bottom
                            if (relativeX == 1 || relativeX == width || relativeY == 1 || relativeY == height || relativeZ == 1 || relativeZ == length) {
                                if (entity instanceof EnergyBankCasingEntity casingEntity) {
                                    long stored = casingEntity.getTag() != null ? casingEntity.getTag().getCompound("manager").getCompound("energy").getCompound("output").getLong("energy") : 0;
                                    if (stored > 0) energyStoredFromCasing = stored;
                                    if (!getEntities().contains(entity))
                                        throw new MultiblockException("You can't share blocks with existing structures " + pos, null);
                                } else {
                                    throw new MultiblockException("The walls, ceiling and floor have to be casings: check at " + pos, null);
                                }
                            } else {


                                BlockState blockState = level.getBlockState(pos);

                                if (blockState.getBlock() instanceof EnergyCellBlock) {
                                    cellPositions.add(pos);
                                } else {
                                    if (!(blockState.getBlock() instanceof AirBlock))
                                        throw new MultiblockException("Block inside the bank is invalid: " + pos, null);
                                }

                            }

                        }
                    }
                }

                if (cellPositions.isEmpty())
                    throw new MultiblockException("Needs at least one energy cell in the bank", null);

                setMultiblockStatus(true);

                calculateEnergyStorage(energyStoredFromCasing);

            }

            private void calculateEnergyStorage(long energyStoredFromCasing) {
                int totalSize = cellPositions.size();
                if (energyHandler != null) {
                    energyHandler.setCapacity(0);
                    for (BlockPos cellPosition : cellPositions) {
                        BlockState blockState = level.getBlockState(cellPosition);
                        if (blockState.getBlock() instanceof EnergyCellBlock cell) {
                            energyHandler.addCapacity(cell.getCapacity());
                        }
                    }
                    energyHandler.setEnergy(energyStoredFromCasing);
                    energyHandler.setExtract(energyHandler.getLargeMaxEnergyStored() / totalSize);
                    energyHandler.setReceive(energyHandler.getLargeMaxEnergyStored() / totalSize);
                    energyHandler.addOutput(Direction.values());
                    energyHandler.addInput(Direction.values());
                }

            }

            @Override
            protected boolean preDetachment(DynamicMultiblockEntity toDetach) {
                setMultiblockStatus(false);
                return super.preDetachment(toDetach);
            }

            private void setMultiblockStatus(boolean b) {
                for (DynamicMultiblockEntity entity : this.getEntities()) {
                    if (entity instanceof EnergyBankCasingEntity entity1) {
                        entity1.formed = b;
                        entity.syncBlockEntity();
                    }
                }
            }

            @Override
            protected boolean preAttachment(DynamicMultiblockEntity toAttach) {

                if (toAttach instanceof EnergyBankCasingEntity entity1) {
                    if (ignoreChecks) return true;
                }
                return true;
            }

            @Override
            public boolean isValidEntity(DynamicMultiblockEntity entity) {
                return entity instanceof EnergyBankCasingEntity;
            }

            @Override
            public void tick() {

                if (checkStructure) {
                    if (!level.isClientSide) {
                        try {
                            // try to use previous calculated min max to help prevent lag
                            if (structureMinMax == null) structureMinMax = calculateMinMax();
                            tryValidateStructure(structureMinMax);
                        } catch (MultiblockException e) {
                            exception = e;
                            structureMinMax = null;
                            if (formed) {
                                setMultiblockStatus(false);
                            }
                        }

                        if (formed) {
                            exception = null;
                        }
                    }
                    checkStructure = false;
                }

                ItemStack draining = itemHandler.getStackInSlot(0);
                draining.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {

                    if (cap.canExtract() && this.energyHandler.getLargeEnergyStored() < this.energyHandler.getLargeMaxEnergyStored()) {
                        int amount = cap.extractEnergy(this.energyHandler.getInputWrapper().receiveEnergy(cap.getEnergyStored(), true), false);
                        energyHandler.addEnergy(amount);
                    }

                });

                ItemStack charging = itemHandler.getStackInSlot(1);
                charging.getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {

                    if (cap.canReceive()) {
                        int received = cap.receiveEnergy(this.energyHandler.getOutputWrapper().extractEnergy(this.energyHandler.getEnergy(), true), false);
                        energyHandler.consumeEnergy(received);
                    }

                });

                for (DynamicMultiblockEntity entity : getEntities()) {
                    if (entity instanceof EnergyBankCasingEntity casingEntity) {
                        if (casingEntity.isPort()) {
                            for (Direction direction : Direction.values()) {
                                BlockEntity blockEntity = level.getBlockEntity(casingEntity.getBlockPos().relative(direction));
                                if (blockEntity != null) {
                                    if (blockEntity instanceof DynamicMultiblockEntity entity1) if (getEntities().contains(entity1)) continue;
                                    blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite()).ifPresent(cap -> {

                                        if (cap.canReceive()) {
                                            int amount = Math.min((int) Math.min(energyHandler.getLargeEnergyStored(), energyHandler.getExtractLimit()), Integer.MAX_VALUE);

                                            // the amount we actually transfered
                                            int received = cap.receiveEnergy(amount, false);
                                            energyHandler.consumeEnergy(received);
                                        }

                                    });
                                }
                            }
                        }
                    }
                }

            }

            @Override
            public CompoundTag write() {
                CompoundTag tag = super.write();
                tag.put("energy", this.energyHandler.serialize());
                tag.put("itemHandler", this.itemHandler.serializeNBT());
                return tag;
            }

            @Override
            public void read(CompoundTag pTag) {
                energyHandler.deserialize(pTag.getCompound("energy"));
                itemHandler.deserializeNBT(pTag.getCompound("itemHandler"));
                super.read(pTag);
            }

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                if (cap == ForgeCapabilities.ENERGY) {
                    return energyHandler.getHandler(side);
                }
                if (cap == ForgeCapabilities.ITEM_HANDLER) {
                    return itemHandler.getCapability(cap, side);
                }
                return LazyOptional.empty();
            }
        };
    }

    public boolean isFormed() {
        return this.formed;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Energy Bank");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        if (formed) return new EnergyBankMenu(p_39954_, p_39955_, p_39956_, this);
        else return null;
    }

    @Override
    public void tick() {
        if (getMaster() != null && getMaster().getTile() == this) getMaster().tick();
    }
}
