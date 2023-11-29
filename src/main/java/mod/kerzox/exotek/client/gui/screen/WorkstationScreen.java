package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ContainerSlotComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.page.BasicPage;
import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class WorkstationScreen extends DefaultScreen<WorkstationMenu> {

    private ResourceLocation blueprint = new ResourceLocation(Exotek.MODID, "textures/gui/workstation.png");
    private ResourceLocation withOutBlueprint = new ResourceLocation(Exotek.MODID, "textures/gui/workstation_crafting.png");

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
    }

    private void switchTabs(ButtonComponent buttonComponent, int i) {
        if (i == 0) {
            setTexture(blueprint);
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
            }
        } else {
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

        switchTabs(null, getMenu().getBlockEntity().getTab());
    }

    @Override
    protected void menuTick() {


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
