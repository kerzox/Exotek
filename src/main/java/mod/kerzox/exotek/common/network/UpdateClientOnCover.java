package mod.kerzox.exotek.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class UpdateClientOnCover {

    private CompoundTag tag;

    public UpdateClientOnCover(FriendlyByteBuf buf) {
        tag = buf.readNbt();
    }

    public UpdateClientOnCover(CompoundTag delta) {
        tag = delta;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public static boolean handle(UpdateClientOnCover packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(packet, ctx));
        });
        return true;
    }

    private static void handleOnServer(UpdateClientOnCover packet, Supplier<NetworkEvent.Context> ctx) {

    }

    private static void handleOnClient(UpdateClientOnCover packet, Supplier<NetworkEvent.Context> ctx) {

        BlockPos pos = NbtUtils.readBlockPos(packet.tag.getCompound("pos"));



    }

}
