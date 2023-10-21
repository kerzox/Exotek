package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.HandlerSlotButtonComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.capability.ICapabilitySerializer;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.UpdateHandlerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

// this is really just a collection of buttons
public class ModifyHandlerPage<T extends DefaultMenu<?>> extends BasicPage<T> {

    // all settings and the close button
    private Map<Direction, HandlerSlotButtonComponent<T>> buttons = new HashMap<>();

    protected IStrictInventory serializer;

    public ModifyHandlerPage(DefaultScreen<T> screen, IStrictInventory cap, int x, int y, int width, int height) {
        super(screen, x, y, width, height, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"));
        this.serializer = cap;
        visible = false;
        Direction[] dir = getDirectionFromFacing(Minecraft.getInstance().player.getDirection());
        buttons.put(dir[0],new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+24, y +45, 15, 15, 48, 88, btn -> modify(btn, dir[0])));

        buttons.put(dir[1],new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+40, y +45, 15, 15, 48, 88, btn -> modify(btn,dir[1])));

        buttons.put(dir[2],new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+8, y +61, 15, 15, 48, 88, btn -> modify(btn, dir[2])));

        buttons.put(dir[3],new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+8, y +45, 15, 15, 48, 88, btn -> modify(btn, dir[3])));

        buttons.put(Direction.UP,new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+24, y +29, 15, 15, 48, 88, btn -> modify(btn, Direction.UP)));

        buttons.put(Direction.DOWN,new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"),
                x+ 24, y +61, 15, 15, 48, 88, btn -> modify(btn, Direction.DOWN)));
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

    public Map<Direction, HandlerSlotButtonComponent<T>> getButtons() {
        return buttons;
    }

    public void update(HashSet<Direction> inputs, HashSet<Direction> outputs) {
        buttons.forEach((direction, tHandlerSlotButtonComponent) -> tHandlerSlotButtonComponent.update(inputs, outputs, direction));
    }

    @Override
    public DefaultScreen<T> getScreen() {
        return (DefaultScreen<T>) super.getScreen();
    }

    private void modify(ButtonComponent<?> btn, Direction direction) {
        System.out.println(direction);
        if (btn instanceof HandlerSlotButtonComponent<?> slotButtonComponent) {
            HandlerSlotButtonComponent.Mode currentMode = slotButtonComponent.getMode();
            if (currentMode == HandlerSlotButtonComponent.Mode.NONE) {
                serializer.addInput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.INPUT) {
                serializer.removeInputs(direction);
                serializer.addOutput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.OUTPUT) {
                serializer.addOutput(direction);
                serializer.addInput(direction);
            }
            else if (currentMode == HandlerSlotButtonComponent.Mode.UNIVERSAL) {
                serializer.removeInputs(direction);
                serializer.removeOutputs(direction);
            }
            CompoundTag tag = getScreen().getMenu().getUpdateTag();
            PacketHandler.sendToServer(new UpdateHandlerPacket(tag));
        }

    }

    @Override
    public boolean mouseClicked(double p_94737_, double p_94738_, int p_94739_) {
        if (!this.visible) return super.mouseClicked(p_94737_, p_94738_, p_94739_);
        buttons.forEach((d, b) -> b.mouseClicked(p_94737_, p_94738_, p_94739_));
        return super.mouseClicked(p_94737_, p_94738_, p_94739_);
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        this.draw(graphics, this.x, this.y, this.width, this.height);
        for (HandlerSlotButtonComponent<T> button : getButtons().values()) {
            button.updatePositionToScreen();
            button.drawComponent(graphics, pMouseX, pMouseY, pPartialTick);
        }
    }
}
