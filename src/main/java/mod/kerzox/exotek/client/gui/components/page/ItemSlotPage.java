package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.client.gui.screen.StorageCrateScreen;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.network.ContainerSlotPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;

import java.util.HashMap;
import java.util.List;

public class ItemSlotPage extends BasicPage {

    private int startingIndex;
    private List<SlotComponent> slots;

    public ItemSlotPage(List<SlotComponent> slots, int index, ICustomScreen screen, int x, int y) {
        super(screen,  new ResourceLocation(Exotek.MODID, "textures/gui/item_slots.png"), x, y, 176, 125, 0, 0, 0, 0, Component.literal("Chest Page"));
        this.slots = slots;
        this.startingIndex = index;
    }

    @Override
    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        return false;
    }

    @Override
    public boolean mouseDragged(double p_93645_, double p_93646_, int p_93647_, double p_93648_, double p_93649_) {
        return false;
    }

    @Override
    public boolean mouseReleased(double p_93684_, double p_93685_, int p_93686_) {
        return false;
    }

    @Override
    public void mouseMoved(double p_94758_, double p_94759_) {

    }

    @Override
    public boolean mouseScrolled(double p_94734_, double p_94735_, double p_94736_) {
        return false;
    }

    public void openPage() {
        PacketHandler.sendToServer(new ContainerSlotPacket(startingIndex, false));
        for (int i = 0; i < slots.size(); i++) {
            SlotComponent slot = slots.get(i);

            for (Slot sScreen : screen.getMenu().getSlots()) {
                if (sScreen instanceof SlotComponent slotComponent) {
                    if (sScreen.getSlotIndex() == slot.getSlotIndex() && slot.getSlotIndex() >= startingIndex && slot.getSlotIndex() < startingIndex + 54) {
                        slotComponent.setActive(true);
                        slotComponent.blockPlace(false);
                        slotComponent.blockPickup(false);
                    }
                }
            }

        }

        setVisible(true);
    }

    public void closePage() {
        PacketHandler.sendToServer(new ContainerSlotPacket(startingIndex, true));
        for (int i = 0; i < slots.size(); i++) {
            SlotComponent slot = slots.get(i);

            for (Slot sScreen : screen.getMenu().getSlots()) {
                if (sScreen instanceof SlotComponent slotComponent) {
                    if (sScreen.getSlotIndex() == slot.getSlotIndex() && slot.getSlotIndex() >= startingIndex && slot.getSlotIndex() < startingIndex + 54) {
                        slotComponent.setActive(false);
                        slotComponent.blockPlace(true);
                        slotComponent.blockPickup(true);
                    }
                }
            }

        }
        setVisible(false);
    }

    @Override
    protected void drawComponent(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.draw(graphics, getCorrectX(), getCorrectY(), getWidth(), getHeight(), getWidth(), getHeight());
    }



}
