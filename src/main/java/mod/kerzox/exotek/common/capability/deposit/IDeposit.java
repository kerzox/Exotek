package mod.kerzox.exotek.common.capability.deposit;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

@AutoRegisterCapability
public interface IDeposit {

    List<OreDeposit.OreStack> getItems();
    List<FluidStack> getFluids();
    boolean isOreDeposit();
    boolean isFluidDeposit();

}
