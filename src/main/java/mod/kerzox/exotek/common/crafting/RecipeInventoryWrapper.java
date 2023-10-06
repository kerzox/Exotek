package mod.kerzox.exotek.common.crafting;

import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

// this will allow for all types of cap handlers such as fluids, items, etc.
public class RecipeInventoryWrapper extends RecipeWrapper {

    private IFluidHandler fluidHandler;

    public RecipeInventoryWrapper() {
        super(new ItemStackHandler());
    }

    public RecipeInventoryWrapper(IItemHandlerModifiable inv) {
        super(inv);
    }

    public RecipeInventoryWrapper(IFluidHandler handler) {
        super(new ItemStackHandler(0));
        fluidHandler = handler;
    }

    public RecipeInventoryWrapper(IFluidHandler handler, IItemHandlerModifiable inv) {
        super(inv);
        fluidHandler = handler;
    }

    public IFluidHandler getFluidHandler() {
        return fluidHandler;
    }


    public boolean canStorageFluid() {
        return this.fluidHandler != null;
    }
}
