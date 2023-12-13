package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.capability.IStrictCapabilityIO;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import mod.kerzox.exotek.common.event.TickUtils;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class SettingsPage extends BasicPage {

    // all settings and the close button
    private Map<String, ModifyHandlerPage> pages = new HashMap<>();
    private List<ButtonComponent> buttons = new ArrayList<>();

    protected ToggleButtonComponent settingsTab;

    private int prevX;
    private int goTo;

    public SettingsPage(ICustomScreen screen, int x, int y, int width, int height) {
        super(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), x, y, width, height, 0, 0, 0, 0, Component.literal("Settings Page"));
        goTo = x - 50;
        visible = false;
        settingsTab = new ToggleButtonComponent(screen,
                new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                -5, 5, 5, 82, 0, 0, 0, 0, Component.literal("Toggle Settings"), this::togglePage);

        AtomicInteger btnX = new AtomicInteger(8);

        AtomicInteger tabY = new AtomicInteger(8);

        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.put("energy", new ModifyHandlerPage(screen, strictInventory, x, y, 82, 82));
                buttons.add(new ToggleButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                        x + btnX.get(), y + tabY.get(), 15, 15, 0, 82, 0, 82+15, Component.literal("Energy Tab"), this::energyTab));
                tabY.addAndGet(17);
            }
        });
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.put("item", new ModifyHandlerPage(screen, strictInventory, x, y, 0, 0));
                buttons.add(new ToggleButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                        x + btnX.get(), y + tabY.get(), 15, 15, 32, 82, 32, 82+15, Component.literal("Item Tab"), this::itemTab));
                tabY.addAndGet(17);
            }
        });
        getScreen().getMenu().getBlockEntity().getCapability(ForgeCapabilities.FLUID_HANDLER).ifPresent(cap -> {
            if (cap instanceof IStrictInventory strictInventory) {
                pages.put("fluid", new ModifyHandlerPage(screen, strictInventory, x, y,0, 0));
                buttons.add(new ToggleButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                        x + btnX.get(), y  + tabY.get(), 15, 15, 16, 82, 16, 82+15, Component.literal("Fluid Tab"), this::fluidTab));
                tabY.addAndGet(17);
            }
        });

        for (ButtonComponent button : buttons) {
            button.setVisible(false);
        }
    }

    public List<ButtonComponent> getButtons() {
        return buttons;
    }

    public Collection<ModifyHandlerPage> getPages() {
        return pages.values();
    }

    public ToggleButtonComponent getSettingsTab() {
        return settingsTab;
    }

    private void itemTab(ButtonComponent buttonComponent) {
        pages.forEach((s, p) -> {
            if (!s.equals("item")) p.setVisible(false);
        });
        for (ButtonComponent button : buttons) {
            if (!button.equals(buttonComponent)) {
                button.setState(false);
            }
        }
        pages.get("item").setVisible(!pages.get("item").visible);

    }

    private void fluidTab(ButtonComponent buttonComponent) {
        pages.forEach((s, p) -> {
            if (!s.equals("fluid")) p.setVisible(false);
        });
        for (ButtonComponent button : buttons) {
            if (!button.equals(buttonComponent)) {
                button.setState(false);
            }
        }
        pages.get("fluid").setVisible(!pages.get("fluid").visible);
    }

    private void energyTab(ButtonComponent buttonComponent) {
        pages.forEach((s, p) -> {
            if (!s.equals("energy")) p.setVisible(false);
        });
        for (ButtonComponent button : buttons) {
            if (!button.equals(buttonComponent)) {
                button.setState(false);
            }
        }
        pages.get("energy").setVisible(!pages.get("energy").visible);
    }
    

    public void togglePage(ButtonComponent button) {
        this.visible = !visible;
        if (this.visible) {
            settingsTab.visible = false;
            for (ButtonComponent bv : buttons) {
                bv.setVisible(true);
                bv.setState(false);
            }
        } else {
            settingsTab.visible = true;
            for (ButtonComponent bv : buttons) {
                bv.setVisible(false);
                bv.setState(false);
            }
            for (ModifyHandlerPage page : pages.values()) {
                page.setVisible(false);
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
    public void onClick(double mouseX, double mouseY, int button) {
        if (this.settingsTab.isMouseOverIgnoreVisible(mouseX, mouseY)) {
            this.settingsTab.onClick(mouseX, mouseY, button);
            return;
        }
        if (this.pages.values().stream().anyMatch(p->p.mouseClicked(mouseX, mouseY, button))) return;
        if (this.buttons.stream().anyMatch(p->p.mouseClicked(mouseX, mouseY, button))) return;
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.draw(graphics);
//        for (ButtonComponent button : buttons) {
//            button.drawComponent(graphics, pMouseX, pMouseY, pPartialTick);
//        }
//        for (ModifyHandlerPage page : pages.values()) {
//            page.render(graphics, pMouseX, pMouseY, pPartialTick);
//        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
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
