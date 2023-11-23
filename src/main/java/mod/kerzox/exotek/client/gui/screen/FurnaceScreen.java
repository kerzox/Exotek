package mod.kerzox.exotek.client.gui.screen;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.components.ToggleButtonComponent;
import mod.kerzox.exotek.client.gui.components.prefab.AutoSortButton;
import mod.kerzox.exotek.client.gui.components.prefab.EnergyBarComponent;
import mod.kerzox.exotek.client.gui.components.prefab.RecipeProgressComponent;
import mod.kerzox.exotek.client.gui.menu.FurnaceMenu;
import mod.kerzox.exotek.client.gui.menu.MaceratorMenu;
import mod.kerzox.exotek.client.gui.menu.SingleBlockMinerMenu;
import mod.kerzox.exotek.common.network.CompoundTagPacket;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.util.MachineTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.*;

public class FurnaceScreen extends DefaultScreen<FurnaceMenu> {

    private EnergyBarComponent energyBar = EnergyBarComponent.small(this,  this.getMenu().getBlockEntity().getCapability(ForgeCapabilities.ENERGY).resolve().get(), 8, 17, ProgressComponent.Direction.UP);   private Map<MachineTier, List<ProgressComponent>> progressMap = new HashMap<>() {
        {
            put(MachineTier.DEFAULT, Collections.singletonList(new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 72, 36, 28, 12, 36, 32, 36, 20,
                    Component.literal("arr1"), ProgressComponent.Direction.RIGHT)));
            put(MachineTier.BASIC, Arrays.asList(
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 63, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr1"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 83, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr2"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 103, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr3"), ProgressComponent.Direction.DOWN)
            ));
            put(MachineTier.ADVANCED, Arrays.asList(
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 63, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr1"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 83, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr2"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 103, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr3"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 43, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr4"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 123, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr5"), ProgressComponent.Direction.DOWN)
            ));
            put(MachineTier.SUPERIOR, Arrays.asList(
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35, 37, 10, 14, 73, 0, 83, 0, Component.literal("arr1"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35 + (20), 37, 10, 14, 73, 0, 83, 0, Component.literal("arr2"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35 + (20 * 2), 37, 10, 14, 73, 0, 83, 0, Component.literal("arr3"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35 + (20 * 3), 37, 10, 14, 73, 0, 83, 0, Component.literal("arr4"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35 + (20 * 4), 37, 10, 14, 73, 0, 83, 0, Component.literal("arr5"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35 + (20 * 5), 37, 10, 14, 73, 0, 83, 0, Component.literal("arr6"), ProgressComponent.Direction.DOWN),
                    new RecipeProgressComponent(FurnaceScreen.this, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), 35 + (20 * 6), 37, 10, 14, 73, 0, 83, 0, Component.literal("arr7"), ProgressComponent.Direction.DOWN)
            ));
        }
    };

    private ToggleButtonComponent sortButton = new AutoSortButton(this, 17, 38);


    public FurnaceScreen(FurnaceMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle, "furnace.png");
    }

    @Override
    protected void onOpen() {
        addRenderableWidget(energyBar);
        for (ProgressComponent component : progressMap.get(getMenu().getBlockEntity().getTier(getMenu().getBlockEntity()))) {
            addRenderableWidget(component);
        }
        addRenderableWidget(sortButton);
        sortButton.setState(getMenu().getBlockEntity().isSorting());
        sortButton.setVisible(getMenu().getBlockEntity().getTier(getMenu().getBlockEntity()) != MachineTier.DEFAULT);
    }


    @Override
    protected void menuTick() {
        int[][] progress = getMenu().getBlockEntity().getClientProgress();
        List<ProgressComponent> components = progressMap.get(getMenu().getBlockEntity().getTier(getMenu().getBlockEntity()));

        for (int i = 0; i < progress.length; i++) {
            if (progress[i] == null) continue;
            int totalDuration = progress[i][1];
            int duration = progress[i][0];

            if (duration > 0) {
                components.get(i).update(totalDuration - duration, totalDuration);
            } else {
                components.get(i).update(0, 0);
            }
        }
    }


    @Override
    protected void addToBackground(GuiGraphics graphics, float partialTick, int pMouseX, int pMouseY) {

    }

    @Override
    protected void addToForeground(GuiGraphics graphics, int x, int y) {

    }


}
