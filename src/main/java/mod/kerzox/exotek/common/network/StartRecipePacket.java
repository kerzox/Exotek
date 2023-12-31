package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.TurbineManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartRecipePacket {

    public StartRecipePacket() {

    }

    public StartRecipePacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public static boolean handle(StartRecipePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(StartRecipePacket packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {
                if (menu.getBlockEntity() instanceof ManufactoryEntity manufactoryEntity) {
                    manufactoryEntity.setStall(!manufactoryEntity.isStalled());
                }
                if (menu.getBlockEntity() instanceof SingleBlockMinerEntity minerDrillBlockEntity) {
                    minerDrillBlockEntity.setRunning(!minerDrillBlockEntity.isRunning());
                }
                if (menu.getBlockEntity() instanceof MultiblockEntity entity) {
                   if (entity.getMultiblockManager() instanceof TurbineManager turbineManager) {
                       turbineManager.setRunning(!turbineManager.isRunning());
                   }
                }
            }
        }
    }

}
