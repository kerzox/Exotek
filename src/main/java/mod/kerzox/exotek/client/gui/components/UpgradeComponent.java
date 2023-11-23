package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.capability.upgrade.UpgradeCard;
import mod.kerzox.exotek.common.item.MachineUpgradeItem;
import mod.kerzox.exotek.common.network.MoveUpgradePacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.List;

import static mod.kerzox.exotek.client.render.RenderingUtil.custom;

public class UpgradeComponent extends NewWidgetComponent {

    private int slot;
    private ResourceLocation texture = new ResourceLocation(Exotek.MODID, "textures/gui/upgrade_page.png");
    private ItemStack stack = ItemStack.EMPTY;

    public UpgradeComponent(ICustomScreen screen, int slot, int x, int y, Component message) {
        super(screen, x, y, 30, 11, message);
        this.slot = slot;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 1) // do extract
        {
            PacketHandler.sendToServer(new MoveUpgradePacket(screen.getMenu().getBlockEntity().getBlockPos(), slot));
        }
        if (stack.getItem() instanceof MachineUpgradeItem card) Minecraft.getInstance().player.sendSystemMessage(Component.literal("Upgrade: " + card.getUpgradeName() + "\nCount: " + stack.getCount()));
        playDownSound();
    }

    @Override
    protected boolean isValidClickButton(int p_93652_) {
        return p_93652_ == 0 || p_93652_ == 1;
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        return super.isMouseOver(x, y);
    }

    @Override
    protected List<Component> getComponents() {
        if (stack.getItem() instanceof MachineUpgradeItem item) return Arrays.asList(
                Component.literal("Upgrade: " + item.getUpgradeName()),
                Component.literal("Count: " + stack.getCount()));
        else return super.getComponents();
    }

    @Override
    protected void drawComponent(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        if (!stack.isEmpty()) {

            graphics.blit(texture, getCorrectX(), getCorrectY(), 0, 57, width, height, 80, 80);
            graphics.pose().pushPose();
            graphics.pose().scale(0.5f, 0.5f, 0.5f);
            float scaled = 1/.5f;
            graphics.renderItem(this.stack, Math.round(getCorrectX() * scaled) + width / 4, Math.round(getCorrectY() * scaled) + height / 4);
            graphics.pose().popPose();

            if (isFocused()) {
                graphics.fill(getCorrectX(), getCorrectY(), getCorrectX() + width - 1, getCorrectY() + 1, custom("FFDE00", 100));
                graphics.fill(getCorrectX(), getCorrectY(), getCorrectX() + 1, getCorrectY() + height - 1, custom("FFDE00", 100));

                graphics.fill(getCorrectX() + width - 2,
                        getCorrectY(),
                        getCorrectX() + width - 1, getCorrectY() + height - 1,
                        custom("FFDE00", 100));

                graphics.fill(getCorrectX(), getCorrectY() + height - 2, getCorrectX() + width - 1, getCorrectY() + height - 1, custom("FFDE00", 100));
            }
        }
    }

    public void setItemStack(ItemStack stackInSlot) {
        this.stack = stackInSlot;
    }
}
