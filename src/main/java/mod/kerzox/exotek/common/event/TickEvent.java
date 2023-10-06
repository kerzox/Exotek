package mod.kerzox.exotek.common.event;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class TickEvent {

    @SubscribeEvent
    public void onWorldTick(net.minecraftforge.event.TickEvent.LevelTickEvent event) {
        if (event.phase == net.minecraftforge.event.TickEvent.Phase.END && event.side == LogicalSide.SERVER) {
            event.level.getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(capability -> {

                if (capability instanceof LevelEnergyNetwork network) {

                    for (EnergySingleNetwork energySingleNetwork : network.getNetworks()) {
                        energySingleNetwork.tick();
                    }

                    network.tick();

                }

            });
        }
    }

}
