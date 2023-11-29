package mod.kerzox.exotek.common.network;

import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.blockentities.machine.ManufactoryEntity;
import mod.kerzox.exotek.common.blockentities.machine.SingleBlockMinerEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.TurbineManager;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class FluidTankClick {

    private int index;

    public FluidTankClick(int index) {
        this.index = index;
    }

    public FluidTankClick(FriendlyByteBuf buf) {
        this.index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(index);
    }

    public static boolean handle(FluidTankClick packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (ctx.get().getDirection() == NetworkDirection.PLAY_TO_SERVER) handleOnServer(packet, ctx);
        });
        return true;
    }

    private static void handleOnServer(FluidTankClick packet, Supplier<NetworkEvent.Context> ctx) {
        Player player = ctx.get().getSender();
        if (player != null) {
            Level level = player.level();
            if (player.containerMenu instanceof DefaultMenu<?> menu) {

                ItemStack stack = menu.getCarried();
                Optional<IFluidHandlerItem> optionalIFluidHandlerItem = stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM).resolve();

                if (optionalIFluidHandlerItem.isPresent()) {

                    // try to fill
                    if (menu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof SidedSingleFluidTank.CombinedWrapper wr) {
                        FluidActionResult result = FluidUtil.tryFillContainer(stack, wr, 1000, Minecraft.getInstance().player, true);
                        if (result.success) {
                            menu.setCarried(result.result);
                            return;
                        }
                    }

                    if (menu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof SidedMultifluidTank.PlayerWrapper wr) {
                        FluidActionResult result = FluidUtil.tryFillContainer(stack, wr.getInventory().getFluidTank(packet.index), 1000, Minecraft.getInstance().player, true);
                        if (result.success) {
                            menu.setCarried(result.result);
                            return;
                        }

                    }

                    // try to insert

                    if (menu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof SidedSingleFluidTank.CombinedWrapper wr) {
                        FluidActionResult result = FluidUtil.tryEmptyContainer(stack, wr, 1000, Minecraft.getInstance().player, true);
                        if (result.success) {
                            menu.setCarried(result.result);
                            return;
                        }
                    }

                    if (menu.getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).resolve().get() instanceof SidedMultifluidTank.PlayerWrapper wr) {
                        FluidActionResult result = FluidUtil.tryEmptyContainer(stack, wr.getInventory().getFluidTank(packet.index), 1000, Minecraft.getInstance().player, true);
                        if (result.success) {
                            menu.setCarried(result.result);
                            return;
                        }
                    }

                }
            }
        }
    }

}
