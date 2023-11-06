package mod.kerzox.exotek.common.capability.upgrade;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.items.ItemStackHandler;

import java.util.List;

@AutoRegisterCapability
public interface IUpgradableMachine {

    ItemStackHandler getInventory();

}
