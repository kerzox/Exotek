package mod.kerzox.exotek.common.blockentities;

import mod.kerzox.exotek.common.capability.fluid.FluidStorageTank;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class RecipeWorkingBlockEntity<T extends AbstractRecipe<RecipeInventoryWrapper>> extends MachineBlockEntity implements MenuProvider, IServerTickable {

    private RecipeType<T> recipeType;
    private RecipeInventoryWrapper recipeInventoryWrapper;
    protected Optional<T> workingRecipe = Optional.empty();
    protected boolean running;
    protected int duration;
    protected int maxDuration;
    protected boolean stall;

    public RecipeWorkingBlockEntity(BlockEntityType<?> type, RecipeType<T> recipeType, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    protected void setRecipeInventory(RecipeInventoryWrapper recipe) {
        this.recipeInventoryWrapper = recipe;
    }

    @Override
    public void tick() {
        if (!stall) {
            if (workingRecipe.isEmpty()) {
                doRecipeCheck().ifPresent(this::doRecipe);
            }
            else doRecipe(getWorkingRecipeUnsafe());
        }
        super.tick();
    }

    public RecipeInventoryWrapper getRecipeInventoryWrapper() {
        return recipeInventoryWrapper;
    }
    public Optional<T> getWorkingRecipeOptional() {
        return workingRecipe;
    }
    public T getWorkingRecipeUnsafe() {
        return workingRecipe.orElse(null);
    }

    public boolean isRunning() {
        return running;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int amount) {
        this.duration = amount;
    }

    public void setRunning(T workingRecipe) {
        this.workingRecipe = Optional.of(workingRecipe);
        this.duration = workingRecipe.getRecipe().getDuration();
        this.running = true;
        this.maxDuration = workingRecipe.getRecipe().getDuration();
    }

    public void setWorkingRecipe(T workingRecipe) {
        if (workingRecipe == null) {
            this.workingRecipe = Optional.empty();
        }
        else this.workingRecipe = Optional.of(workingRecipe);
    }

    protected void finishRecipe() {
        running = false;
        this.workingRecipe = Optional.empty();
        this.duration = 0;
        syncBlockEntity();
    }

    public boolean isStalled() {
        return stall;
    }

    public void setStall(boolean stall) {
        this.stall = stall;
    }

    public int getMaxDuration() {
        return maxDuration;
    }

    protected Optional<T> doRecipeCheck() {
        return level.getRecipeManager().getRecipeFor(recipeType, getRecipeInventoryWrapper(), level);
    }

    protected abstract boolean hasAResult(T workingRecipe);

    protected abstract void onRecipeFinish(T workingRecipe);

    protected void doRecipe(T workingRecipe) {

        // check for a result if we don't we skip this
        if (!hasAResult(workingRecipe)) {
            finishRecipe();
            return;
        }

        // getting this far we must have a valid recipe and a valid result (either fluid or item)

        if (!isRunning()) {
            setRunning(workingRecipe);
        }

        if (isRunning()) {

            if (getDuration() <= 0) onRecipeFinish(workingRecipe);

            if (workingRecipe.requiresCondition()) {
                if (!checkConditionForRecipeTick(workingRecipe)) return;
            }

            duration--;

            syncBlockEntity();

        }

    }

    public static List<Integer> hasEnoughFluidSlots(FluidStack[] fResult, IFluidHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (FluidStack resultStack : fResult) {
            for (int index = 0; index < handler.getTanks(); index++) {
                if (handler instanceof SidedMultifluidTank.OutputWrapper wrapper) {
                    FluidStorageTank tank = wrapper.getStorageTank(index);
                    int filledAmount = tank.fill(resultStack, IFluidHandler.FluidAction.SIMULATE);
                    if (!slotsUsed.contains(index) && filledAmount == resultStack.getAmount()) {
                        slotsUsed.add(index);
                        break;
                    }
                } else {
                    int filledAmount = handler.fill(resultStack, IFluidHandler.FluidAction.SIMULATE);
                    if (!slotsUsed.contains(index) && filledAmount == resultStack.getAmount()) {
                        slotsUsed.add(index);
                        break;
                    }
                }
            }
        }
        return slotsUsed;
    }

    public static void useFluidIngredients(NonNullList<FluidIngredient> fluidIngredients, IFluidHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (FluidIngredient ingredient : fluidIngredients) {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack tank = handler.getFluidInTank(i);
                if (ingredient.test(tank)) {
                    slotsUsed.add(i);
                    tank.shrink(ingredient.getProxy().getAmount());
                    break;
                }
            }
        }
    }

    public static void useIngredients(NonNullList<Ingredient> specificIngredients, IItemHandler handler, int amountToShrink) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (Ingredient ingredient : specificIngredients) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!slotsUsed.contains(i) && ingredient.test(handler.getStackInSlot(i))) {
                    slotsUsed.add(i);
                    handler.getStackInSlot(i).shrink(amountToShrink);
                    break;
                }
            }
        }
    }

    public static void useSizeSpecificIngredients(NonNullList<SizeSpecificIngredient> specificIngredients, IItemHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (SizeSpecificIngredient ingredient : specificIngredients) {
            for (int i = 0; i < handler.getSlots(); i++) {
                if (!slotsUsed.contains(i) && ingredient.test(handler.getStackInSlot(i))) {
                    slotsUsed.add(i);
                    handler.getStackInSlot(i).shrink(ingredient.getSize());
                    break;
                }
            }
        }
    }

    public static void useSizeSpecificIngredients(NonNullList<SizeSpecificIngredient> specificIngredients, IItemHandler handler, int slots) {
        List<Integer> slotsUsed = new ArrayList<>();
        for (SizeSpecificIngredient ingredient : specificIngredients) {
            for (int i = 0; i < slots; i++) {
                if (!slotsUsed.contains(i) && ingredient.test(handler.getStackInSlot(i))) {
                    slotsUsed.add(i);
                    handler.getStackInSlot(i).shrink(ingredient.getSize());
                    break;
                }
            }
        }
    }

    public static List<Integer> hasEnoughItemSlots(ItemStack[] result, IItemHandler handler) {
        List<Integer> slotsUsed = new ArrayList<>();

        for (ItemStack resultItemStack : result) {
            for (int index = 0; index < handler.getSlots(); index++) {
                ItemStack ret = handler.insertItem(index, resultItemStack, true);
                if (handler instanceof ItemStackInventory.OutputHandler outputHandler) {
                    ret = outputHandler.forceInsertItem(index, resultItemStack, true);
                }
                if (!slotsUsed.contains(index) && ret.isEmpty()) {
                    slotsUsed.add(index);
                    break;
                }
            }
        }
        return slotsUsed;
    }

    public static void transferFluidResults(FluidStack[] result, IFluidHandler handler) {
        for (FluidStack resultFluidStack : result) {
            if (handler instanceof SidedMultifluidTank.OutputWrapper wrapper) {
                wrapper.forceFill(resultFluidStack, IFluidHandler.FluidAction.EXECUTE);
                continue;
            }
            handler.fill(resultFluidStack, IFluidHandler.FluidAction.EXECUTE);
        }
    }
    
    public static void transferItemResults(ItemStack[] result, IItemHandler handler) {
        for (ItemStack resultItemStack : result) {
            for (int index = 0; index < handler.getSlots(); index++) {
                if (handler instanceof ItemStackInventory.OutputHandler wrapper) {
                    if (wrapper.forceInsertItem(index, resultItemStack, true).isEmpty()) {
                        wrapper.forceInsertItem(index, resultItemStack, false);
                        break;
                    }
                } else {
                    if (handler.insertItem(index, resultItemStack, true).isEmpty()) {
                        handler.insertItem(index, resultItemStack, false);
                        break;
                    }
                }
            }
        }
    }

    protected boolean checkConditionForRecipeTick(RecipeInteraction recipe) {
        return true;
    }
}
