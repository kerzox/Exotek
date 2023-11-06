package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.screen.transfer.EnergyCableLevelScreen;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
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

public class MoveUpgradePacket {

    private CompoundTag tag = new CompoundTag();

    public MoveUpgradePacket(FriendlyByteBuf buf) {
        tag = buf.readAnySizeNbt();
    }

    public MoveUpgradePacket(BlockPos pos, int slot) {
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        tag.putInt("slot", slot);
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(tag);
    }

    public static boolean handle(MoveUpgradePacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
            else DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleOnClient(packet, ctx));
        });
        return true;
    }

    private static void handleOnServer(MoveUpgradePacket packet, Supplier<NetworkEvent.Context> ctx) {
        BlockPos pos = NbtUtils.readBlockPos(packet.tag.getCompound("pos"));
        int slot = packet.tag.getInt("slot");
        BlockEntity blockEntity = ctx.get().getSender().level().getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.getCapability(ExotekCapabilities.UPGRADABLE_MACHINE).ifPresent(cap -> {
                if (!cap.getInventory().extractItem(slot, 1, true).isEmpty()) {
                    if (cap.getInventory().insertItem(1, cap.getInventory().extractItem(slot, 1, true), true).isEmpty()) {
                        cap.getInventory().insertItem(1, cap.getInventory().extractItem(slot, 1, false), false);
                    }
                }
            });
            if (blockEntity instanceof BasicBlockEntity blockEntity1) blockEntity1.syncBlockEntity();
        }
    }

    private static void handleOnClient(MoveUpgradePacket packet, Supplier<NetworkEvent.Context> ctx) {

    }

}
