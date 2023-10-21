package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.utility.WrenchHandler;
import mod.kerzox.exotek.common.network.OpenScreen;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class WrenchItem extends Item {

    public WrenchItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        if (!ctx.getLevel().isClientSide) {
            ctx.getItemInHand().getCapability(ExotekCapabilities.CYCLE_ITEM_CAPABILITY).ifPresent(iCycleItem -> {
                if (iCycleItem instanceof WrenchHandler wrench) {

                    Player player = ctx.getPlayer();

                    ctx.getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {


                        if (capability instanceof LevelEnergyNetwork network && ctx.getHand() == InteractionHand.MAIN_HAND) {

                            EnergySingleNetwork network1 = network.getNetworkFromPosition(ctx.getClickedPos());

                            if (network1 != null) {

                                if (wrench.getMode() == WrenchHandler.Modes.DEBUG) {
                                    player.sendSystemMessage(Component.literal("Total Networks: " + network.getNetworks().size()));
                                    player.sendSystemMessage(Component.literal("Networks At This Position: " + network.getAllNetworksFromPosition(ctx.getClickedPos()).size()));
                                    player.sendSystemMessage(Component.literal("Network Tier: " + network1.getTier()));
                                    player.sendSystemMessage(Component.literal("Network: " + network1));
                                    player.sendSystemMessage(Component.literal("Network Size: " + network1.size()));
                                    player.sendSystemMessage(Component.literal("Energy Stored: " + network1.getInternalStorage().getEnergyStored()));
                                    player.sendSystemMessage(Component.literal("Energy Capacity: " + network1.getInternalStorage().getMaxEnergyStored()));
                                    player.sendSystemMessage(Component.literal("Output Inventories: " + network1.getAllOutputs().size()));
                                    player.sendSystemMessage(Component.literal("IO Settings"));
                                    if (network1.getNodeByPosition(ctx.getClickedPos()) != null) {
                                        network1.getNodeByPosition(ctx.getClickedPos()).getDirectionalIO().forEach((d, t) -> {
                                            player.sendSystemMessage(Component.literal(d.getName() + " : " + t.getName()));
                                        });
                                    }
                                }

                                if (wrench.getMode() == WrenchHandler.Modes.WRENCH) {
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
                                    return;
                                }

                                if (wrench.getMode() == WrenchHandler.Modes.ENERGY) {
                                    if (!ctx.getLevel().isClientSide && ctx.getLevel().getBlockEntity(ctx.getClickedPos()) == null) {
                                        PacketHandler.sendToClientPlayer(new OpenScreen(OpenScreen.Screens.ENERGY_CABLE_LEVEL_SCREEN,
                                                network1.getNodeByPosition(ctx.getClickedPos()).serialize()), (ServerPlayer) player);
                                    }
                                    return;
                                }

                            }
                        }


                    });
                }
            });
        }
        return super.useOn(ctx);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new WrenchHandler();
    }


}
