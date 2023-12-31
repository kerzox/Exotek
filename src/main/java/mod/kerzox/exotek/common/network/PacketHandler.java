package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.Exotek;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class PacketHandler {

    private static int ID = 0;
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(Exotek.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int nextID() {
        return ID++;
    }

    public static void register() {
        INSTANCE.messageBuilder(SyncContainer.class, nextID())
                .encoder((p, b) -> {})
                .decoder(SyncContainer::new)
                .consumerMainThread(SyncContainer::handle)
                .add();
        INSTANCE.messageBuilder(FluidTankClick.class, nextID())
                .encoder(FluidTankClick::toBytes)
                .decoder(FluidTankClick::new)
                .consumerMainThread(FluidTankClick::handle)
                .add();
        INSTANCE.messageBuilder(CreateBlueprintItem.class, nextID())
                .encoder(CreateBlueprintItem::toBytes)
                .decoder(CreateBlueprintItem::new)
                .consumerMainThread(CreateBlueprintItem::handle)
                .add();
        INSTANCE.messageBuilder(LockRecipePacket.class, nextID())
                .encoder(LockRecipePacket::toBytes)
                .decoder(LockRecipePacket::new)
                .consumerMainThread(LockRecipePacket::handle)
                .add();
        INSTANCE.messageBuilder(StartRecipePacket.class, nextID())
                .encoder(StartRecipePacket::toBytes)
                .decoder(StartRecipePacket::new)
                .consumerMainThread(StartRecipePacket::handle)
                .add();
        INSTANCE.messageBuilder(MoveUpgradePacket.class, nextID())
                .encoder(MoveUpgradePacket::toBytes)
                .decoder(MoveUpgradePacket::new)
                .consumerMainThread(MoveUpgradePacket::handle)
                .add();
        INSTANCE.messageBuilder(ContainerSlotPacket.class, nextID())
                .encoder(ContainerSlotPacket::toBytes)
                .decoder(ContainerSlotPacket::new)
                .consumerMainThread(ContainerSlotPacket::handle)
                .add();
        INSTANCE.messageBuilder(CompoundTagPacket.class, nextID())
                .encoder(CompoundTagPacket::toBytes)
                .decoder(CompoundTagPacket::new)
                .consumerMainThread(CompoundTagPacket::handle)
                .add();
        INSTANCE.messageBuilder(LevelNetworkPacket.class, nextID())
                .encoder(LevelNetworkPacket::toBytes)
                .decoder(LevelNetworkPacket::new)
                .consumerMainThread(LevelNetworkPacket::handle)
                .add();
        INSTANCE.messageBuilder(UpdateHandlerPacket.class, nextID())
                .encoder(UpdateHandlerPacket::toBytes)
                .decoder(UpdateHandlerPacket::new)
                .consumerMainThread(UpdateHandlerPacket::handle)
                .add();
        INSTANCE.messageBuilder(OpenScreen.class, nextID())
                .encoder(OpenScreen::toBytes)
                .decoder(OpenScreen::new)
                .consumerMainThread(OpenScreen::handle)
                .add();
        INSTANCE.messageBuilder(UtilityModeCycle.class, nextID())
                .encoder(UtilityModeCycle::toBytes)
                .decoder(UtilityModeCycle::new)
                .consumerMainThread(UtilityModeCycle::handle)
                .add();
        INSTANCE.messageBuilder(SyncBlockEntityAtPosition.class, nextID())
                .encoder(SyncBlockEntityAtPosition::toBytes)
                .decoder(SyncBlockEntityAtPosition::new)
                .consumerMainThread(SyncBlockEntityAtPosition::handle)
                .add();
    }

    public static void sendToClientPlayer(Object packet, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllClientsFromChunk(Object packet, LevelChunk chunk) {
        INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    public static void sendToAllClients(Object packet) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), packet);
    }
    public static void sendToServer(Object packet) {
        INSTANCE.sendToServer(packet);
    }

}
