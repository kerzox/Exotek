package mod.kerzox.exotek.client.gui.components.prefab;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.gui.components.ProgressComponent;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
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

    public EnergyBarComponent(ICustomScreen screen, IEnergyStorage storage, int x, int y, Direction direction) {
        super(screen, new ResourceLocation(Exotek.MODID, "textures/gui/widgets.png"), x, y, 6, 54, 244, 100, 244 + 6, 100, Component.literal("Energy Bar"), direction);
        this.storage = storage;
    }

    public EnergyBarComponent(ICustomScreen screen, IEnergyStorage storage, int x, int y) {
        this(screen, storage, x, y, Direction.UP);
    }

    @Override
    public void onInit() {
        if (storage != null) update(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    @Override
    public void tick() {
        if (storage != null) update(storage.getEnergyStored(), storage.getMaxEnergyStored());
    }

    @Override
    protected void onHover(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.renderTooltip(Minecraft.getInstance().font, List.of(Component.literal("Stored Energy: " +
                        getMinimum())), Optional.empty(), ItemStack.EMPTY, mouseX, mouseY);
    }
}
