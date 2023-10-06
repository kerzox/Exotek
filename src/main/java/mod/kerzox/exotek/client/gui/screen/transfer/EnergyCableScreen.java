package mod.kerzox.exotek.client.gui.screen.transfer;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.transfer.EnergyCableMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.ArrayList;
import java.util.List;

public class EnergyCableScreen extends DefaultScreen<EnergyCableMenu> {

    private List<ButtonComponent<EnergyCableMenu>> buttons = new ArrayList<>();
    private List<ButtonComponent<EnergyCableMenu>> directionalButtons = new ArrayList<>();

    public EnergyCableScreen(EnergyCableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "cable.png");

        buttons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                8, 19, 12, 12, 36, 217, 36, 217+12, button -> extract()));

        buttons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                8+12, 19, 12, 12, 48, 217, 48, 217+12, button -> extract()));

        int x = 15;
        int y = -8;

        Direction[] dir = getDirectionFromFacing(Minecraft.getInstance().player.getDirection());

        directionalButtons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+24, y +45, 15, 15, 48, 88, 64, 88, button -> toggleDirection(button, dir[0])));

        directionalButtons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+40, y +45, 15, 15, 48, 88, 64, 88, button -> toggleDirection(button, dir[1])));

        directionalButtons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+8, y +61, 15, 15, 48, 88, 64, 88, button -> toggleDirection(button, dir[2])));

        directionalButtons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+8, y +45, 15, 15, 48, 88, 64, 88, button -> toggleDirection(button, dir[3])));

        directionalButtons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+24, y +29, 15, 15, 48, 88, 64, 88, button -> toggleDirection(button, Direction.UP)));

        directionalButtons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+ 24, y +61, 15, 15, 48, 88, 64, 88, button -> toggleDirection(button, Direction.DOWN)));

        this.getMenu().getBlockEntity().getLevel().getCapability(ExotekCapabilities.LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {

            if (cap instanceof LevelEnergyNetwork network) {

            }

        });

    }

    private void toggleDirection(ButtonComponent<?> button, Direction direction) {
        for (ButtonComponent<EnergyCableMenu> btn : directionalButtons) {
            if (button != btn) {
                btn.setState(false);
            }
        }
    }

    private Direction[] getDirectionFromFacing(Direction facing) {
        Direction[] dir = new Direction[Direction.values().length];
        if (facing == Direction.SOUTH) {
            dir[0] = Direction.NORTH;
            dir[1] = Direction.WEST;
            dir[2] = Direction.SOUTH;
            dir[3] = Direction.EAST;
        }
        if (facing == Direction.NORTH) {
            dir[0] = Direction.SOUTH;
            dir[1] = Direction.EAST;
            dir[2] = Direction.NORTH;
            dir[3] = Direction.WEST;
        }
        if (facing == Direction.EAST) {
            dir[0] = Direction.WEST;
            dir[1] = Direction.SOUTH;
            dir[2] = Direction.EAST;
            dir[3] = Direction.NORTH;
        }
        if (facing == Direction.WEST) {
            dir[0] = Direction.EAST;
            dir[1] = Direction.NORTH;
            dir[2] = Direction.WEST;
            dir[3] = Direction.SOUTH;
        }
        dir[4] = Direction.UP;
        dir[5] = Direction.DOWN;
        return dir;
    }

    private void extract() {

    }

    @Override
    protected void onOpen() {
        for (ButtonComponent<EnergyCableMenu> button : buttons) {
            addWidgetComponent(button);
        }
        for (ButtonComponent<EnergyCableMenu> directionalButton : directionalButtons) {
            addWidgetComponent(directionalButton);
        }
    }

    @Override
    protected void menuTick() {

    }


    @Override
    protected void mouseTracked(GuiGraphics graphics, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }
}
