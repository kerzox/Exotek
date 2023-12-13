package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import mod.kerzox.exotek.common.capability.BlueprintHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.item.BlueprintValidatorItem;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class CreateBlueprintItem {

    private String blueprint;
    private int index;

    public CreateBlueprintItem(String blueprint, int index) {
        this.blueprint = blueprint;
        this.index = index;
    }

    public CreateBlueprintItem(FriendlyByteBuf buf) {
        blueprint = buf.readUtf();
        index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(blueprint);
        buf.writeInt(index);
    }

    public static boolean handle(CreateBlueprintItem packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(CreateBlueprintItem packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof WorkstationMenu menu) {
                if (menu.getBlockEntity().getHandler().insertItem(packet.index,
                        BlueprintValidatorItem.of(ExotekRegistry.Items.BLUEPRINT_ITEM.get(), packet.blueprint), false).isEmpty()) {

                }
            }
        }
    }

}
