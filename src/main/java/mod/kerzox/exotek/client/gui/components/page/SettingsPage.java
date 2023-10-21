package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.components.TabComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import mod.kerzox.exotek.common.network.ContainerSlotPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingsPage<T extends DefaultMenu<?>> extends BasicPage<T> {

    // all settings and the close button
    private Map<String, ModifyHandlerPage<T>> pages = new HashMap<>();

    // all settings and the close button
    private List<ButtonComponent<T>> buttons = new ArrayList<>();

    protected TabComponent<T> settingsTab;

    public SettingsPage(DefaultScreen<T> screen, int x, int y, int width, int height) {
        super(screen, x, y, width, height, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"));
        visible = false;
        settingsTab = new TabComponent<>(screen, 176, 5, 21, 24, 141, 143, 162, 143, this::togglePage);
        AtomicInteger btnX = new AtomicInteger(8);

        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.put("energy", new ModifyHandlerPage<>(screen, strictInventory,x + 0, y  +0, 0, 0));
                buttons.add(new ToggleButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"), x+ btnX.get(), y +7, 15, 15, 0, 104, 0, 104+15, this::energyTab));
                btnX.addAndGet(15);
            }
        });
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.put("item", new ModifyHandlerPage<>(screen, strictInventory,x + 0, y + 0, 0, 0));
                buttons.add(new ToggleButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"), x+btnX.get(), y +7, 15, 15, 32, 104, 32, 104+15, this::itemTab));
                btnX.addAndGet(15);
            }
        });
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.put("fluid", new ModifyHandlerPage<>(screen, strictInventory, x + 0, y + 0, 0, 0));
                buttons.add(new ToggleButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"), x+btnX.get(), y +7, 15, 15, 16, 104, 16, 104+15, this::fluidTab));
                btnX.addAndGet(15);
            }
        });
    }

    @Override
    public DefaultScreen<T> getScreen() {
        return (DefaultScreen<T>) super.getScreen();
    }

    private void itemTab(ButtonComponent<?> buttonComponent) {
        pages.forEach((s, p) -> {
            if (!s.equals("item")) p.setVisible(false);
        });
        for (ButtonComponent<T> button : buttons) {
            if (!button.equals(buttonComponent)) {
                button.setState(false);
            }
        }
        pages.get("item").setVisible(!pages.get("item").visible);

    }

    private void fluidTab(ButtonComponent<?> buttonComponent) {
        pages.forEach((s, p) -> {
            if (!s.equals("fluid")) p.setVisible(false);
        });
        for (ButtonComponent<T> button : buttons) {
            if (!button.equals(buttonComponent)) {
                button.setState(false);
            }
        }
        pages.get("fluid").setVisible(!pages.get("fluid").visible);
    }

    private void energyTab(ButtonComponent<?> buttonComponent) {
        pages.forEach((s, p) -> {
            if (!s.equals("energy")) p.setVisible(false);
        });
        for (ButtonComponent<T> button : buttons) {
            if (!button.equals(buttonComponent)) {
                button.setState(false);
            }
        }
        pages.get("energy").setVisible(!pages.get("energy").visible);
    }


    public void togglePage(ButtonComponent<?> button) {
        this.visible = !visible;
        if (this.visible) {
            settingsTab.setTextureOffset(162, 143);
            settingsTab.setPosition(settingsTab.getX() - 3, settingsTab.getY());
            PacketHandler.sendToServer(new ContainerSlotPacket(true));
            for (Slot slot : getScreen().getMenu().slots) {
                if (slot instanceof SlotComponent slotComponent) {
                    slotComponent.blockPickup(true);
                    slotComponent.blockPlace(true);
                    slotComponent.setActive(false);
                }
            }
        } else {
            settingsTab.setPosition(settingsTab.getX() + 3, settingsTab.getY());
            settingsTab.setTextureOffset(141, 143);
            PacketHandler.sendToServer(new ContainerSlotPacket(false));
            for (Slot slot : getScreen().getMenu().slots) {
                if (slot instanceof SlotComponent slotComponent) {
                    slotComponent.blockPickup(false);
                    slotComponent.blockPlace(false);
                    slotComponent.setActive(true);
                }
            }
        }
    }

    public void tick() {
        if (!visible) return;
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.get("energy").update(strictInventory.getInputs(), strictInventory.getOutputs());
            }
        });
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.get("item").update(strictInventory.getInputs(), strictInventory.getOutputs());
            }
        });
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.get("fluid").update(strictInventory.getInputs(), strictInventory.getOutputs());
            }
        });
    }

    @Override
    public void updatePositionToScreen() {
        super.updatePositionToScreen();
        settingsTab.updatePositionToScreen();
    }

    @Override
    public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
        settingsTab.mouseClicked(p_94737_, p_94738_, p_94739_);
        if (!visible) return super.mouseClicked(p_94737_, p_94738_, p_94739_);
        for (ButtonComponent<T> button : buttons) {
            button.mouseClicked(p_94737_, p_94738_, p_94739_);
        }
        pages.forEach((s, p) -> p.mouseClicked(p_94737_, p_94738_, p_94739_));
        return super.mouseClicked(p_94737_, p_94738_, p_94739_);
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.draw(graphics, this.x, this.y, this.width, this.height);
        for (ButtonComponent<T> button : buttons) {
            button.updatePositionToScreen();
            button.drawComponent(graphics, pMouseX, pMouseY, pPartialTick);
        }
        for (ModifyHandlerPage<T> page : pages.values()) {
            page.render(graphics, pMouseX, pMouseY, pPartialTick);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.x = this.screen.getGuiLeft() + this.x1;
        this.y = this.screen.getGuiTop() + this.y1;
        if (!visible)  {
            settingsTab.drawComponent(guiGraphics, pMouseX, pMouseY, pPartialTick);
            return;
        }
        drawComponent(guiGraphics, pMouseX, pMouseY, pPartialTick);
        settingsTab.drawComponent(guiGraphics, pMouseX, pMouseY, pPartialTick);
    }


    //    public void moveWithMouse(double mouseX, double mouseY) {
//        int pos1 = (int) (oldMouseX - mouseX);
//        int pos2 = (int) (oldMouseY - mouseY);
//        oldMouseX = (int) mouseX;
//        oldMouseY = (int) mouseY;
//        this.x = this.x - pos1;
//        this.y = this.y - pos2;
//        for (GuiConfigurationButtons<T> machineHandlerSlot : machineHandlerSlots) {
//            machineHandlerSlot.x = machineHandlerSlot.x - pos1;
//            machineHandlerSlot.y = machineHandlerSlot.y - pos2;
//        }
//        for (GuiButton<T> tabBtn : tabBtns) {
//            tabBtn.x = tabBtn.x - pos1;
//            tabBtn.y = tabBtn.y - pos2;
//        }
//    }
}
