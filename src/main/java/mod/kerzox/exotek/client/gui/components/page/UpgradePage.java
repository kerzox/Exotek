package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.NewWidgetComponent;
import mod.kerzox.exotek.client.gui.components.ScrollbarComponent;
import mod.kerzox.exotek.client.gui.components.UpgradeComponent;
import mod.kerzox.exotek.client.gui.components.UpgradeSlotComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class UpgradePage extends NewWidgetComponent {

    private boolean showBackground = false;
    private ResourceLocation texture = new ResourceLocation(Exotek.MODID, "textures/gui/upgrade_page.png");

    private int transitionTime;
    private int tick;
    private int test1;
    private int test2;

    private int scrollIndex = 0;


    private int lerpedX;

    private NonNullList<UpgradeComponent> upgradesInside = NonNullList.createWithCapacity(8);

    private ScrollbarComponent scrollBarComponent =
            new ScrollbarComponent(screen,
                    getCorrectX() + 34, getCorrectY() + 6,
                    4, 4,
                    4, 45,
                    141, 167,
                    0, 45,
                    Component.literal("scrollbar"),
                    new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), this::onScrollBar);

    public UpgradePage(ICustomScreen screen, int x, int y, Component message) {
        super(screen, x, y, 64, 57, message);
        int ySpacer = 0;
        screen.getMenu().getBlockEntity().getCapability(ExotekCapabilities.UPGRADABLE_MACHINE).ifPresent(cap -> {
            for (int i = 0; i < cap.getUpgradeSlots(); i++) {
                upgradesInside.add(new UpgradeComponent(screen, i + 2, getCorrectX() + 4, getCorrectY() + 7, Component.literal("empty")));
            }
        });
    }

    public int getLerpedX() {
        return lerpedX;
    }

    public boolean backgroundShown() {
        return showBackground;
    }

    private void onScrollBar(double x, double y) {
        int scrollbarHeight = 45; // The height of the scrollbar
        int numberOfSteps = upgradesInside.size() - (4 - 1);

        // Calculate the step size
        int stepSize = scrollbarHeight / Math.max(numberOfSteps, 1);

        // Assume the current scrollbar position
        int currentScrollPosition = (int) y - scrollBarComponent.getPrevY(); // Change this value as needed

        // Calculate the current step
        int currentStep = currentScrollPosition / stepSize;

        // Ensure the current step is within the valid range
        currentStep = Math.max(0, Math.min(numberOfSteps, currentStep));

        scrollIndex = currentStep;
    }

    @Override
    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        if (this.active && this.visible) {
            if (this.isValidClickButton(p_93643_)) {
                boolean flag = this.clicked(p_93641_, p_93642_);
                if (flag) {
                    if (!showBackground)this.playDownSound(Minecraft.getInstance().getSoundManager());
                    this.onClick(p_93641_, p_93642_, p_93643_);
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
       if (!showBackground) {
           showBackground = true;
           transitionTime = 20 * 6;
           tick = 0;
           for (Slot slot : screen.getMenu().getSlots()) {
               if (slot instanceof UpgradeSlotComponent slotComponent) slotComponent.setActive(true);
           }
       } else {
           for (Slot slot : screen.getMenu().getSlots()) {
               if (slot instanceof UpgradeSlotComponent slotComponent) slotComponent.setActive(false);
           }
       }
    }

    @Override
    public boolean isMouseOver(double x, double y) {
        if (showBackground) return false;
        boolean xBounds = ((x > getCorrectX()) && (x < getCorrectX() + 5));
        boolean yBounds = ((y > getCorrectY()) && (y < getCorrectY() + 24));
        return this.visible && this.active && xBounds && yBounds;
    }

    public static int lerp(int startValue, int endValue, float t) {
        t = Math.min(1.0f, Math.max(0.0f, t)); // Ensure t is within [0, 1]
        return (int) (startValue + (endValue - startValue) * t);
    }

    @Override
    protected void drawComponent(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (transitionTime > 0) {
            float t = (float) tick / (float) (transitionTime);
            int lerpedValue = lerp(getCorrectX() - width + 5, getCorrectX(), t);
            lerpedX = lerpedValue;
            graphics.blit(texture, lerpedValue, getCorrectY(), 0, 0, width, height, 80, 80);
//            renderUpgrades(graphics, lerpedValue, 0, mouseX, mouseY, partialTick);
            scrollBarComponent.setPosition2(lerpedValue - screen.getGuiLeft() + (34), scrollBarComponent.getY());
            scrollBarComponent.setPosition(lerpedValue - screen.getGuiLeft() + (34), scrollBarComponent.getY());
            scrollBarComponent.render(graphics, mouseX, mouseY, partialTick);
            transitionTime--;
        }
        else if (showBackground) {
            screen.getMenu().getBlockEntity().getCapability(ExotekCapabilities.UPGRADABLE_MACHINE).ifPresent(cap -> {
                for (int i = 2; i < cap.getInventory().getSlots(); i++) {
                    int index = i - 2;
                    upgradesInside.get(index).setItemStack(cap.getInventory().getStackInSlot(i));
                }
            });
            graphics.blit(texture, getCorrectX(), getCorrectY(), 0, 0, width, height, 80, 80);
            scrollBarComponent.render(graphics, mouseX, mouseY, partialTick);
            renderUpgrades(graphics, mouseX, mouseY, partialTick);
        }
        else graphics.blit(texture, getCorrectX(), getCorrectY(), 64, 0, 5, 24, 80, 80);
        tick++;
    }

    private void renderUpgrades(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int x = getCorrectX() + 4;
        int y = getY() + 7;
        int ySpacer = 0;
        for (UpgradeComponent component : upgradesInside) {
            component.setActive(false);
            component.setVisible(false);
        }
        for (int i = scrollIndex; i < Math.min(scrollIndex + 4, upgradesInside.size()); i++) {
            UpgradeComponent component = upgradesInside.get(i);
            component.setActive(true);
            component.setVisible(true);
            component.setY(y + ySpacer);
            component.render(graphics, mouseX, mouseY, partialTick);
            ySpacer += 11;
        }
    }

    public ScrollbarComponent getScrollBarComponent() {
        return scrollBarComponent;
    }

    public List<UpgradeComponent> getUpgradesInside() {
        return upgradesInside;
    }
}
