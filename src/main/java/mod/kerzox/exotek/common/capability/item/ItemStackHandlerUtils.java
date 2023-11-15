package mod.kerzox.exotek.common.capability.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ItemStackHandlerUtils {

    public static void insertAndModifyStack(IItemHandler insertTo, ItemStack stack) {
        ItemStack ret = ItemHandlerHelper.insertItem(insertTo, stack.copy(), false);
        if (ret.isEmpty()) {
            stack.shrink(stack.getCount());
        } else {
            int extracted = stack.getCount() - ret.getCount();
            stack.shrink(extracted);
        }
    }

    public static void insertAndModifyStack(IItemHandler insertTo, ItemStack stack, int maxInsert) {
        ItemStack ret = ItemHandlerHelper.insertItem(insertTo, stack.copyWithCount(maxInsert), false);
        if (ret.isEmpty()) {
            stack.shrink(stack.getCount());
        } else {
            int extracted = stack.getCount() - ret.getCount();
            stack.shrink(extracted);
        }
    }


}
