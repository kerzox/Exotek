package mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic;

import mod.kerzox.exotek.client.gui.menu.multiblock.EnergyBankMenu;
import mod.kerzox.exotek.common.block.EnergyCellBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockException;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
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
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * This is a special dynamic multiblock that actually needs a structure,
 * But unlike the normal structure that has hard limits this structure can be a variable height and width.
 */

public class EnergyBankCasingEntity extends DynamicMultiblockEntity implements MenuProvider {

    private boolean formed = false;
    private Master master = createMaster();
    private CompoundTag tag;
    private boolean ignoreChecks = false;

    public EnergyBankCasingEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.ENERGY_BANK_CASING.get(), pos, state);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
//
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

    @Override
    public Map<Direction, DynamicMultiblockEntity> getNeighbouringEntities() {
        Map<Direction, DynamicMultiblockEntity> dynamicMultiblockEntityMap = new HashMap<>();
        if (getMaster() != null) {
            for (Direction direction : Direction.values()) {
                if (level.getBlockEntity(worldPosition.relative(direction)) instanceof EnergyBankCasingEntity entity) {
                    if (!entity.formed && !formed) {
                        dynamicMultiblockEntityMap.put(direction, entity);
                    }
                    else if (entity.getMaster().equals(getMaster()) || (getMaster().getEntities().contains(entity))) {
                        dynamicMultiblockEntityMap.put(direction, entity);
                    }
                }
            }
        }
        return dynamicMultiblockEntityMap;
    }

    @Override
    protected void write(CompoundTag pTag) {
        super.write(pTag);
        pTag.putBoolean("formed", this.formed);
    }

    @Override
    protected void read(CompoundTag pTag) {
        tag = pTag;
        super.read(pTag);
        this.formed = pTag.getBoolean("formed");
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return formed ? super.getCapability(cap, side) : LazyOptional.empty();
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
    public Master createMaster() {
        return new Master(this) {
            private HashSet<BlockPos> cellPositions = new HashSet<>();
            private SidedEnergyHandler energyHandler = new SidedEnergyHandler(0);

            protected int minimumWidth = 3;
            protected int minimumLength = 3;
            protected int minimumHeight = 3;
            protected int maximumWidth = 15;
            protected int maximumLength = 15;
            protected int maximumHeight = 15;

            // will have to define a structure.
            /*
                 the structure will be a rectangular box of variable height and width.
                 The insides will matter for the structure so inside will be the cells

             */

            @Override
            protected void onAttachment(DynamicMultiblockEntity attached) {
                // do form code
                if (attached instanceof EnergyBankCasingEntity entity) {
                    if (!level.isClientSide) {
                        try {
                            tryValidateStructure();
                        } catch (MultiblockException e) {
                            System.out.println(e.getMessage());

                            setMultiblockStatus(false);
                        }

                        if (formed) {
                            calculateEnergyStorage();
                        }
                    }

                }
            }

            public void tryValidateStructure() throws MultiblockException {

                BlockPos[] twoCorners = getMaster().calculateMinMax();
                cellPositions.clear();
                // first find the casings

                int width = twoCorners[1].getX() - twoCorners[0].getX() + 1;
                int height = twoCorners[1].getY() - twoCorners[0].getY() + 1;
                int length = twoCorners[1].getZ() - twoCorners[0].getZ() + 1;

                if (width < minimumWidth || width > maximumWidth) throw new MultiblockException("Multiblock structure width is wrong: " + width + " should be " + minimumWidth + " - " + maximumWidth, null);
                if (height < minimumHeight || height > maximumHeight) throw new MultiblockException("Multiblock structure height is wrong: " + height + " should be " + minimumHeight + " - " + maximumHeight, null);
                if (length < minimumLength || length > maximumLength) throw new MultiblockException("Multiblock structure length is wrong: " + length + " should be " + minimumLength + " - " + maximumLength, null);

                for (int x = twoCorners[0].getX(); x <= twoCorners[1].getX(); x++) {
                    for (int y = twoCorners[0].getY(); y <= twoCorners[1].getY(); y++) {
                        for (int z = twoCorners[0].getZ(); z <= twoCorners[1].getZ(); z++) {
                            BlockPos pos = new BlockPos(x, y, z);
                            BlockEntity entity = level.getBlockEntity(pos);
                            int relativeX = x - (twoCorners[0].getX() - 1);
                            int relativeY = y - (twoCorners[0].getY() - 1);
                            int relativeZ = z - (twoCorners[0].getZ() - 1);

                            // walls, top and bottom
                            if (relativeX == 1 || relativeX == width || relativeY == 1 || relativeY == height || relativeZ == 1 || relativeZ == length) {
                                if (entity instanceof EnergyBankCasingEntity) {
                                    if (!getEntities().contains(entity)) throw new MultiblockException("The walls, ceiling and floor have to be casings: check at " + pos, null);
                                } else {
                                    throw new MultiblockException("The walls, ceiling and floor have to be casings: check at " + pos, null);
                                }
                            } else {


                                BlockState blockState = level.getBlockState(pos);

                                if (blockState.getBlock() instanceof EnergyCellBlock) {
                                    cellPositions.add(pos);
                                } else {
                                    if (!(blockState.getBlock() instanceof AirBlock)) throw new MultiblockException("Block inside the bank is invalid: " + pos,null);
                                }

                            }

                        }
                    }
                }

                if (cellPositions.isEmpty()) throw new MultiblockException("Needs at least one energy cell in the bank", null);

                setMultiblockStatus(true);

            }

            private void calculateEnergyStorage() {
                int totalSize = cellPositions.size();
                if (energyHandler != null) {
                    energyHandler.setCapacity(0);
                    for (BlockPos cellPosition : cellPositions) {
                        BlockState blockState = level.getBlockState(cellPosition);
                        if (blockState.getBlock() instanceof EnergyCellBlock cell) {
                            energyHandler.addCapacity(cell.getTiers().getTransfer() * 1000);
                        }
                    }
                    energyHandler.setExtract(energyHandler.getMaxEnergy());
                    energyHandler.setReceive(energyHandler.getMaxEnergy());
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
                    return !entity1.formed;
                }

                return true;
            }

            @Override
            public boolean isValidEntity(DynamicMultiblockEntity entity) {
                return entity instanceof EnergyBankCasingEntity;
            }

            @Override
            public void tick() {

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

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.ENERGY.orEmpty(cap, energyHandler.getHandler(side));
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
}
