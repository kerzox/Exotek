package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateHandlerPacket {

    CompoundTag recipeNbt;

    public UpdateHandlerPacket(CompoundTag up) {
        this.recipeNbt = up;
    }

    public UpdateHandlerPacket(FriendlyByteBuf buf) {
        this.recipeNbt = buf.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(recipeNbt);
    }

    public static boolean handle(UpdateHandlerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(UpdateHandlerPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                menu.getBlockEntity().deserializeNBT(packet.recipeNbt);
            }
        }
    }

}
