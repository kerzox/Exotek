package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class EnergyCableBlock extends DirectionConnectableBlock implements EntityBlock {

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new EnergyCableEntity(p_153215_, p_153216_);
    }

    private HashMap<Direction, VoxelShape> cache = new HashMap<>();
    private VoxelShape CORE = Block.box(5, 5, 5, 11, 11, 11);
    private VoxelShape[] allValidSides = new VoxelShape[]{
            Shapes.or(Block.box(5, 0, 5, 11, 11, 11)), // down 0
            Shapes.or(Block.box(5, 5, 5, 11, 11+5, 11)), // up 1
            Shapes.or(Block.box(5, 5, 0, 11, 11, 5)), // north 2
            Shapes.or(Block.box(5, 5, 5, 11, 11, 11+5)), // south 3
            Shapes.or(Block.box(0, 5, 5, 11, 11, 11)), // west 4
            Shapes.or(Block.box(5, 5, 5, 11+5, 11, 11)), // east 5
    };

    private void initializeShapeCache() {
        for (Direction direction : Direction.values()) {
            VoxelShape combinedShape = CORE;
            if (direction == Direction.UP) {
                combinedShape = Shapes.or(combinedShape, allValidSides[1]);
            }
            if (direction == Direction.DOWN) {
                combinedShape = Shapes.or(combinedShape, allValidSides[0]);
            }
            if (direction == Direction.WEST) {
                combinedShape = Shapes.or(combinedShape, allValidSides[4]);
            }
            if (direction == Direction.EAST) {
                combinedShape = Shapes.or(combinedShape, allValidSides[5]);
            }
            if (direction == Direction.NORTH) {
                combinedShape = Shapes.or(combinedShape, allValidSides[2]);
            }
            if (direction == Direction.SOUTH) {
                combinedShape = Shapes.or(combinedShape, allValidSides[3]);
            }
            cache.put(direction, combinedShape);
        }
    }

    public EnergyCableBlock(Properties p_49795_) {
        super(p_49795_);
//        this.registerDefaultState(this.stateDefinition.any()
//                .setValue(NORTH, Connection.NONE)
//                .setValue(EAST, Connection.NONE)
//                .setValue(SOUTH, Connection.NONE)
//                .setValue(WEST, Connection.NONE)
//                .setValue(UP, Connection.NONE)
//                .setValue(DOWN, Connection.NONE));
        initializeShapeCache();
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }


    @Override
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        if (pLevel.getBlockEntity(pPos) instanceof EnergyCableEntity pipe) {
            VoxelShape combinedShape = CORE;
            for (Direction direction : pipe.getConnectionsAsDirections()) {
                if (direction == Direction.UP) {
                    combinedShape = Shapes.or(combinedShape, allValidSides[1]);
                }
                if (direction == Direction.DOWN) {
                    combinedShape = Shapes.or(combinedShape, allValidSides[0]);
                }
                if (direction == Direction.WEST) {
                    combinedShape = Shapes.or(combinedShape, allValidSides[4]);
                }
                if (direction == Direction.EAST) {
                    combinedShape = Shapes.or(combinedShape, allValidSides[5]);
                }
                if (direction == Direction.NORTH) {
                    combinedShape = Shapes.or(combinedShape, allValidSides[2]);
                }
                if (direction == Direction.SOUTH) {
                    combinedShape = Shapes.or(combinedShape, allValidSides[3]);
                }
            }

            return combinedShape;
        }
        return CORE;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_ITEM.get()
                || pPlayer.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_2_ITEM.get()
                || pPlayer.getMainHandItem().getItem() == Registry.Items.ENERGY_CABLE_3_ITEM.get()
                || pPlayer.getMainHandItem().getItem() == Registry.Items.WRENCH_ITEM.get()) {
            return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
        }
        if (pLevel.getBlockEntity(pPos) instanceof MenuProvider menu) {
            if (pLevel.isClientSide) return InteractionResult.SUCCESS;
            NetworkHooks.openScreen((ServerPlayer) pPlayer, menu, pPos);
            return InteractionResult.SUCCESS;
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    public void onPlace(BlockState state, Level pLevel, BlockPos pPos, BlockState pState, boolean moving) {

        if (pLevel.getBlockEntity(pPos) instanceof EnergyCableEntity notifiedPipe) {
            for (Direction direction : Direction.values()) {
                pLevel.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability1 -> {
                    if (capability1 instanceof LevelEnergyNetwork network) {
                        EnergySubNetwork network1 = network.getNetworkFromPosition(pPos.relative(direction));
                        if (network1 != null) {
                            notifiedPipe.addVisualConnection(direction);
                        }
                    }
                });
                BlockEntity blockEntity = pLevel.getBlockEntity(pPos.relative(direction));
                if (blockEntity != null) {
                    LazyOptional<IEnergyStorage> capability = blockEntity.getCapability(ForgeCapabilities.ENERGY);
                    if (capability.isPresent()) notifiedPipe.addVisualConnection(direction);
                    if (blockEntity instanceof EnergyCableEntity energyCableEntity) {
                        energyCableEntity.addVisualConnection(direction.getOpposite());
                        notifiedPipe.addVisualConnection(direction);
                    }
                }
            }
        }

    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
        if (pLevel.getBlockEntity(pPos) instanceof EnergyCableEntity notifiedPipe) {
            BlockEntity blockEntity = pLevel.getBlockEntity(pFromPos);
            BlockPos pos = pFromPos.subtract(pPos);
            Direction facing = Direction.fromDelta(pos.getX(), pos.getY(), pos.getZ());
            if (blockEntity != null) {
                BlockPos pos1 = pPos.subtract(pFromPos);
                LazyOptional<IEnergyStorage> capability = blockEntity.getCapability(ForgeCapabilities.ENERGY);

                if (blockEntity instanceof EnergyCableEntity energyCableEntity) {
                    energyCableEntity.addVisualConnection(facing.getOpposite());
                    notifiedPipe.addVisualConnection(facing);
                } else {
                    if (capability.isPresent()) {
                        notifiedPipe.addVisualConnection(facing);
                        capability.addListener(l -> notifiedPipe.removeVisualConnection(facing));
                    }
                }

                pLevel.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability1 -> {
                    if (capability1 instanceof LevelEnergyNetwork network) {
                        EnergySubNetwork network1 = network.getNetworkFromPosition(pFromPos);
                        if (network1 != null) {
                            notifiedPipe.addVisualConnection(facing);
                        }
                    }
                });

            } else {
                if (notifiedPipe.getConnectedSides().get(facing) == Connection.CONNECTED) {
                    notifiedPipe.removeVisualConnection(facing);
                }
            }
        }

    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState oState, boolean p_60519_) {
        if (level.getBlockEntity(pos) instanceof EnergyCableEntity pipe) {
            for (Direction direction : pipe.getConnectionsAsDirections()) {
                if (level.getBlockEntity(pos.relative(direction)) instanceof EnergyCableEntity connectedTo) {
                    connectedTo.removeVisualConnection(direction.getOpposite());
                }
            }
        }
        super.onRemove(state, level, pos, oState, p_60519_);
        level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
            if (capability instanceof LevelEnergyNetwork network) {
                EnergySubNetwork network1 = network.getNetworkFromPosition(pos);
                if (network1 != null) {
                    network.detach(network1, pos);
                    // this is because i didn't want to use tile entities.
                    // this is so if this block is removed during a blockstate change it can still stay as a energy cable in the level capability.
                  //  network.positionNeedsUpdating(network1.getTier(), pos);
                }
            }
        });
    }

    public static class Item extends BlockItem {

        private CapabilityTiers tier;

        public Item(Block block, CapabilityTiers tier, Properties p_40566_) {
            super(block, p_40566_);
            this.tier = tier;
        }

        @Override
        protected boolean placeBlock(BlockPlaceContext ctx, BlockState p_40579_) {
            if (ctx.getPlayer().isShiftKeyDown()) return false;
            if (super.placeBlock(ctx, p_40579_)) {
                ctx.getLevel().getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
                    Player player = ctx.getPlayer();
                    if (capability instanceof LevelEnergyNetwork network) {
                        EnergySubNetwork network1 = network.getNetworkFromPosition(ctx.getClickedPos());
                        network.createOrAttachTo(tier, ctx.getClickedPos(), true);
                    }
                });
                return true;
            }
            return false;
        }

        @Override
        public InteractionResult useOn(UseOnContext ctx) {
            if (!ctx.getLevel().isClientSide && ctx.getPlayer().isShiftKeyDown()) {
                ctx.getLevel().getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
                    Player player = ctx.getPlayer();
                    if (capability instanceof LevelEnergyNetwork network) {
                        EnergySubNetwork network1 = network.getNetworkFromPosition(ctx.getClickedPos());
                        if (network1 == null) {
                            network.createOrAttachTo(tier, ctx.getClickedPos(), true);
                            for (Direction direction : Direction.values()) {
                                BlockPos pos = ctx.getClickedPos().relative(direction);
                                if (ctx.getLevel().getBlockEntity(pos) instanceof EnergyCableEntity entity) {
                                    entity.addVisualConnection(direction.getOpposite());
                                }
                            }
                        }
                    }
                });
            }
            return super.useOn(ctx);
        }

        public CapabilityTiers getTier() {
            return tier;
        }
    }

    /**
     *
     player.sendSystemMessage(Component.literal("Total Networks: " + network.getNetworks().size()));

     if (network1 != null) {

     // do debug briefing
     player.sendSystemMessage(Component.literal("Networks At This Position: " + network.getAllNetworksFromPosition(ctx.getClickedPos()).size()));
     player.sendSystemMessage(Component.literal("Network: " + network1));
     player.sendSystemMessage(Component.literal("Network Size: " + network1.size()));
     player.sendSystemMessage(Component.literal("Energy Stored: " + network1.getInternalStorage().getEnergyStored()));
     player.sendSystemMessage(Component.literal("Energy Capacity: " + network1.getInternalStorage().getMaxEnergyStored()));
     player.sendSystemMessage(Component.literal("Output Inventories: " + network1.getAllOutputs().size()));

     }
     */

}
