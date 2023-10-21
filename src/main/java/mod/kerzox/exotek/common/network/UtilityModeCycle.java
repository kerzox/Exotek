package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.utility.WrenchHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UtilityModeCycle {

    boolean up;

    public UtilityModeCycle(boolean up) {
        this.up = up;
    }

    public UtilityModeCycle(FriendlyByteBuf buf) {
        this.up = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(up);
    }

    public static boolean handle(UtilityModeCycle packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(UtilityModeCycle packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            player.getMainHandItem().getCapability(ExotekCapabilities.CYCLE_ITEM_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof WrenchHandler utilityHandler) {
                    utilityHandler.cycle(packet.up);
                    player.sendSystemMessage(Component.literal(utilityHandler.getMode().toString()));
                }
            });
        }
    }

}
