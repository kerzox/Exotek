package mod.kerzox.exotek.client.gui.screen;

import com.google.common.collect.Lists;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.*;
import mod.kerzox.exotek.client.gui.components.page.ModifyHandlerPage;
import mod.kerzox.exotek.client.gui.components.page.SettingsPage;
import mod.kerzox.exotek.client.gui.components.page.UpgradePage;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.common.util.MachineTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.Arrays;
import java.util.List;

public abstract class DefaultScreen<T extends DefaultMenu<?>> extends AbstractContainerScreen<T> implements ICustomScreen {


    public final List<Renderable> backgroundRenderables = Lists.newArrayList();
    public static final int DEFAULT_X_POS = 0;
    public static final int DEFAULT_Y_POS = 0;
    public static final int DEFAULT_WIDTH = 176;
    public static final int DEFAULT_HEIGHT = 166;

    protected ResourceLocation texture;
    protected int guiX;
    protected int guiY;
    protected boolean settingsVisible;

    protected SettingsPage settingsPage = new SettingsPage(this, -82, 5, 82, 82);
    protected UpgradePage upgradePage = new UpgradePage(this, 176, 5, Component.literal("Word"));

    protected final ResourceLocation BASIC_SLOT_UPGRADE_TEXTURE_DOWN_ARR = new ResourceLocation(Exotek.MODID, "textures/gui/three_slot_arrdown.png");
    protected final ResourceLocation ADV_SLOT_UPGRADE_TEXTURE_DOWN_ARR = new ResourceLocation(Exotek.MODID, "textures/gui/five_slot_arrdown.png");
    protected final ResourceLocation SUP_SLOT_UPGRADE_TEXTURE_DOWN_ARR = new ResourceLocation(Exotek.MODID, "textures/gui/seven_slot_arrdown.png");

    protected void setTextureFromTier(MachineTier tier) {
        switch (tier) {
            case BASIC -> setTexture(BASIC_SLOT_UPGRADE_TEXTURE_DOWN_ARR);
            case ADVANCED -> setTexture(ADV_SLOT_UPGRADE_TEXTURE_DOWN_ARR);
            case SUPERIOR -> setTexture(SUP_SLOT_UPGRADE_TEXTURE_DOWN_ARR);
            default -> setTexture(texture);
        }
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y, int width, int height) {
        super(pMenu, pPlayerInventory, pTitle);
        this.texture = texture;
        this.guiX = x;
        this.guiY = y;
        this.width = width;
        this.height = height;
        this.imageWidth = width;
        this.imageHeight = height;
    }

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, ResourceLocation texture, int x, int y, int width, int height, boolean settings) {
        this(pMenu, pPlayerInventory, pTitle, texture, x, y, width, height);
        this.settingsVisible = settings;
    }

    public SettingsPage getSettingsPage() {
        return settingsPage;
    }

    public UpgradePage getUpgradePage() {
        return upgradePage;
    }

    public boolean isSettingsVisible() {
        return settingsVisible;
    }

    @Override
    protected void containerTick() {
        getMenu().getUpdateTag();

        for (Renderable renderable : this.renderables) {
            if (renderable instanceof NewWidgetComponent component) component.tick();
        }

        if (settingsVisible) settingsPage.tick();
        menuTick();
    }

    @Override
    public boolean mouseClicked(double p_97748_, double p_97749_, int p_97750_) {
        return super.mouseClicked(p_97748_, p_97749_, p_97750_);
    }

    protected void menuTick() {


    }

    public void setTexture(ResourceLocation texture) {
        this.texture = texture;
    }

    protected <T extends GuiEventListener & Renderable & NarratableEntry> T addBackgroundWidget(T p_169406_) {
        this.backgroundRenderables.add(p_169406_);
        return this.addWidget(p_169406_);
    }

    @Override
    protected void init() {
        super.init();

        if (settingsVisible) {
            addRenderableWidget(settingsPage);
            addRenderableWidget(settingsPage.getSettingsTab());

            for (ModifyHandlerPage page : settingsPage.getPages()) {
                addRenderableWidget(page);
            }

            for (ButtonComponent button : settingsPage.getButtons()) {
                addRenderableWidget(button);
            }
        }

        if (getMenu().noUpgrades()) {
            this.upgradePage.visible = false;
        } else {
            addBackgroundWidget(upgradePage);
            addBackgroundWidget(upgradePage.getScrollBarComponent());
            for (UpgradeComponent component : upgradePage.getUpgradesInside()) {
                addRenderableWidget(component);
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

        for (GuiEventListener child : children()) {
            if (child instanceof NewWidgetComponent widgetComponent) widgetComponent.onInit();
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

    public DefaultScreen(T pMenu, Inventory pPlayerInventory, Component pTitle, String texture, int width, int height, boolean settings) {
        this(pMenu, pPlayerInventory, pTitle, new ResourceLocation(Exotek.MODID, "textures/gui/"+texture), 0, 0, width, height, settings);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        int i = (this.width - this.imageWidth) / 2 + guiX;
        int j = (this.height - this.imageHeight) / 2 + guiY;
        //this.upgradePage.render(graphics, pMouseX, pMouseY, partialTick);
        for (Renderable renderable : backgroundRenderables) {
            renderable.render(graphics, pMouseX, pMouseY, partialTick);
        }
        renderBeforeBackground(graphics, partialTick, pMouseX, pMouseY);
        graphics.blit(texture, i, j, 0, 0, this.imageWidth, this.imageHeight);
        addToBackground(graphics, partialTick, pMouseX, pMouseY);
    }

    protected void renderBeforeBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    protected abstract void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY);

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        super.renderBackground(graphics);
        super.render(graphics, pMouseX, pMouseY, partialTick);
        this.renderTooltip(graphics, pMouseX, pMouseY);
        for (Slot slot : getMenu().slots) {
            if (slot instanceof SlotComponent slotComponent) {
                slotComponent.highlightOnHover(graphics, pMouseX, pMouseY);
            }
        }
    }

    @Override
    protected void renderTooltip(GuiGraphics p_283594_, int p_282171_, int p_281909_) {
        super.renderTooltip(p_283594_, p_282171_, p_281909_);
        addToForeground(p_283594_, p_282171_, p_281909_);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.getChildAt(mouseX, mouseY).filter((p_94708_) -> p_94708_.mouseDragged(mouseX, mouseY, button, dragX, dragY)).isPresent()) return true;
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    protected abstract void addToForeground(GuiGraphics graphics, int x, int y);

    @Override
    public T getMenu() {
        return super.getMenu();
    }


}
