package mod.kerzox.exotek.client.gui.components.prefab;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public class AutoSortButton<T extends DefaultMenu<?>> extends ToggleButtonComponent<T> {

    public AutoSortButton(ICustomScreen screen, int x, int y, IPressable btn) {
        super(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), x, y, 12, 12, 108, 217, 108, 217 + 12, btn);
    }

    public AutoSortButton(ICustomScreen screen, int x, int y) {
        this(screen, x, y, button1 -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("sort", "sort");
            PacketHandler.sendToServer(new CompoundTagPacket(tag));
        });
    }



    @Override
    public void doHover(GuiGraphics graphics, int pMouseX, int pMouseY) {
        graphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.literal("Item Sort")), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
    }
}
