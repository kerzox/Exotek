package mod.kerzox.exotek.common.blockentities;

import com.mojang.datafixers.util.Pair;
import mod.kerzox.exotek.common.capability.fluid.FluidStorageTank;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.common.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.units.qual.C;

import java.util.*;

/*
    This machine can handle multiple recipes in parallel.
    Can take any recipe as long as it extends Recipe and a inventory that extends container for said recipe class
 */

public abstract class TieredRecipeWorkingBlockEntity<T extends Recipe<? extends Container>> extends MachineBlockEntity implements MenuProvider, IServerTickable, ITieredMachine {

    private RecipeType<T> recipeType;
    protected RecipeInventoryWrapper[] recipeInventoryWrapper;
    protected boolean running;
    protected boolean stall;
    protected NonNullList<Optional<Pair<T, int[]>>> workingRecipes = NonNullList.withSize(1, Optional.empty());
    protected boolean forceConditionCheck;
    private int[][] clientProgress = new int[1][];

    public TieredRecipeWorkingBlockEntity(BlockEntityType<?> type, RecipeType<T> recipeType, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.recipeType = recipeType;
    }

    public void setRecipeInventoryWrapper(RecipeInventoryWrapper[] recipeInventoryWrapper) {
        this.recipeInventoryWrapper = recipeInventoryWrapper;
    }

    public int[][] getClientProgress() {
        return clientProgress;
    }

    private MachineTier getTier() {
        return this.getTier(this);
    }

    @Override
    public void tick() {
        if (!stall) {
            for (int i = 0; i < recipeInventoryWrapper.length; i++) {

                // check if we are already working on this recipe if we are skip this recipe
                if (workingRecipes.get(i).isPresent()) continue;

                // try and get a recipe for this index
                Optional<T> recipe = doRecipeCheck(recipeInventoryWrapper[i]);

                // if one is valid then add that index to working recipes
                if (recipe.isPresent()) {
                    T ret = recipe.get();
                    if (ret instanceof AbstractCookingRecipe abstractCookingRecipe) { // use vanilla recipe cooking
                        this.workingRecipes.set(i, Optional.of(Pair.of(ret, new int[] {abstractCookingRecipe.getCookingTime(), abstractCookingRecipe.getCookingTime()})));
                    }
                    else if (ret instanceof AbstractRecipe<?> abstractRecipe) { // use our custom recipe duration
                        this.workingRecipes.set(i, Optional.of(Pair.of(ret, new int[] {abstractRecipe.getDuration(), abstractRecipe.getDuration()})));
                    } else { // just have duration set to 8 seconds
                        this.workingRecipes.set(i, Optional.of(Pair.of(ret, new int[] {20 * 8, 20 * 8})));
                    }
                }
            }
            for (int i = 0; i < workingRecipes.size(); i++) {
                if (workingRecipes.get(i).isPresent()) doRecipe(i, workingRecipes.get(i).get());
            }
        }
        super.tick();
    }


    public boolean isRunning() {
        return running;
    }

    public void setRunning(int index) {
        running = true;
    }

    protected void finishRecipe(int index) {
        this.workingRecipes.set(index, Optional.empty());
        syncBlockEntity();
    }

    public boolean isStalled() {
        return stall;
    }

    public void setStall(boolean stall) {
        this.stall = stall;
    }

    // this is horrible man am I just retarded or something but this is all i could think of.
    protected Optional<T> doRecipeCheck(RecipeInventoryWrapper wrapper) {
        Optional<?> ret = level.getRecipeManager().getRecipeFor((RecipeType) recipeType, wrapper, level);
        return ((Optional<T>) ret);
    }

    @Override
    protected void write(CompoundTag pTag) {
        ListTag list = new ListTag();
        for (int i = 0; i < workingRecipes.size(); i++) {
            Optional<Pair<T, int[]>> recipe = workingRecipes.get(i);
            if (recipe.isPresent()) {
                CompoundTag tag = new CompoundTag();
                int duration = recipe.get().getSecond()[0];
                int maxDuration = recipe.get().getSecond()[1];
                tag.putInt("slot", i);
                tag.putInt("duration", duration);
                tag.putInt("maxDuration", maxDuration);
                list.add(tag);
            }
        }
        pTag.put("recipe_progress", list);
    }

    @Override
    protected void read(CompoundTag pTag) {
        ListTag list = pTag.getList("recipe_progress", Tag.TAG_COMPOUND);
        if (list.size() > 0) clientProgress = new int[recipeInventoryWrapper.length][];
        for (int i = 0; i < list.size(); i++) {
            CompoundTag tag = list.getCompound(i);
            clientProgress[tag.getInt("slot")] = new int[] {tag.getInt("duration"), tag.getInt("maxDuration")};
        }
    }

    protected abstract boolean hasAResult(RecipeInventoryWrapper index, T workingRecipe);

    protected abstract void onRecipeFinish(int i, RecipeInventoryWrapper index, T workingRecipe);

    protected void doRecipe(int index, Pair<T, int[]> working) {
        T workingRecipe = working.getFirst();
        int duration = working.getSecond()[0];
        int maxDuration = working.getSecond()[1];

        // check for a result if we don't we skip this
        if (!hasAResult(recipeInventoryWrapper[index], workingRecipe)) {
            finishRecipe(index);
            return;
        }

        // just check if we still match
        if (doRecipeCheck(recipeInventoryWrapper[index]).isEmpty()) {
           finishRecipe(index);
            return;
        }

        // getting this far we must have a valid recipe and a valid result (either fluid or item)

        if (!isRunning()) {
            setRunning(index);
        }

        if (isRunning()) {

            if (duration <= 0) onRecipeFinish(index, recipeInventoryWrapper[index], workingRecipe);

            // this is so vanilla recipes can also use the conditional recipe checker (ie to use energy)
            if (forceConditionCheck) {
                if (!checkConditionForRecipeTick(index, workingRecipe)) return;
            }
            else if (workingRecipe instanceof AbstractRecipe<?> recipe) {
                if (recipe.requiresCondition()) { // check if the abstract recipe needs to pass an additional check such as energy usage.
                    if (!checkConditionForRecipeTick(index, workingRecipe)) return;
                }
            }

            if (this.workingRecipes.get(index).isPresent()) this.workingRecipes.get(index).get().getSecond()[0]--;

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

    protected boolean checkConditionForRecipeTick(int index, T workingRecipe) {
        return true;
    }
}
