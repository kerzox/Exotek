package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ContainerSlotPacket {

    boolean data;

    public ContainerSlotPacket(boolean data) {
        this.data = data;
    }

    public ContainerSlotPacket(FriendlyByteBuf buf) {
        this.data = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(this.data);
    }

    public static boolean handle(ContainerSlotPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(ContainerSlotPacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                for (Slot slot : menu.getSlots()) {
                    if (slot instanceof SlotComponent slotComponent) {
                        slotComponent.blockPlace(packet.data);
                        slotComponent.blockPickup(packet.data);
                        slotComponent.setActive(!packet.data);
                    }
                }
            }
        }
    }

}
