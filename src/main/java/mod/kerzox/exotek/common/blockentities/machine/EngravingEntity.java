package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.EngraverMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.EngraverRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EngravingEntity extends RecipeWorkingBlockEntity<EngraverRecipe> {

    private int feTick = 20;
    private final int SPECIAL_SLOT = 0;
    private final int INPUT_SLOT = 1;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };
    private final ItemStackInventory itemHandler = new ItemStackInventory(2, 1);

    public EngravingEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.ENGRAVING_ENTITY.get(), Registry.ENGRAVER_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(itemHandler));
        addCapabilities(energyHandler, itemHandler);
    }

    @Override
    protected boolean hasAResult(EngraverRecipe workingRecipe) {
        return !workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY).isEmpty();
    }

    @Override
    protected void onRecipeFinish(EngraverRecipe workingRecipe) {
        ItemStack result = workingRecipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);
        // simulate the transfer of items
        ItemStack simulation = this.itemHandler.getInputHandler().forceExtractItem(INPUT_SLOT, 1, true);

        // check if we are still able to finish by placing the item etc. (Might change this) TODO
        if (simulation.isEmpty()) return;
        if (!this.itemHandler.getOutputHandler().forceInsertItem(INPUT_SLOT, result, true).isEmpty()) return;

        this.itemHandler.getInputHandler().getStackInSlot(INPUT_SLOT).shrink(1);
        this.itemHandler.getOutputHandler().forceInsertItem(INPUT_SLOT, result, false);

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
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("itemHandler", this.itemHandler.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.itemHandler.deserialize(pTag.getCompound("itemHandler"));
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        super.addToUpdateTag(tag);
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
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
