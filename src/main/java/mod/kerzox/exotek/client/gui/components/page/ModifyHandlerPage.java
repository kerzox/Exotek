package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.HandlerSlotButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import mod.kerzox.exotek.common.capability.ICapabilityIO;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.UpdateHandlerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.*;

// this is really just a collection of buttons
public class ModifyHandlerPage extends BasicPage {

    private List<ToggleButtonComponent> buttons = new ArrayList<>();
    private Map<Direction, HandlerSlotButtonComponent> directionalButtons = new HashMap<>();
    protected IStrictInventory serializer;

    private Direction currentDirection = Direction.NORTH;

    public ModifyHandlerPage(ICustomScreen screen, IStrictInventory cap, int x, int y, int width, int height) {
        super(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"), x, y, width, height, 0, 0, 0, 0, Component.literal("Modify Handler"));
        this.serializer = cap;
        visible = false;

        int x1 = 36 + x;
        int y1 = 38 + y;

        if (serializer instanceof ICapabilityIO ioSeralizer) {
            buttons.add(new ToggleButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                    x1, 8 + y, 12, 12, 36, 217, 36, 217+12, Component.literal("Push Button"), b -> push(b, ioSeralizer)) {
                @Override
                protected List<Component> getComponents() {
                    return List.of(Component.literal("Push into inventories"));
                }
            });

            buttons.add(new ToggleButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                    x1+12, 8 + y, 12, 12, 48, 217, 48, 217+12, Component.literal("Extract Button"), b -> extract(b, ioSeralizer)) {
                @Override
                protected List<Component> getComponents() {
                    return List.of(Component.literal("Extract from inventories"));
                }
            });
        }

        Direction[] dir = getDirectionFromFacing(Minecraft.getInstance().player.getDirection());

        directionalButtons.put(Direction.UP, new HandlerSlotButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), Direction.UP,
                x1 + 12, y1, 12, 12, 36, 112, 36, 112, serializer));

        directionalButtons.put(dir[3], new HandlerSlotButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), dir[3],
                x1, y1 + 12, 12, 12, 36, 112, 36, 112, serializer));

        directionalButtons.put(dir[2], new HandlerSlotButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), dir[2],
                x1, y1 + (12 * 2), 12, 12, 36, 112, 36, 112, serializer));

        directionalButtons.put(dir[0], new HandlerSlotButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), dir[0],
                x1 + 12, y1 + 12, 12, 12, 36, 112, 36, 112,  serializer));

        directionalButtons.put(Direction.DOWN, new HandlerSlotButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), Direction.DOWN,
                x1 + 12, y1 + (12 * 2), 12, 12, 36, 112, 36, 112, serializer));

        directionalButtons.put(dir[1], new HandlerSlotButtonComponent(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"), dir[1],
                x1 + (12 * 2), y1 + 12, 12, 12, 36, 112, 36, 112, serializer));
    }

    public void update(HashSet<Direction> inputs, HashSet<Direction> outputs) {
        directionalButtons.forEach((direction, tHandlerSlotButtonComponent) -> tHandlerSlotButtonComponent.update(inputs, outputs, direction));
        if (serializer instanceof ICapabilityIO io) {
            switch (io.getIOSetting()) {
                case DEFAULT -> {
                    for (ToggleButtonComponent button : buttons) {
                        button.setState(false);
                    }
                }
                case ALL -> {
                    for (ToggleButtonComponent button : buttons) {
                        button.setState(true);
                    }
                }
                case PUSH -> {
                    buttons.get(0).setState(true);
                    buttons.get(1).setState(false);
                }
                case EXTRACT -> {
                    buttons.get(0).setState(false);
                    buttons.get(1).setState(true);
                }
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

    private void push(ButtonComponent button, ICapabilityIO ioSeralizer) {
        if (button instanceof ToggleButtonComponent toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                if (ioSeralizer.getIOSetting() == IOTypes.EXTRACT) {
                    ioSeralizer.setIOSetting(IOTypes.ALL);
                } else ioSeralizer.setIOSetting(IOTypes.PUSH);
            } else {
                if (ioSeralizer.getIOSetting() == IOTypes.ALL) {
                    ioSeralizer.setIOSetting(IOTypes.EXTRACT);
                } else ioSeralizer.setIOSetting(IOTypes.DEFAULT);
            }
        }
        CompoundTag tag = getScreen().getMenu().getUpdateTag();
        PacketHandler.sendToServer(new UpdateHandlerPacket(tag));
    }

    private void extract(ButtonComponent button, ICapabilityIO ioSeralizer) {
        if (button instanceof ToggleButtonComponent toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                if (ioSeralizer.getIOSetting() == IOTypes.PUSH) {
                    ioSeralizer.setIOSetting(IOTypes.ALL);
                } else ioSeralizer.setIOSetting(IOTypes.EXTRACT);
            } else {
                if (ioSeralizer.getIOSetting() == IOTypes.ALL) {
                    ioSeralizer.setIOSetting(IOTypes.PUSH);
                } else ioSeralizer.setIOSetting(IOTypes.DEFAULT);
            }
        }
        CompoundTag tag = getScreen().getMenu().getUpdateTag();
        PacketHandler.sendToServer(new UpdateHandlerPacket(tag));
    }


    public boolean mouseClicked(double p_93641_, double p_93642_, int p_93643_) {
        if (this.active && this.visible) {
            if (directionalButtons.values().stream().anyMatch(b -> b.mouseClicked(p_93641_, p_93642_, p_93643_))) return true;
            return buttons.stream().anyMatch(b -> b.mouseClicked(p_93641_, p_93642_, p_93643_));
        } else {
            return false;
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {


    }

    @Override
    public void drawComponent(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.buttons.forEach(tToggleButtonComponent -> {
            tToggleButtonComponent.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        });
        this.directionalButtons.forEach((d, b) -> b.render(pPoseStack, pMouseX, pMouseY, pPartialTick));
    }
}
