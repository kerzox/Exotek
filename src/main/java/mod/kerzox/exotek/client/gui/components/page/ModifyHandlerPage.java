package mod.kerzox.exotek.client.gui.components.page;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.HandlerSlotButtonComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import mod.kerzox.exotek.common.capability.IStrictInventory;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.network.UpdateHandlerPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.royawesome.jlibnoise.module.combiner.Min;

import java.util.*;

// this is really just a collection of buttons
public class ModifyHandlerPage<T extends DefaultMenu<?>> extends BasicPage<T> {

    private List<ToggleButtonComponent<T>> buttons = new ArrayList<>();
    private Map<Direction, HandlerSlotButtonComponent<T>> directionalButtons = new HashMap<>();
    protected IStrictInventory serializer;

    private Direction currentDirection = Direction.NORTH;

    public ModifyHandlerPage(DefaultScreen<T> screen, IStrictInventory cap, int x, int y, int width, int height) {
        super(screen, x, y, width, height, new ResourceLocation(Exotek.MODID, "textures/gui/settings.png"));
        this.serializer = cap;
        visible = false;

        int x1 = 36 + x;
        int y1 = 38 + y;

        buttons.add(new ToggleButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x1, 8 + y, 12, 12, 36, 217, 36, 217+12, this::push));

        buttons.add(new ToggleButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x1+12, 8 + y, 12, 12, 48, 217, 48, 217+12, this::extract));

        buttons.add(new ToggleButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x1+(12*2), 8 + y, 12, 12, 72, 217, 72, 217+12, this::disabled));

        Direction[] dir = getDirectionFromFacing(Minecraft.getInstance().player.getDirection());

        directionalButtons.put(Direction.UP, new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                x1 + 12, y1, 12, 12, 36, 112, 36, 112, button -> modify(button, Direction.UP)));

        directionalButtons.put(dir[3], new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                x1, y1 + 12, 12, 12, 36, 112, 36, 112, button -> modify(button, dir[3])));

        directionalButtons.put(dir[2], new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                x1, y1 + (12 * 2), 12, 12, 36, 112, 36, 112, button -> modify(button, dir[2])));

        directionalButtons.put(dir[0], new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                x1 + 12, y1 + 12, 12, 12, 36, 112, 36, 112, button -> modify(button, dir[0])));

        directionalButtons.put(Direction.DOWN, new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                x1 + 12, y1 + (12 * 2), 12, 12, 36, 112, 36, 112, button -> modify(button, Direction.DOWN)));

        directionalButtons.put(dir[1], new HandlerSlotButtonComponent<>(screen, new ResourceLocation(Exotek.MODID, "textures/gui/settingsv2.png"),
                x1 + (12 * 2), y1 + 12, 12, 12, 36, 112, 36, 112, button -> modify(button, dir[1])));
    }

    public void update(HashSet<Direction> inputs, HashSet<Direction> outputs) {
        directionalButtons.forEach((direction, tHandlerSlotButtonComponent) -> tHandlerSlotButtonComponent.update(inputs, outputs, direction));
    }

    @Override
    public void doHover(GuiGraphics graphics, int pMouseX, int pMouseY) {
        if (this.visible) {
            directionalButtons.forEach((d, b) -> {
                if (b.isMouseOver(pMouseX, pMouseY)) {
                    graphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.literal("Mode: " + b.getMode().toString()), Component.literal("Direction: " + d)), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
                }
            });
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

    //TODO add push pull to machines

    private void disabled(ButtonComponent<?> button) {

    }

    private void push(ButtonComponent<?> button) {

    }

    private void extract(ButtonComponent<?> button) {

    }

    private void modify(ButtonComponent<?> btn, Direction direction) {
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
        if (this.visible) {
            this.buttons.forEach(tToggleButtonComponent -> {
                tToggleButtonComponent.mouseClicked(p_94737_, p_94738_, p_94739_);
            });
            this.directionalButtons.forEach((d, b) -> b.mouseClicked(p_94737_, p_94738_, p_94739_));
        }
        return super.mouseClicked(p_94737_, p_94738_, p_94739_);
    }

    @Override
    public void drawComponent(GuiGraphics pPoseStack, int pMouseX, int pMouseY, float pPartialTick) {
        this.buttons.forEach(tToggleButtonComponent -> {
            tToggleButtonComponent.render(pPoseStack, pMouseX, pMouseY, pPartialTick);
        });
        this.directionalButtons.forEach((d, b) -> b.render(pPoseStack, pMouseX, pMouseY, pPartialTick));
    }
}
