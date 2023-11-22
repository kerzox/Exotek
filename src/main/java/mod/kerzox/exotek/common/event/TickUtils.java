package mod.kerzox.exotek.common.event;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.AbstractLevelNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.ILevelSubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class TickUtils {

    public static int serverTick;
    public static int clientRenderTick;

    public static int prevServerTick;
    public static int preVclientRenderTick;

    public static double getDeltaTime(float partialTicks) {
        return getLastClientTick() + (getClientTick() - getLastClientTick()) * partialTicks;
    }

    @SubscribeEvent
    public void onWorldTick(net.minecraftforge.event.TickEvent.LevelTickEvent event) {
        if (event.phase == net.minecraftforge.event.TickEvent.Phase.END) {
            if(event.side == LogicalSide.SERVER) {
                serverTick = (serverTick + 1) % 1_728_000;
                prevServerTick = serverTick;
                event.level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {
                    if (capability instanceof LevelEnergyNetwork network) {
                        for (ILevelSubNetwork subNets : network.getNetworks()) {
                            subNets.tick();
                        }
                        network.tick();
                    }
                });
            }
            else if (event.side == LogicalSide.CLIENT) {
                clientRenderTick = (clientRenderTick + 1) % 1_728_000;
                preVclientRenderTick = clientRenderTick;
            }
        }
    }

    public static int getClientTick() {
        return clientRenderTick;
    }

    public static int getLastClientTick() {
        return preVclientRenderTick;
    }

    public static int getLastServerTick() {
        return prevServerTick;
    }

    public static int getServerTick() {
        return serverTick;
    }
}
