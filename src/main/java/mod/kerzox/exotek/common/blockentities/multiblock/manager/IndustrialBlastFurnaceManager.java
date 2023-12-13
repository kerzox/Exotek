package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.IndustrialBlastFurnaceRecipe;
import mod.kerzox.exotek.common.event.TickUtils;
import mod.kerzox.exotek.registry.ConfigConsts;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Industrial blast furnace is a faster blast furnace (also electric) that uses heat to heat the furnace up
 * This means it has a multiplier to speed the hotter the furnace is.
 */

public class IndustrialBlastFurnaceManager extends RecipeMultiblockManager<IndustrialBlastFurnaceRecipe> {

    private SidedEnergyHandler energyHandler = new SidedEnergyHandler(1000000);
    private ItemStackInventory inventory = new ItemStackInventory(1, 1);
    private SidedSingleFluidTank fluidTank = new SidedSingleFluidTank(32000);

    private int heat;
    private int tick;


    public IndustrialBlastFurnaceManager() {
        super("industrial_blast_furnace");
        setRecipeInventoryWrapper(new RecipeInventoryWrapper(fluidTank, inventory));
    }

    @Override
    protected void tick() {
        tick = TickUtils.getAsOverflowSafeTick(tick);

        // heat the furnace up
        if (!energyHandler.hasEnough(ConfigConsts.INDUSTRIAL_BLAST_FURNACE_FE_USAGE_PER_TICK)) {
            // every x seconds without enough electricity we lose a little heat;
            if (TickUtils.every(tick, ConfigConsts.INDUSTRIAL_BLAST_FURNACE_HEAT_LOSS)) {
                if (heat > 0) {
                    heat--;
                }
            }
        } else {
            if (TickUtils.every(tick, ConfigConsts.INDUSTRIAL_BLAST_FURNACE_HEAT_GAIN)) {
                if (heat < ConfigConsts.INDUSTRIAL_BLAST_FURNACE_MAX_HEAT) {
                    heat++;
                }
            }
            energyHandler.consumeEnergy(ConfigConsts.INDUSTRIAL_BLAST_FURNACE_FE_USAGE_PER_TICK);
        }

        workingRecipe.ifPresent(this::doRecipe);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide) if (getManagingBlockEntity() != null && getManagingBlockEntity().getSecond() != null) return this.getManagingBlockEntity().getSecond().onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
        return false;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return inventory.getHandler(side);
        else if (cap == ForgeCapabilities.FLUID_HANDLER) return fluidTank.getHandler(side);
        else if (cap == ForgeCapabilities.ENERGY) return energyHandler.getHandler(side);
        return LazyOptional.empty();
    }

    public SidedSingleFluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.put("itemHandler", this.inventory.serialize());
        tag.put("energyHandler", this.energyHandler.serialize());
        tag.put("fluidHandler", this.fluidTank.serialize());
        tag.putInt("duration", this.duration);
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("heat", this.heat);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.inventory.deserialize(tag.getCompound("itemHandler"));
        this.energyHandler.deserialize(tag.getCompound("energyHandler"));
        this.fluidTank.deserialize(tag.getCompound("fluidHandler"));
        this.heat = tag.getInt("heat");
        this.duration = tag.getInt("duration");
        this.maxDuration = tag.getInt("max_duration");
        super.deserialize(tag);
    }


    @Override
    public void checkForRecipes(Level level) {
        Optional<IndustrialBlastFurnaceRecipe> recipe = level.getRecipeManager().getRecipeFor(ExotekRegistry.INDUSTRIAL_BLAST_FURNACE_RECIPE.get(),
                getRecipeInventoryWrapper(),
                level);
        recipe.ifPresent(this::doRecipe);
    }

    @Override
    public int getMaxDuration() {
        return super.getMaxDuration();
    }

    @Override
    public int getDuration() {
        return super.getDuration();
    }

    @Override
    public void doRecipe(IndustrialBlastFurnaceRecipe recipeInteraction) {
        IndustrialBlastFurnaceRecipe recipe = (IndustrialBlastFurnaceRecipe) recipeInteraction.getRecipe();
        ItemStack result = recipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        if (result.isEmpty()) return;

        if (!isRunning()) {
            setRunning(recipeInteraction);
        }

        if (getDuration() < 0) {
            ItemStack ret = inventory.getOutputHandler().forceInsertItem(0, result.copy(), true);
            if (!ret.isEmpty()) return;

            RecipeWorkingBlockEntity.useFluidIngredients(recipe.getFluidIngredients(), this.fluidTank);
            RecipeWorkingBlockEntity.useIngredients(recipe.getIngredients(), inventory.getInputHandler(), 1);
            this.inventory.getOutputHandler().forceInsertItem(0, result.copy(), false);
            finishRecipe();
            return;
        }

        if (recipe.getHeat() > heat) return;

        // add modifier to heat if recipe heat is less than the current heat of the blast furnace
        double difference = (double) heat / recipe.getHeat();
        double modifier = (difference * ConfigConsts.INDUSTRIAL_BLAST_FURNACE_SPEED_MODIFIER);
        duration -= modifier;


    }

    public int getHeat() {
        return heat;
    }
}
