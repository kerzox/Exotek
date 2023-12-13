package mod.kerzox.exotek.common.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IStrictCombinedItemHandler extends IStrictCapabilityIO, IItemHandlerModifiable {

    IItemHandlerModifiable getInputHandler();
    IItemHandlerModifiable getOutputHandler();

}
