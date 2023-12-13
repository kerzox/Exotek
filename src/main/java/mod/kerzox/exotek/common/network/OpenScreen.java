package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.screen.transfer.EnergyCableLevelScreen;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class OpenScreen {

    private Screens data;
    private CompoundTag tag;

    public enum Screens {
        ENERGY_CABLE_LEVEL_SCREEN
    }

    public OpenScreen(FriendlyByteBuf buf) {
        data = Screens.valueOf(buf.readUtf());
        tag = buf.readAnySizeNbt();
    }

    public OpenScreen(Screens screen, CompoundTag tag) {
        data = screen;
        this.tag = tag;
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(data.toString());
        buf.writeNbt(tag);
    }

    public static boolean handle(OpenScreen packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(packet, ctx));
        });
        return true;
    }

    private static void handleOnServer(OpenScreen packet, Supplier<NetworkEvent.Context> ctx) {

    }

    private static void handleOnClient(OpenScreen packet,Supplier<NetworkEvent.Context> ctx) {
        switch (packet.data) {
            case ENERGY_CABLE_LEVEL_SCREEN -> EnergyCableLevelScreen.draw(new LevelNode(packet.tag));
        }
    }

}
