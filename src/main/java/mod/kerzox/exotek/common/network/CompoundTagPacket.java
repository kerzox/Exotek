package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CompoundTagPacket {

    CompoundTag recipeNbt;

    public CompoundTagPacket(CompoundTag up) {
        this.recipeNbt = up;
    }

    public CompoundTagPacket(String str) {
        CompoundTag tag = new CompoundTag();
        tag.putString(str, str);
        recipeNbt = tag;
    }

    public CompoundTagPacket(String str, int value) {
        CompoundTag tag = new CompoundTag();
        tag.putInt(str, value);
        recipeNbt = tag;
    }

    public CompoundTagPacket(String str, float value) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(str, value);
        recipeNbt = tag;
    }

    public CompoundTagPacket(String str, double value) {
        CompoundTag tag = new CompoundTag();
        tag.putDouble(str, value);
        recipeNbt = tag;
    }

    public CompoundTagPacket(FriendlyByteBuf buf) {
        this.recipeNbt = buf.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(recipeNbt);
    }

    public static boolean handle(CompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(CompoundTagPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                menu.getBlockEntity().updateFromNetwork(packet.recipeNbt);
            }
        }
    }

}
