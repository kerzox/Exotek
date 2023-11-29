package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.RubberExtractionMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.RubberExtractionRecipe;
import mod.kerzox.exotek.common.event.TickUtils;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class RubberExtractionEntity extends RecipeWorkingBlockEntity<RubberExtractionRecipe> {

    private int feTick = 20;
    private Direction direction = Direction.NORTH;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };
    private final SidedSingleFluidTank sidedSingleFluidTank = new SidedSingleFluidTank(16000);
    private final ItemStackHandler hiddenInventory = new ItemStackHandler(1);
    private int logsLastFor = TickUtils.minutesToTicks(10);
    private int ticksTaken = logsLastFor;

    public RubberExtractionEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.RUBBER_EXTRACTION_ENTITY.get(), ExotekRegistry.RUBBER_EXTRACTION_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(hiddenInventory));
        addCapabilities(energyHandler, sidedSingleFluidTank);
    }

    @Override
    public void tick() {
        Optional<RubberExtractionRecipe> recipe = Optional.empty();
        for (Direction direction : Direction.values()) {
            if (level.getBlockState(worldPosition.relative(direction)).is(BlockTags.LOGS)) {
                this.hiddenInventory.setStackInSlot(0, new ItemStack(level.getBlockState(worldPosition.relative(direction)).getBlock().asItem()));
                this.direction = direction;
                recipe = level.getRecipeManager().getRecipeFor(ExotekRegistry.RUBBER_EXTRACTION_RECIPE.get(), getRecipeInventoryWrapper(), level);
                break;
            }
        }
        recipe.ifPresent(r-> {
            doRecipe(r);
            ticksTaken--;
            if (ticksTaken <= 0) {
                ticksTaken = logsLastFor;
                level.destroyBlock(worldPosition.relative(direction), false);
            }
        });
    }

    @Override
    protected boolean hasAResult(RubberExtractionRecipe workingRecipe) {
        return !workingRecipe.assembleFluid(getRecipeInventoryWrapper()).isEmpty();
    }

    @Override
    protected void onRecipeFinish(RubberExtractionRecipe recipe) {
        FluidStack result = recipe.assembleFluid(getRecipeInventoryWrapper());
        if (!this.sidedSingleFluidTank.getFluid().isEmpty()) {
            if (!this.sidedSingleFluidTank.getFluid().isFluidEqual(result)) return;
        }

        this.sidedSingleFluidTank.fill(result, IFluidHandler.FluidAction.EXECUTE);

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

    public int getLogsLastFor() {
        return logsLastFor;
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.logsLastFor = pTag.getInt("max_duration");
        this.ticksTaken = pTag.getInt("duration");
    }

    @Override
    protected void write(CompoundTag tag) {
        tag.putInt("duration", this.ticksTaken);
        tag.putInt("max_duration", this.logsLastFor);
        tag.putBoolean("stall", this.stall);
    }


    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new RubberExtractionMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
