package mod.kerzox.exotek.common.blockentities;
import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WorkstationEntity extends MachineBlockEntity implements MenuProvider {

    /*
        0 = blueprint, 1 = crafting, 2 = ?, 3 = ?
     */
    private int tab = 0;
    private SidedItemStackHandler handler = new SidedItemStackHandler(10);
    private boolean click;

    public WorkstationEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.WORKSTATION_ENTITY.get(),pos, state);
        addCapabilities(handler);
    }

    public int getTab() {
        return tab;
    }

    @Override
    public void updateFromNetwork(CompoundTag tag) {
        super.updateFromNetwork(tag);
        if (tag.contains("tab")) {
            tab = tag.getInt("tab");
        }
        if (tag.contains("finish_recipe")) {
            this.click = true;
        }
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("tab", this.tab);
        pTag.putBoolean("clicked", this.click);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.tab = pTag.getInt("tab");
        this.click = pTag.getBoolean("clicked");
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new WorkstationMenu(p_39954_, p_39955_, p_39956_, this);
    }
//
//    @Override
//    protected boolean hasAResult(WorkstationRecipe workingRecipe) {
//        return !workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY).isEmpty();
//    }
//
//    @Override
//    protected void onRecipeFinish(WorkstationRecipe workingRecipe) {
//        if (click) {
//            ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);
//
//            if (!handler.insertItem(9, result.copy(), true).isEmpty()) return;
//            handler.insertItem(9, result, false);
//
//            // ignore the last slot which is our output
//            useSizeSpecificIngredients(workingRecipe.getSizeIngredients(), handler, 9);
//
//            finishRecipe();
//            click = false;
//        }
//    }
}
