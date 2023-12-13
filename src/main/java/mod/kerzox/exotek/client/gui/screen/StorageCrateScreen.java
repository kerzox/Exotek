package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.components.page.BasicPage;
import mod.kerzox.exotek.client.gui.components.page.ItemSlotPage;
import mod.kerzox.exotek.client.gui.menu.FluidTankMenu;
import mod.kerzox.exotek.client.gui.menu.StorageCrateMenu;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.util.MachineTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class StorageCrateScreen extends DefaultScreen<StorageCrateMenu> {

    List<ItemSlotPage> pages = new ArrayList<>();
    private int currentPageIndex;

    private ButtonComponent backArrow = new ButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            143, 129, 12, 12, 120, 217, 120, 217 + 12, Component.literal("Back Button"), button -> onBackArrow());

    private void onBackArrow() {
        if (currentPageIndex == 0) return;
        pages.get(currentPageIndex).closePage();
        currentPageIndex--;
        forwardArrow.setVisible(true);
        if (currentPageIndex == 0) backArrow.setVisible(false);
        pages.get(currentPageIndex).openPage();
    }

    private ButtonComponent forwardArrow = new ButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            157, 129, 12, 12, 12, 217, 12, 217 + 12, Component.literal("Forward Button"), button -> onForwardArrow());

    private void onForwardArrow() {
        if (getMenu().getTier() == MachineTier.BASIC) {
            if (currentPageIndex == 1) {
                return;
            }
            pages.get(currentPageIndex).closePage();
            currentPageIndex++;
            if (currentPageIndex == 1) forwardArrow.setVisible(false);
        } else if (getMenu().getTier() == MachineTier.ADVANCED) {
            if (currentPageIndex == 2) {
                return;
            }
            pages.get(currentPageIndex).closePage();
            currentPageIndex++;
            if (currentPageIndex == 2) forwardArrow.setVisible(false);
        } else if (getMenu().getTier() == MachineTier.SUPERIOR) {
            if (currentPageIndex == 3) {
                return;
            }
            pages.get(currentPageIndex).closePage();
            currentPageIndex++;
            if (currentPageIndex == 3) forwardArrow.setVisible(false);
        }
        pages.get(currentPageIndex).openPage();
        backArrow.setVisible(true);
    }

    public StorageCrateScreen(StorageCrateMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "storage_crate.png", 176, 228, true);
        currentPageIndex = 0;
        backArrow.setVisible(false);
        pages.add(new ItemSlotPage(pMenu.getPageSlots().get(MachineTier.DEFAULT), 0, this, 0, 0));
        pages.add(new ItemSlotPage(pMenu.getPageSlots().get(MachineTier.BASIC), StorageCrateMenu.PAGE_TWO_INDEX, this, 0, 0));
        pages.add(new ItemSlotPage(pMenu.getPageSlots().get(MachineTier.ADVANCED), StorageCrateMenu.PAGE_THREE_INDEX, this, 0, 0));
        pages.add(new ItemSlotPage(pMenu.getPageSlots().get(MachineTier.SUPERIOR), StorageCrateMenu.PAGE_FOUR_INDEX, this, 0, 0));
    }



    @Override
    protected void onOpen() {
        if (getMenu().getTier() != MachineTier.DEFAULT) {
            addRenderableWidget(backArrow);
            addRenderableWidget(forwardArrow);
        }
        for (ItemSlotPage page : pages) {
            addRenderableWidget(page);
            page.closePage();
        }
        pages.get(0).openPage();
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }

    @Override
    protected void menuTick() {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        FormattedCharSequence title = Component.literal("Page Number: " + currentPageIndex).getVisualOrderText();
        graphics.drawString(this.font, title, getGuiLeft() + (7), getGuiTop() + 130, 4210752, false);
    }

}
