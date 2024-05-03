package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LevelNetworkClientPacket {

    public static void handleOnClient(LevelNetworkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.level().getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof LevelEnergyNetwork network) {
                    network.deserializeNBT(packet.nbtTag);
                }
            });
        }
    }

}
