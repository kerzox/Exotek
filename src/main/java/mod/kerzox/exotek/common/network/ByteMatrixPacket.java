package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ByteMatrixPacket {

    byte[][] data;

    public ByteMatrixPacket(byte[][] data) {
        this.data = data;
    }

    public ByteMatrixPacket(FriendlyByteBuf buf) {
        data = new byte[buf.readInt()][];
        for (int i = 0; i < data.length; i++) {
            data[i] = buf.readByteArray();
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(data.length);
        for (byte[] datum : data) {
            buf.writeByteArray(datum);
        }
    }

    public static boolean handle(ByteMatrixPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(ByteMatrixPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {

            }
        }
    }

}
