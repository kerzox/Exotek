package mod.kerzox.exotek.common.capability;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;

public interface IStrictCombinedItemHandler extends IStrictInventory, IItemHandlerModifiable {

    NonNullList<ItemStack> getItems();
    void setItems(NonNullList<ItemStack> stacks);

}
