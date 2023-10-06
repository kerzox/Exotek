package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.MaceratorMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.MaceratorRecipe;
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

public class MaceratorEntity extends RecipeWorkingBlockEntity {

    private int feTick = 20;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };
    private final ItemStackInventory itemHandler = new ItemStackInventory(1, 1);

    public MaceratorEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.MACERATOR_ENTITY.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(itemHandler));
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.getHandler(side);
        }
        else if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.itemHandler.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void doRecipeCheck() {
        Optional<MaceratorRecipe> recipe = level.getRecipeManager().getRecipeFor(Registry.MACERATOR_RECIPE.get(), getRecipeInventoryWrapper(), level);
        recipe.ifPresent(this::doRecipe);
    }

    @Override
    public void doRecipe(RecipeInteraction workingRecipe) {
        MaceratorRecipe recipe = (MaceratorRecipe) workingRecipe.getRecipe();
        ItemStack result = recipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        // recipe doesn't have a result return
        if (result.isEmpty()) return;

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipe);
        }

        // finish recipe
        if (getDuration() <= 0) {

            // simulate the transfer of items
            ItemStack simulation = this.itemHandler.getInputHandler().forceExtractItem(0, 1, true);

            // check if we are still able to finish by placing the item etc. (Might change this) TODO
            if (simulation.isEmpty()) return;
            if (!this.itemHandler.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) return;

            this.itemHandler.getInputHandler().getStackInSlot(0).shrink(1);
            this.itemHandler.getOutputHandler().forceInsertItem(0, result, false);

            finishRecipe();

            return;

        }

        // do power consume TODO ideally we want the machine to stop working until energy returns this will be looked into after
        if (!energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
            return;
        };

        energyHandler.consumeEnergy(feTick);
        duration--;
//
//        syncBlockEntity();
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
        return Component.literal("Macerator Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new MaceratorMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
