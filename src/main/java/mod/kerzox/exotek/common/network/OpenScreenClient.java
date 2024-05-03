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

public class OpenScreenClient {
    public static void handleOnClient(OpenScreen packet, Supplier<NetworkEvent.Context> ctx) {
        switch (packet.data) {
            case ENERGY_CABLE_LEVEL_SCREEN -> EnergyCableLevelScreen.draw(new LevelNode(packet.tag));
        }
    }

}
