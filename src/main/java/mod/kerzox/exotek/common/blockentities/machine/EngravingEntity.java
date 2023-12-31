package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.EngraverMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.EngravingRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class EngravingEntity extends RecipeWorkingBlockEntity<EngravingRecipe> {

    private int feTick = 20;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };
    private final ItemStackInventory itemHandler = new ItemStackInventory(2, 1);

    public EngravingEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.ENGRAVING_ENTITY.get(), ExotekRegistry.ENGRAVER_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(itemHandler));
        addCapabilities(energyHandler, itemHandler);
    }

    @Override
    protected boolean hasAResult(EngravingRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(EngravingRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        if (hasEnoughItemSlots(new ItemStack[]{result}, itemHandler.getOutputHandler()).size() != 1) return;
        transferItemResults(new ItemStack[]{result}, itemHandler.getOutputHandler());

        useIngredients(workingRecipe.getIngredients(), itemHandler.getInputHandler(), 1);

        finishRecipe();
    }

    @Override
    protected boolean checkConditionForRecipeTick(RecipeInteraction recipe) {
        if (energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(feTick);
            return true;
        }
        else return false;
    }


    @Override
    protected void read(CompoundTag pTag) {
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putInt("max_duration", this.maxDuration);
        pTag.putInt("duration", this.duration);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Engraving Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new EngraverMenu(p_39954_, p_39955_, p_39956_, this);
    }

}
