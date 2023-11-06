package mod.kerzox.exotek.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.*;
import mod.kerzox.exotek.client.gui.components.page.SettingsPage;
import mod.kerzox.exotek.client.gui.components.page.UpgradePage;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.menu.EngraverMenu;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.Optional;

public abstract class DefaultScreen<T extends DefaultMenu<?>> extends AbstractContainerScreen<T> implements ICustomScreen {

    public static final int DEFAULT_X_POS = 0;
    public static final int DEFAULT_Y_POS = 0;
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;

    protected ResourceLocation texture;
    protected int guiX;
    protected int guiY;
    protected boolean settingsVisible;

    protected SettingsPage<T> settingsPage = new SettingsPage<>(this, 0, -7, 176, 88);
    protected UpgradePage upgradePage = new UpgradePage(this, 176, 29, Component.literal("Word"));

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y, int width, int height) {
        super(pMenu, pPlayerInventory, pTitle);
        this.texture = texture;
        this.guiX = x;
        this.guiY = y;
        this.width = width;
        this.height = height;
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y, int width, int height, boolean settings) {
        super(pMenu, pPlayerInventory, pTitle);
        this.texture = texture;
        this.guiX = x;
        this.guiY = y;
        this.width = width;
        this.height = height;
        this.settingsVisible = settings;
    }

    @Override
    protected void containerTick() {
        getMenu().getUpdateTag();

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TankComponent<?> tanks) {
                tanks.updateState();
            }
            if (renderable instanceof ProgressComponent<?> progressComponent) {
                int totalDuration = getMenu().getUpdateTag().getInt("max_duration");
                int duration = getMenu().getUpdateTag().getInt("duration");

                if (duration > 0) {
                    progressComponent.update(totalDuration - duration, totalDuration, ProgressComponent.Direction.RIGHT);
                } else {
                    progressComponent.update(0, 0, ProgressComponent.Direction.RIGHT);
                }
            }
        }

        if (settingsVisible) settingsPage.tick();

        menuTick();
    }

    protected void onMouseClick(double mouseX, double mouseY, int button) {

    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        if (settingsVisible) {
            settingsPage.mouseClicked(p_97748_, p_97749_, p_97750_);
        }
        if (!settingsPage.visible) {
            for (Renderable renderable : this.renderables) {
                if (renderable instanceof WidgetComponent<?> widgetComponent)
                    widgetComponent.mouseClicked(p_97748_, p_97749_, p_97750_);
            }
        }
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }

    protected void menuTick() {


    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    @Override
    protected void init() {
        super.init();

        if (settingsVisible) {
            settingsPage.updatePositionToScreen();
            addWidget(upgradePage);
            addWidget(upgradePage.getScrollBarComponent());
            for (UpgradeComponent component : upgradePage.getUpgradesInside()) {
                addWidget(component);
            }
        }


        onOpen();

        // call sync
        getMenu().getUpdateTag();

        for (Slot slot : getMenu().slots) {
            if (slot instanceof SlotComponent slotComponent) {
                slotComponent.setPositionProper(this.getGuiLeft(), this.getGuiTop());
            }
            if (slot instanceof UpgradeSlotComponent slotComponent) {
                slotComponent.setActive(false);
            }
        }

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof TankComponent<?> tanks) {
                tanks.updateState();
            }
        }

    }

    protected void onOpen() {

    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y) {
        this(pMenu, pPlayerInventory, pTitle, texture, x, y, 176, 166);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture) {
        this(pMenu, pPlayerInventory, pTitle, texture, DEFAULT_X_POS, DEFAULT_Y_POS, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, String texture) {
        this(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Exotek.MODID, "textures/gui/"+texture), 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, true);
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, String texture, boolean settings) {
        this(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Exotek.MODID, "textures/gui/"+texture), 0, 0, DEFAULT_WIDTH, DEFAULT_HEIGHT, settings);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2 + guiX;
        int j = (this.height - this.imageHeight) / 2 + guiY;
        this.upgradePage.render(graphics, pMouseX, pMouseY, partialTick);
        renderBeforeBackground(graphics, partialTick, pMouseX, pMouseY);
        graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        addToBackground(graphics, partialTick, pMouseX, pMouseY);
    }

    protected void renderBeforeBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    protected abstract void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY);

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        super.render(graphics, pMouseX, pMouseY, partialTick);
        if (!settingsPage.visible) {
            this.renderTooltip(graphics, pMouseX, pMouseY);
            mouseTracked(graphics, pMouseX, pMouseY);
            for (Renderable renderable : this.renderables) {
//                if (renderable instanceof TankComponent<?> tanks) {
//                    if (tanks.isMouseOver(pMouseX, pMouseY)) {
//                        tanks.doHover(graphics, pMouseX, pMouseY);
//                    }
//                }
                if (renderable instanceof WidgetComponent<?> component) {
                    if (component.isMouseOver(pMouseX, pMouseY)) component.doHover(graphics, pMouseX, pMouseY);
                }
            }
            for (Slot slot : getMenu().slots) {
                if (slot instanceof SlotComponent slotComponent) {
                    slotComponent.highlightOnHover(graphics, pMouseX, pMouseY);
                }
            }
        }

        if (settingsVisible) settingsPage.render(graphics, pMouseX, pMouseY, partialTick);
    }

    @Override
    protected void renderTooltip(GuiGraphics p_283594_, int p_282171_, int p_281909_) {
        super.renderTooltip(p_283594_, p_282171_, p_281909_);
        addToForeground(p_283594_, p_282171_, p_281909_);
    }

    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (!settingsPage.visible) {
            for (Renderable renderable : this.renderables) {
                if (renderable instanceof WidgetComponent<?> widgetComponent)
                    widgetComponent.mouseMoved(pMouseX, pMouseY);
            }
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.getChildAt(mouseX, mouseY).filter((p_94708_) -> p_94708_.mouseDragged(mouseX, mouseY, button, dragX, dragY)).isPresent()) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double p_97812_, double p_97813_, int p_97814_) {
        if (!settingsPage.visible) {
            for (Renderable renderable : this.renderables) {
                if (renderable instanceof WidgetComponent<?> widgetComponent)
                    widgetComponent.mouseReleased(p_97812_, p_97813_, p_97814_);
            }
        }
        return super.mouseReleased(p_97812_, p_97813_, p_97814_);
    }

    protected void addWidgetComponent(WidgetComponent<?> widget) {
        addRenderableOnly(widget);
    }

    protected abstract void addToForeground(GuiGraphics graphics, int x, int y);

    @Override
    public T getMenu() {
        return super.getMenu();
    }


}
