package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class WrenchItem extends Item {

    public WrenchItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!ctx.getLevel().isClientSide) {
            ctx.getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {

                Player player = ctx.getPlayer();

                if (capability instanceof LevelEnergyNetwork network) {

                    EnergySingleNetwork network1 = network.getNetworkFromPosition(ctx.getClickedPos());
                    player.sendSystemMessage(Component.literal("Total Networks: " + network.getNetworks().size()));


                    if (network1 != null) {
                        if (!player.isShiftKeyDown()) {
                            // remove it from the network
                            network.detach(network1, ctx.getClickedPos());
                            BlockState blockState = ctx.getLevel().getBlockState(ctx.getClickedPos());

                            for (Direction direction : Direction.values()) {
                                BlockPos pos = ctx.getClickedPos().relative(direction);
                                if (ctx.getLevel().getBlockEntity(pos) instanceof EnergyCableEntity entity) {
                                    entity.removeVisualConnection(direction.getOpposite());
                                }
                            }

                            // block we are clicking is an actual energy cable block so we want to drop it
                            if (blockState.getBlock() == Registry.Blocks.ENERGY_CABLE_BLOCK.get() || blockState.getBlock() == Registry.Blocks.ENERGY_CABLE_3_BLOCK.get() || blockState.getBlock() == Registry.Blocks.ENERGY_CABLE_2_BLOCK.get()) {
                                ctx.getLevel().destroyBlock(ctx.getClickedPos(), true);
                            }


                        }
                        else {
                            // do debug briefing
                            player.sendSystemMessage(Component.literal("Networks At This Position: " + network.getAllNetworksFromPosition(ctx.getClickedPos()).size()));
                            player.sendSystemMessage(Component.literal("Network Tier: " + network1.getTier()));
                            player.sendSystemMessage(Component.literal("Network: " + network1));
                            player.sendSystemMessage(Component.literal("Network Size: " + network1.size()));
                            player.sendSystemMessage(Component.literal("Energy Stored: " + network1.getInternalStorage().getEnergyStored()));
                            player.sendSystemMessage(Component.literal("Energy Capacity: " + network1.getInternalStorage().getMaxEnergyStored()));
                            player.sendSystemMessage(Component.literal("Output Inventories: " + network1.getAllOutputs().size()));
                        }

                    }

                }

            });
        }
        return super.useOn(ctx);
    }
}
