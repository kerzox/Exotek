package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class LevelNetworkPacket {

    CompoundTag nbtTag;

    public LevelNetworkPacket(CompoundTag up) {
        this.nbtTag = up;
    }

    public LevelNetworkPacket(FriendlyByteBuf buf) {
        this.nbtTag = buf.readAnySizeNbt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(nbtTag);
    }

    public static boolean handle(LevelNetworkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> LevelNetworkClientPacket.handleOnClient(packet, ctx));
        });
        return true;
    }

    private static void handleOnServer(LevelNetworkPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();
        if (player != null) {
            Level level = player.getCommandSenderWorld();
            level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {
                if (cap instanceof LevelEnergyNetwork network) {
                    if (packet.nbtTag.contains("node_to_update")) {
                        CompoundTag tag1 = packet.nbtTag.getCompound("node_to_update");
                        LevelNode node = new LevelNode(tag1);
                        EnergySubNetwork sub = network.getNetworkFromPosition(node.getWorldPosition());
                        if (sub != null) {
                            sub.getNodeByPosition(node.getWorldPosition()).read(tag1);
                            sub.checkNodeForSpecialType(sub.getNodeByPosition(node.getWorldPosition()));
                        }
                    }
                    else PacketHandler.sendToClientPlayer(new LevelNetworkPacket(network.serializeNBT()), player);
                }
            });
        }
    }



}
