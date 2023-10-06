package mod.kerzox.exotek.common.event;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySingleNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.TickEvent;
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

}
