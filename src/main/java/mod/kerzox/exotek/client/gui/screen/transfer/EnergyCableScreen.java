package mod.kerzox.exotek.client.gui.screen.transfer;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.transfer.EnergyCableMenu;
import mod.kerzox.exotek.client.gui.screen.DefaultScreen;
import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class EnergyCableScreen extends DefaultScreen<EnergyCableMenu> {

//    private ProgressComponent<EnergyCableMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 154, 17, 10, 54, 0, 65, 10, 65);
    private List<ToggleButtonComponent> buttons = new ArrayList<>();
    private Map<Direction, ToggleButtonComponent> directionalButtons = new HashMap<>();
    private LevelEnergyNetwork levelInstance;

    private Direction currentDirection = Direction.NORTH;

    public EnergyCableScreen(EnergyCableMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "cable.png", false);

        buttons.add(new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                11, 17, 12, 12, 36, 217, 36, 217 + 12, Component.literal("Push Button"), this::push));

        buttons.add(new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                11 + 12, 17, 12, 12, 48, 217, 48, 217 + 12, Component.literal("Extract Button"), this::extract));

        buttons.add(new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                11 + (12 * 2), 17, 12, 12, 72, 217, 72, 217 + 12, Component.literal("Disable Button"), this::disable));

        int x = 11;
        int y = 34;

        Direction[] dir = getDirectionFromFacing(Minecraft.getInstance().player.getDirection());

        directionalButtons.put(Direction.UP, new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + 12, y, 12, 12, 60, 217, 60, 229, Component.literal("Directional Button"), button -> toggleDirection(button, Direction.UP)));

        directionalButtons.put(dir[3], new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x, y + 12, 12, 12, 60, 217, 60, 229, Component.literal("Directional Button"), button -> toggleDirection(button, dir[3])));

        directionalButtons.put(dir[2], new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x, y + (12 * 2), 12, 12, 60, 217, 60, 229, Component.literal("Directional Button"), button -> toggleDirection(button, dir[2])));

        directionalButtons.put(dir[0], new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + 12, y + 12, 12, 12, 60, 217, 60, 229, Component.literal("Directional Button"), button -> toggleDirection(button, dir[0])));

        directionalButtons.put(Direction.DOWN, new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + 12, y + (12 * 2), 12, 12, 60, 217, 60, 229, Component.literal("Directional Button"), button -> toggleDirection(button, Direction.DOWN)));

        directionalButtons.put(dir[1], new ToggleButtonComponent(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + (12 * 2), y + 12, 12, 12, 60, 217, 60, 229, Component.literal("Directional Button"), button -> toggleDirection(button, dir[1])));

        Minecraft.getInstance().level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {

            if (cap instanceof LevelEnergyNetwork network) {
                levelInstance = network;
            }

        });

        currentDirection = dir[0];
    }

    private void disable(ButtonComponent button) {
        CompoundTag tag = new CompoundTag();
        LevelNode node = getSubnet().getNodeByPosition(getMenu().getBlockEntity().getBlockPos());
        if (button instanceof ToggleButtonComponent toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                node.getDirectionalIO().put(currentDirection, IOTypes.NONE);
            } else {
                node.getDirectionalIO().put(currentDirection, IOTypes.DEFAULT);
            }
        }
        tag.put("node_to_update", node.serialize());
        PacketHandler.sendToServer(new LevelNetworkPacket(tag));
    }

    private void push(ButtonComponent button) {
        CompoundTag tag = new CompoundTag();
        LevelNode node = getSubnet().getNodeByPosition(getMenu().getBlockEntity().getBlockPos());
        if (button instanceof ToggleButtonComponent toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                node.getDirectionalIO().put(currentDirection, IOTypes.PUSH);
            } else {
                node.getDirectionalIO().put(currentDirection, IOTypes.DEFAULT);
            }
        }
        tag.put("node_to_update", node.serialize());
        PacketHandler.sendToServer(new LevelNetworkPacket(tag));
    }

    private void extract(ButtonComponent button) {
        CompoundTag tag = new CompoundTag();
        LevelNode node = getSubnet().getNodeByPosition(getMenu().getBlockEntity().getBlockPos());
        if (button instanceof ToggleButtonComponent toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                node.getDirectionalIO().put(currentDirection, IOTypes.EXTRACT);
            } else {
                node.getDirectionalIO().put(currentDirection, IOTypes.DEFAULT);
            }
        }
        tag.put("node_to_update", node.serialize());
        PacketHandler.sendToServer(new LevelNetworkPacket(tag));
    }

    private void toggleDirection(ButtonComponent button, Direction direction) {
        if (button instanceof ToggleButtonComponent toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                for (ButtonComponent btn : directionalButtons.values()) {
                    if (button != btn) {
                        btn.setState(false);
                    } else {
                        currentDirection = direction;

                    }
                }
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {

    }

    private EnergySubNetwork getSubnet() {
        return levelInstance.getNetworkFromPosition(getMenu().getBlockEntity().getBlockPos());
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



    @Override
    protected void onOpen() {
        for (ButtonComponent button : buttons) {
            addRenderableWidget(button);
        }
        for (ButtonComponent directionalButton : directionalButtons.values()) {
            addRenderableWidget(directionalButton);
        }
        EnergySubNetwork sub = getSubnet();
        if (sub != null) {
            PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));
            this.directionalButtons.get(currentDirection).setState(true);
            this.buttons.get(0).setState(sub.getNodeByPosition(getMenu().getBlockEntity().getBlockPos()).getDirectionalIO().get(currentDirection) == IOTypes.PUSH);
            this.buttons.get(1).setState(sub.getNodeByPosition(getMenu().getBlockEntity().getBlockPos()).getDirectionalIO().get(currentDirection) == IOTypes.EXTRACT);
            this.buttons.get(2).setState(sub.getNodeByPosition(getMenu().getBlockEntity().getBlockPos()).getDirectionalIO().get(currentDirection) == IOTypes.NONE);
        }
    }

    @Override
    protected void menuTick() {
        EnergySubNetwork sub = getSubnet();
        if (sub != null) {
            PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));
            this.buttons.get(0).setState(sub.getNodeByPosition(getMenu().getBlockEntity().getBlockPos()).getDirectionalIO().get(currentDirection) == IOTypes.PUSH);
            this.buttons.get(1).setState(sub.getNodeByPosition(getMenu().getBlockEntity().getBlockPos()).getDirectionalIO().get(currentDirection) == IOTypes.EXTRACT);
            this.buttons.get(2).setState(sub.getNodeByPosition(getMenu().getBlockEntity().getBlockPos()).getDirectionalIO().get(currentDirection) == IOTypes.NONE);
        }
    }

    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {
        Component text = Component.literal(currentDirection.getName().substring(0, 1).toUpperCase() + currentDirection.getName().substring(1));
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        graphics.drawString(this.font, formattedcharsequence, (this.getGuiLeft() + 71) + ((29) - this.font.width(formattedcharsequence) / 2), this.getGuiTop() + 19, 4210752, false);
    }
}
