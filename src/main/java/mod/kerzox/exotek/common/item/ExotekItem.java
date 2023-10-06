package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.FluidDeposit;
import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class ExotekItem extends Item {

    public ExotekItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        Player player = ctx.getPlayer();

        if (!level.isClientSide) {

            level.getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {

                if (capability instanceof LevelEnergyNetwork network) {

                }

            });
        }

//        IDeposit deposit = p_41427_.getLevel().getChunkAt(p_41427_.getClickedPos()).getCapability(ExotekCapabilities.DEPOSIT_CAPABILITY).orElse(null);
//        if (deposit instanceof ChunkDeposit chunkDeposit && !p_41427_.getLevel().isClientSide) {
//            if (chunkDeposit.isFluidDeposit()) {
//                p_41427_.getPlayer().sendSystemMessage(Component.literal("Fluid: ")
//                        .append(chunkDeposit.getFluidDeposit().getFluids().get(0).getDisplayName()).append("\n")
//                        .append(chunkDeposit.getFluidDeposit().getReservoirAmount()+""));
//            }
//            if (chunkDeposit.isOreDeposit()) {
//                p_41427_.getPlayer().sendSystemMessage(Component.literal("Ore: ")
//                        .append(chunkDeposit.getOreDeposit().getItems().get(0).getItemStack().getDisplayName()).append("\n")
//                        .append(chunkDeposit.getOreDeposit().getVeinAmounts()[0]+""));
//            }
//        }
        return super.useOn(ctx);
    }
}
