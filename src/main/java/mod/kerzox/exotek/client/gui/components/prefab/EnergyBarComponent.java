package mod.kerzox.exotek.client.gui.components.prefab;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.client.gui.screen.SolarPanelScreen;
import mod.kerzox.exotek.common.capability.energy.ILargeEnergyStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Optional;

public class EnergyBarComponent extends ProgressComponent {

    private IEnergyStorage storage;

    public EnergyBarComponent(ICustomScreen screen, IEnergyStorage storage, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Direction direction) {
        super(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), x, y, width, height, u1, v1, u2, v2, Component.literal("Energy Bar"), direction);
        this.storage = storage;
    }

    public static EnergyBarComponent small(ICustomScreen screen, IEnergyStorage storage, int x, int y, Direction direction) {
        return new EnergyBarComponent(screen, storage, x, y, 6, 54, 244, 100, 244 + 6, 100, direction);
    }

    public static EnergyBarComponent large(ICustomScreen screen, IEnergyStorage storage, int x, int y, Direction direction) {
        return new EnergyBarComponent(screen, storage, x, y, 141, 49, 0, 119, 0, 119 + 49, direction);
    }

    @Override
    public void onInit() {
        if (storage != null) {
            if (storage instanceof ILargeEnergyStorage largeEnergyStorage) update(largeEnergyStorage.getLargeEnergyStored(), largeEnergyStorage.getLargeMaxEnergyStored());
            else update(storage.getEnergyStored(), storage.getMaxEnergyStored());
        }
    }

    @Override
    public void tick() {
        if (storage != null) {
            if (storage instanceof ILargeEnergyStorage largeEnergyStorage) update(largeEnergyStorage.getLargeEnergyStored(), largeEnergyStorage.getLargeMaxEnergyStored());
            else update(storage.getEnergyStored(), storage.getMaxEnergyStored());
        }
    }

    @Override
    protected List<Component> getComponents() {
        return List.of(Component.literal("Energy: " +
                SolarPanelScreen.abbreviateNumber(getMinimum(), true) + "/"
                + SolarPanelScreen.abbreviateNumber(getMaximum(), true)));
    }
}
