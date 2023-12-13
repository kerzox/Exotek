package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.menu.StorageCrateMenu;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ContainerSlotPacket {

    int index;
    boolean data;

    public ContainerSlotPacket(int data, boolean vis) {
        this.index = data;
        this.data = vis;
    }

    public ContainerSlotPacket(FriendlyByteBuf buf) {
        this.index = buf.readInt();
        this.data = buf.readBoolean();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(this.index);
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
            if (player.containerMenu instanceof StorageCrateMenu menu) {

                for (int i = 0; i < menu.getPageSlots().get(menu.getTier()).size(); i++) {

                    Set<SlotComponent> allSlots = new HashSet<>();
                    menu.getPageSlots().values().forEach(allSlots::addAll);

                    for (SlotComponent slot : allSlots) {
                        for (Slot sScreen : menu.getSlots()) {
                            if (sScreen instanceof SlotComponent slotComponent) {
                                if (sScreen.getSlotIndex() == slot.getSlotIndex()) {
                                    if (slotComponent.getSlotIndex() >= packet.index && slotComponent.getSlotIndex() <= packet.index + 54) {
                                        slotComponent.blockPlace(packet.data);
                                        slotComponent.blockPickup(packet.data);
                                        slotComponent.setActive(!packet.data);
                                    }
                                }
                            }
                        }
                    }

                }

            }
        }
    }

}
