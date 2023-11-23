package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.menu.BurnableGeneratorMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class BurnableGeneratorScreen extends DefaultScreen<BurnableGeneratorMenu> {

    public BurnableGeneratorScreen(BurnableGeneratorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "burnable_generator.png");
    }

    @Override
    protected void onOpen() {

    }

    @Override
    protected void menuTick() {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {


    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
