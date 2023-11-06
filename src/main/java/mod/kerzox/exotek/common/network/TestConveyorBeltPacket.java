package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TestConveyorBeltPacket {

    private float tag;

    public TestConveyorBeltPacket(FriendlyByteBuf buf) {
        tag = buf.readFloat();
    }

    public TestConveyorBeltPacket(float delta) {
        tag = delta;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeFloat(tag);
    }

    public static boolean handle(TestConveyorBeltPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(packet, ctx));
        });
        return true;
    }

    private static void handleOnServer(TestConveyorBeltPacket packet, Supplier<NetworkEvent.Context> ctx) {

    }

    private static void handleOnClient(TestConveyorBeltPacket packet, Supplier<NetworkEvent.Context> ctx) {
        float delta = packet.tag;

    }

}
