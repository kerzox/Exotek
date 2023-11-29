package mod.kerzox.exotek.client.render.event;

import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.UtilityModeCycle;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ClientMouseEvents {

    @SubscribeEvent
    public void onMouseScroll(InputEvent.MouseScrollingEvent event) {
        Player player = Minecraft.getInstance().player;
        if (player.isShiftKeyDown()) {
            if (player.getMainHandItem().getItem() == ExotekRegistry.Items.WRENCH_ITEM.get()) {
                PacketHandler.sendToServer(new UtilityModeCycle(event.getScrollDelta() < 0));
                event.setCanceled(true);
            }
        }
    }

}
