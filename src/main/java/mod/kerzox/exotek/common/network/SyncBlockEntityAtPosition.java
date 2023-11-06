package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncBlockEntityAtPosition {

    private BlockPos pos;

    public SyncBlockEntityAtPosition(BlockPos pos) {
        this.pos = pos;
    }

    public SyncBlockEntityAtPosition(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public static boolean handle(SyncBlockEntityAtPosition packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(SyncBlockEntityAtPosition packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (level.getBlockEntity(packet.pos) instanceof BasicBlockEntity blockEntity) {
                blockEntity.syncBlockEntity();
            }
        }
    }
}
