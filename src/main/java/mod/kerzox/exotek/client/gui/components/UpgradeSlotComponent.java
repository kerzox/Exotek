package mod.kerzox.exotek.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class UpgradeSlotComponent extends SlotComponent {

    public UpgradeSlotComponent(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition);
    }
}
