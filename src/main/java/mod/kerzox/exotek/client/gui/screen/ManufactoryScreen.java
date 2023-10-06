package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.TankComponent;
import mod.kerzox.exotek.client.gui.menu.ManufactoryMenu;
import mod.kerzox.exotek.common.capability.item.CraftingInventoryWrapper;
import mod.kerzox.exotek.common.network.LockRecipePacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.StartRecipePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class ManufactoryScreen extends DefaultScreen<ManufactoryMenu> {

    private ProgressComponent<ManufactoryMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 8, 17, 10, 54, 0, 65, 10, 65);
    private ProgressComponent<ManufactoryMenu> manuBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            106, 30, 12, 12, 128, 79, 140, 79);

    private TankComponent<ManufactoryMenu> inputTank1 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(0), 24, 17, 18, 18, 110, 67, 110, 49);
    private TankComponent<ManufactoryMenu> inputTank2 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(1),24, 17+18, 18, 18, 110, 67, 110, 49);
    private TankComponent<ManufactoryMenu> inputTank3 = new TankComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            getMenu().getBlockEntity().getSidedMultifluidTank().getInputHandler().getStorageTank(2),24, 17+18+18, 18, 18, 110, 67, 110, 49);

    private ToggleButtonComponent<ManufactoryMenu> lockButton = new ToggleButtonComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            100, 59, 12, 12, 24, 217, 24, 217+12, button -> lockRecipe());
    private ToggleButtonComponent<ManufactoryMenu> startButton = new ToggleButtonComponent<>(this,
            new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
            100, 59-12, 12, 12, 12, 217, 12, 217+12, button -> startRecipe());

    private void startRecipe() {
        PacketHandler.sendToServer(new StartRecipePacket());
    }

    private void lockRecipe() {
        PacketHandler.sendToServer(new LockRecipePacket());
    }

    public ManufactoryScreen(ManufactoryMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "manufactory.png");
    }

    @Override
    protected void onOpen() {
        addWidgetComponent(energyBar);
        addWidgetComponent(inputTank1);
        addWidgetComponent(inputTank2);
        addWidgetComponent(inputTank3);
        addWidgetComponent(lockButton);
        addWidgetComponent(startButton);
        addWidgetComponent(manuBar);
        lockButton.setState(getMenu().getBlockEntity().isLocked());
        startButton.setState(!getMenu().getBlockEntity().isStalled());
        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);
    }


    @Override
    protected void menuTick() {

        energyBar.update(
                getMenu().getUpdateTag().getCompound("energyHandler").getCompound("output").getInt("energy"),
                getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).map(IEnergyStorage::getMaxEnergyStored).orElse(0), ProgressComponent.Direction.UP);

    }

    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (energyBar.isMouseOver(pMouseX, pMouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.literal("Stored Energy: " + this.energyBar.getMinimum())), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

        Optional<CraftingRecipe> recipe2 =
                Minecraft.getInstance().level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,
                        getMenu().getBlockEntity().getCraftingInventoryWrapper(),
                        Minecraft.getInstance().level);

        if (recipe2.isPresent()) {
            graphics.pose().pushPose();
            graphics.renderItem(recipe2.get().getResultItem(RegistryAccess.EMPTY), this.getGuiLeft()+ 130, this.getGuiTop()+36);
            graphics.pose().popPose();
        }
    }

}
