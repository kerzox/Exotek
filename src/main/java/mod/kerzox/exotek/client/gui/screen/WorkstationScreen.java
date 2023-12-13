package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.*;
import mod.kerzox.exotek.client.gui.components.page.BasicPage;
import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.IBlueprint;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.item.BlueprintValidatorItem;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.CreateBlueprintItem;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.ArrayList;
import java.util.List;

public class WorkstationScreen extends DefaultScreen<WorkstationMenu> {

    private ResourceLocation blueprint = new ResourceLocation(Exotek.MODID, "textures/gui/workstation.png");
    private ResourceLocation withOutBlueprint = new ResourceLocation(Exotek.MODID, "textures/gui/workstation_crafting.png");

    private ScrollbarComponent scrollBarComponent =
            new ScrollbarComponent(this,
                    90, 27,
                    4, 4,
                    4, 40,
                    141, 167,
                    0, 40,
                    Component.literal("scrollbar"),
                    new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), this::onScrollBar);


    private List<ButtonComponent> blueprints = new ArrayList<>();
    private int scrollIndex = 0;

    private void onScrollBar(double x, double y) {
        int scrollbarHeight = 40; // The height of the scrollbar
        int numberOfSteps = blueprints.size() - 1;

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

    protected BasicPage colourTab = new BasicPage(this, new ResourceLocation(Exotek.MODID, "textures/gui/workstation_booklet.png"),
            154, 15, 24, 33, 0, 0, 0, 0, Component.literal("Colour Tabs")) {

        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
            if (getMenu().getBlockEntity().getTab() == 0) {
                setTextureOffset(0, 33);
            } else {
                setTextureOffset(0, 0);
            }
            this.draw(graphics, getCorrectX(), getCorrectY(), this.width, this.height, 24, 66);
        }

        @Override
        public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
            if (lockButton.mouseClicked(p_93641_, p_93642_, p_93643_)) return true;
            else if (lockButton2.mouseClicked(p_93641_, p_93642_, p_93643_)) return true;
            else if (lockButton3.mouseClicked(p_93641_, p_93642_, p_93643_)) return true;
            else return lockButton4.mouseClicked(p_93641_, p_93642_, p_93643_);
        }
    };

    private ButtonComponent lockButton = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            159, 15, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Blueprint"), (button -> switchTabs(button, 0))) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }

        @Override
        protected List<Component> getComponents() {
            return List.of(Component.literal("Blueprint Tab"));
        }
    };

    private ButtonComponent lockButton2 = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            159 + 4, 15 + 8, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"), buttonComponent ->
            switchTabs(buttonComponent, 1)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }

        @Override
        protected List<Component> getComponents() {
            return List.of(Component.literal("Crafting Tab"));
        }
    };

    private ButtonComponent lockButton3 = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            159 + 4 + 4, 15 + 8 + 8,  9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"), buttonComponent ->
            switchTabs(buttonComponent, 2)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };


    private ButtonComponent lockButton4 = new ToggleButtonComponent(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            159 + 4 + 4 + 4, 15 + 8 + 8 + 8, 9, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"),
            buttonComponent -> switchTabs(buttonComponent, 3)) {
        @Override
        public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {

        }
    };


    public WorkstationScreen(WorkstationMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "workstation.png", false);
        int x = 24;
        int y = 26;
        for (IBlueprint blueprint : MultiblockValidator.getBlueprints().values()) {
            if (blueprint.getMultiblockName().contains("generated")) continue;
            blueprints.add(new ButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                    x, y, 66, 8, 0, 217, 0, 217, Component.literal("Switch Tab To Crafting"),
                    buttonComponent -> onBlueprintClicked(buttonComponent, blueprint)) {
                @Override
                protected List<Component> getComponents() {
                    return List.of(Component.literal("Blueprint: " + blueprint.getMultiblockName()));
                }

                @Override
                public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
                    FormattedCharSequence title = Component.literal(blueprint.getMultiblockName()).getVisualOrderText();
                    graphics.drawString(Minecraft.getInstance().font, title, getCorrectX() + ((66 / 2) - Minecraft.getInstance().font.width(title) / 2),
                            getCorrectY(), 4210752, false);

                }
            });
            y += 10;
        }
        scrollBarComponent.setVisible(false);

    }


    private void onBlueprintClicked(ButtonComponent buttonComponent, IBlueprint blueprint) {
        PacketHandler.sendToServer(new CreateBlueprintItem(blueprint.getMultiblockName(), 9));
    }

    private void switchTabs(ButtonComponent buttonComponent, int i) {
        if (i == 0) {
            for (Slot slot : getMenu().getSlots()) {
                if (slot instanceof ModifiedResultSlot<?> slot1) {
                    slot1.setActive(false);
                    slot1.blockPickup(true);
                    slot1.blockPlace(true);
                }
            }
            setTexture(blueprint);
            scrollBarComponent.setVisible(true);
            PacketHandler.sendToServer(new CompoundTagPacket("tab", 0));
            //PacketHandler.sendToServer(new ContainerSlotPacket(true));
            for (int i1 = 0; i1 < getMenu().getSlots().size(); i1++) {
                Slot slot = getMenu().getSlot(i1);
                if (slot instanceof ContainerSlotComponent component && i1 != 45)
                {
                    component.blockPickup(true);
                    component.blockPlace(true);
                    component.setActive(false);
                }
                if (slot.getSlotIndex() == getMenu().getOutputSlot().getSlotIndex() && slot instanceof SlotComponent slotComponent) {
                    slotComponent.setActive(true);
                    slotComponent.blockPickup(false);
                    slotComponent.blockPlace(false);
                }
            }
        } else {
            for (Slot slot : getMenu().getSlots()) {
                if (slot instanceof ModifiedResultSlot<?> slot1) {
                    slot1.setActive(true);
                    slot1.blockPickup(false);
                    slot1.blockPlace(false);
                }
                if (slot.getSlotIndex() == getMenu().getOutputSlot().getSlotIndex() && slot instanceof SlotComponent slotComponent) {
                    slotComponent.setActive(false);
                    slotComponent.blockPickup(true);
                    slotComponent.blockPlace(true);
                }
            }
            for (ButtonComponent component : blueprints) {
                component.setActive(false);
                component.setVisible(false);
            }
            scrollBarComponent.setVisible(false);
            setTexture(withOutBlueprint);
            PacketHandler.sendToServer(new CompoundTagPacket("tab", 1));
            //PacketHandler.sendToServer(new ContainerSlotPacket(false));
            for (Slot slot : getMenu().getSlots()) {
                if (slot instanceof ContainerSlotComponent component)
                {
                    component.blockPickup(false);
                    component.blockPlace(false);
                    component.setActive(true);
                }
            }
        }
    }

    @Override
    protected void onOpen() {
        addBackgroundWidget(colourTab);
        addRenderableWidget(lockButton);
        addRenderableWidget(lockButton2);
        addRenderableWidget(lockButton3);
        addRenderableWidget(lockButton4);
        addRenderableWidget(scrollBarComponent);
        for (ButtonComponent component : blueprints) {
            addRenderableWidget(component);
        }

        switchTabs(null, getMenu().getBlockEntity().getTab());
    }

    @Override
    protected void menuTick() {
        if (getMenu().getBlockEntity().getTab() == 0) {
            int x = 24;
            int y = 27;
            int ySpacer = 0;
            for (ButtonComponent component : blueprints) {
                component.setActive(false);
                component.setVisible(false);
            }
            for (int i = scrollIndex; i < Math.min(scrollIndex + 4, blueprints.size()); i++) {
                ButtonComponent component = blueprints.get(i);
                component.setActive(true);
                component.setVisible(true);
                component.setY(y + ySpacer);
                ySpacer += 10;
            }
        } else {
            for (ButtonComponent component : blueprints) {
                component.setActive(false);
                component.setVisible(false);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }


    protected void renderBeforeBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {
        //colourTab.drawComponent(graphics, pMouseX, pMouseY, partialTick);
    }


    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
//        if (getMenu().getBlockEntity().getTab() == 1) {
//            Optional<WorkstationRecipe> recipe =
//                    Minecraft.getInstance().level.getRecipeManager().getRecipeFor(Registry.WORKSTATION_RECIPE.get(),
//                            getMenu().getBlockEntity().getRecipeInventoryWrapper(),
//                            Minecraft.getInstance().level);
//
//            if (recipe.isPresent()) {
//                graphics.pose().pushPose();
//                graphics.renderItem(recipe.get().getResultItem(RegistryAccess.EMPTY), this.getGuiLeft()+ 119, this.getGuiTop()+38);
//                graphics.pose().popPose();
//            }
//        }
    }

}
