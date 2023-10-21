package mod.kerzox.exotek.common.event;

import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.utility.WrenchHandler;
import mod.kerzox.exotek.common.network.OpenScreen;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CommonEvents {

    @SubscribeEvent
    public void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        LevelAccessor levelAcc = event.getLevel();
        BlockPos pos = event.getPos();

        // hopefully this level accessor is a instance of level

        if (levelAcc instanceof Level level) {
            level.getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof LevelEnergyNetwork levelNetwork) {
                    for (Direction direction : Direction.values()) {
                        BlockPos neighbouring = pos.relative(direction);

                        EnergySingleNetwork subnet = levelNetwork.getNetworkFromPosition(neighbouring);
                        if (subnet != null) {
                            subnet.checkForInventories();
                        }

                    }
                }
            });
        }

    }

    @SubscribeEvent
    public void onBlockClicked(PlayerInteractEvent.RightClickBlock ctx) {
//        if (!ctx.getLevel().isClientSide) {
//            ctx.getEntity().getMainHandItem().getCapability(ExotekCapabilities.CYCLE_ITEM_CAPABILITY).ifPresent(iCycleItem -> {
//                if (iCycleItem instanceof WrenchHandler wrench) {
//                    ctx.getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
//
//                        Player player = ctx.getEntity();
//
//                        if (capability instanceof LevelEnergyNetwork network) {
//
//                            EnergySingleNetwork network1 = network.getNetworkFromPosition(ctx.getPos());
//
//                            if (network1 != null) {
//
//                                if (wrench.getMode() == WrenchHandler.Modes.WRENCH) {
//                                    network.detach(network1, ctx.getPos());
//                                    BlockState blockState = ctx.getLevel().getBlockState(ctx.getPos());
//
//                                    for (Direction direction : Direction.values()) {
//                                        BlockPos pos = ctx.getPos().relative(direction);
//                                        if (ctx.getLevel().getBlockEntity(pos) instanceof EnergyCableEntity entity) {
//                                            entity.removeVisualConnection(direction.getOpposite());
//                                        }
//                                    }
//
//                                    // block we are clicking is an actual energy cable block so we want to drop it
//                                    if (blockState.getBlock() == Registry.Blocks.ENERGY_CABLE_BLOCK.get() || blockState.getBlock() == Registry.Blocks.ENERGY_CABLE_3_BLOCK.get() || blockState.getBlock() == Registry.Blocks.ENERGY_CABLE_2_BLOCK.get()) {
//                                        ctx.getLevel().destroyBlock(ctx.getPos(), true);
//                                    }
//                                    return;
//                                }
//
//                                if (wrench.getMode() == WrenchHandler.Modes.ENERGY) {
//                                    if (!ctx.getLevel().isClientSide && ctx.getLevel().getBlockEntity(ctx.getPos()) == null) {
//                                        PacketHandler.sendToClientPlayer(new OpenScreen(OpenScreen.Screens.ENERGY_CABLE_LEVEL_SCREEN,
//                                                network1.getNodeByPosition(ctx.getPos()).serialize()), (ServerPlayer) player);
//                                    }
//                                    return;
//                                }
//
//                            }
//                        }
//                    });
//                }
//            });
//        }
    }

}
