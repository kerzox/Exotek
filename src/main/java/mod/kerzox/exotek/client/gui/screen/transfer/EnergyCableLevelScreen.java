package mod.kerzox.exotek.client.gui.screen.transfer;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ButtonComponent;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.menu.transfer.EnergyCableMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.common.blockentities.transport.IOTypes;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelNode;
import mod.kerzox.exotek.common.network.LevelNetworkPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class EnergyCableLevelScreen extends Screen implements ICustomScreen {

    protected int left;
    protected int top;
    protected int xSize;
    protected int ySize;
    protected int imageWidth = 176;
    protected int imageHeight = 166;

    private ResourceLocation GUI;
    private int backgroundColour;

    private ProgressComponent<EnergyCableMenu> energyBar = new ProgressComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 154, 17, 10, 54, 0, 65, 10, 65);
    private List<ToggleButtonComponent<EnergyCableMenu>> buttons = new ArrayList<>();
    private Map<Direction, ToggleButtonComponent<EnergyCableMenu>> directionalButtons = new HashMap<>();
    private LevelEnergyNetwork levelInstance;

    private Direction currentDirection = Direction.NORTH;

    private LevelNode node;

    protected EnergyCableLevelScreen(LevelNode node) {
        super(Component.literal("Cable Config"));
        this.GUI = new ResourceLocation(Exotek.MODID, "textures/gui/cable.png");
        this.backgroundColour = 0;
        this.xSize = 256;
        this.ySize = 256;

        this.node = node;

        buttons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                11, 17, 12, 12, 36, 217, 36, 217+12, this::push));

        buttons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                11+12, 17, 12, 12, 48, 217, 48, 217+12, this::extract));

        buttons.add(new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                11+(12*2), 17, 12, 12, 72, 217, 72, 217+12, this::disable));

        int x = 11;
        int y = 34;

        Direction[] dir = getDirectionFromFacing(Minecraft.getInstance().player.getDirection());

        directionalButtons.put(Direction.UP, new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + 12, y, 12, 12, 60, 217, 60, 229, button -> toggleDirection(button, Direction.UP)));

        directionalButtons.put(dir[3], new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x, y + 12, 12, 12, 60, 217, 60, 229, button -> toggleDirection(button, dir[3])));

        directionalButtons.put(dir[2], new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x, y + (12 * 2), 12, 12, 60, 217, 60, 229, button -> toggleDirection(button, dir[2])));

        directionalButtons.put(dir[0], new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + 12, y + 12, 12, 12, 60, 217, 60, 229, button -> toggleDirection(button, dir[0])));

        directionalButtons.put(Direction.DOWN, new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + 12, y + (12 * 2), 12, 12, 60, 217, 60, 229, button -> toggleDirection(button, Direction.DOWN)));

        directionalButtons.put(dir[1], new ToggleButtonComponent<>(this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"),
                x + (12 * 2), y + 12, 12, 12, 60, 217, 60, 229, button -> toggleDirection(button, dir[1])));

        Minecraft.getInstance().level.getCapability(ExotekCapabilities.ENERGY_LEVEL_NETWORK_CAPABILITY).ifPresent(cap -> {

            if (cap instanceof LevelEnergyNetwork network) {
                levelInstance = network;
            }

        });

        currentDirection = dir[0];

    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private void disable(ButtonComponent<?> button) {
        CompoundTag tag = new CompoundTag();
        if (button instanceof ToggleButtonComponent<?> toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                node.getDirectionalIO().put(currentDirection, IOTypes.NONE);
            } else {
                node.getDirectionalIO().put(currentDirection, IOTypes.DEFAULT);
            }
        }
        tag.put("node_to_update", node.serialize());
        PacketHandler.sendToServer(new LevelNetworkPacket(tag));
    }

    private void push(ButtonComponent<?> button) {
        CompoundTag tag = new CompoundTag();
        if (button instanceof ToggleButtonComponent<?> toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                node.getDirectionalIO().put(currentDirection, IOTypes.PUSH);
            } else {
                node.getDirectionalIO().put(currentDirection, IOTypes.DEFAULT);
            }
        }
        tag.put("node_to_update", node.serialize());
        PacketHandler.sendToServer(new LevelNetworkPacket(tag));
    }

    private void extract(ButtonComponent<?> button) {
        CompoundTag tag = new CompoundTag();
        if (button instanceof ToggleButtonComponent<?> toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                node.getDirectionalIO().put(currentDirection, IOTypes.EXTRACT);
            } else {
                node.getDirectionalIO().put(currentDirection, IOTypes.DEFAULT);
            }
        }
        tag.put("node_to_update", node.serialize());
        PacketHandler.sendToServer(new LevelNetworkPacket(tag));
    }

    private void toggleDirection(ButtonComponent<?> button, Direction direction) {
        if (button instanceof ToggleButtonComponent<?> toggleButtonComponent) {
            if (toggleButtonComponent.getState()) {
                for (ButtonComponent<EnergyCableMenu> btn : directionalButtons.values()) {
                    if (button != btn) {
                        btn.setState(false);
                    } else {
                        currentDirection = direction;

                    }
                }
            }
        }
    }

    private EnergySubNetwork getSubnet() {
        return levelInstance.getNetworkFromPosition(node.getWorldPosition());
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


    public static void draw(LevelNode node) {
        Minecraft.getInstance().setScreen(new EnergyCableLevelScreen(node));
    }

    @Override
    protected void init() {
        this.left = (this.width - this.imageWidth) / 2;
        this.top = (this.height - this.imageHeight) / 2;
        for (ToggleButtonComponent<EnergyCableMenu> button : buttons) {
            addRenderableOnly(button);
        }
        for (ToggleButtonComponent<EnergyCableMenu> value : directionalButtons.values()) {
            addRenderableOnly(value);
        }
        addRenderableOnly(energyBar);
        PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));
        this.directionalButtons.get(currentDirection).setState(true);
        this.energyBar.update(getSubnet().getInternalStorage().getEnergyStored(), getSubnet().getInternalStorage().getMaxEnergyStored(), ProgressComponent.Direction.UP);
        this.buttons.get(0).setState(getSubnet().getNodeByPosition(node.getWorldPosition()).getDirectionalIO().get(currentDirection) == IOTypes.PUSH);
        this.buttons.get(1).setState(getSubnet().getNodeByPosition(node.getWorldPosition()).getDirectionalIO().get(currentDirection) == IOTypes.EXTRACT);
        this.buttons.get(2).setState(getSubnet().getNodeByPosition(node.getWorldPosition()).getDirectionalIO().get(currentDirection) == IOTypes.NONE);
    }

    @Override
    public void tick() {
        EnergySubNetwork sub = getSubnet();
        if (sub != null) {
            PacketHandler.sendToServer(new LevelNetworkPacket(new CompoundTag()));
            this.energyBar.update(sub.getInternalStorage().getEnergyStored(), sub.getInternalStorage().getMaxEnergyStored(), ProgressComponent.Direction.UP);
            this.buttons.get(0).setState(sub.getNodeByPosition(node.getWorldPosition()).getDirectionalIO().get(currentDirection) == IOTypes.PUSH);
            this.buttons.get(1).setState(sub.getNodeByPosition(node.getWorldPosition()).getDirectionalIO().get(currentDirection) == IOTypes.EXTRACT);
            this.buttons.get(2).setState(getSubnet().getNodeByPosition(node.getWorldPosition()).getDirectionalIO().get(currentDirection) == IOTypes.NONE);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTick) {
        if (GUI != null) {
            int i = (this.width - this.imageWidth) / 2;
            int j = (this.height - this.imageHeight) / 2;
            graphics.blit(GUI, i, j, 0, 0, this.imageWidth, this.imageHeight);
        }
       super.render(graphics, pMouseX, pMouseY, partialTick);

        Component text = Component.literal(currentDirection.getName().substring(0, 1).toUpperCase() + currentDirection.getName().substring(1));
        FormattedCharSequence formattedcharsequence = text.getVisualOrderText();
        graphics.drawString(this.font, formattedcharsequence, (this.getGuiLeft() + 71) + ((29) - this.font.width(formattedcharsequence) / 2), this.getGuiTop() + 19, 4210752, false);
        if (energyBar.isMouseOver(pMouseX, pMouseY)) {
            graphics.renderTooltip(this.font, List.of(Component.literal("Stored Energy: " + this.energyBar.getMinimum())), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);
        }
    }


    @Override
    public boolean mouseClicked(double p_94695_, double p_94696_, int p_94697_) {
        for (Renderable renderable : this.renderables) {
            if (renderable instanceof ButtonComponent<?> buttonComponent) {
                buttonComponent.mouseClicked(p_94695_, p_94696_, p_94697_);
            }
        }
        return super.mouseClicked(p_94695_, p_94696_, p_94697_);
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return 0;
    }

    @Override
    public int getGuiLeft() {
        return left;
    }

    @Override
    public int getGuiTop() {
        return top;
    }

    @Override
    public DefaultMenu<?> getMenu() {
        return null;
    }
}
