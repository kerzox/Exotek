package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.ManufactoryMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.CraftingInventoryWrapper;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.ChemicalReactorRecipe;
import mod.kerzox.exotek.common.crafting.recipes.CircuitAssemblyRecipe;
import mod.kerzox.exotek.common.crafting.recipes.ManufactoryRecipe;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

//TODO
// Just bite the fucking bullet and remove the recipe working block entity from this is code is garbage

public class ManufactoryEntity extends RecipeWorkingBlockEntity implements ICustomCollisionShape {

    private int feTick = 20;
    protected Optional<CraftingRecipe> vanillaWorkingRecipe = Optional.empty();
    private boolean lock = false;

    private ItemStack resultToShow = ItemStack.EMPTY;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };

    private final SidedMultifluidTank sidedMultifluidTank = new SidedMultifluidTank(3, 16000, 0, 0) {
        @Override
        protected void onContentsChanged(IFluidHandler handlerAffected) {
            if (!(handlerAffected instanceof OutputWrapper)) { // ignore output handler
                running = false;
                if (!isLocked()) doRecipeCheck();
            }
        }

    };

    private final ItemStackInventory itemHandler = new ItemStackInventory(9, 1) {
        @Override
        protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
            if (!(handler instanceof PlayerWrapper)) {
                if (!isLocked()) {
                    resultToShow = ItemStack.EMPTY;
                }
            }
        }
    };
    private final CraftingInventoryWrapper craftingInventoryWrapper = new CraftingInventoryWrapper(itemHandler.getInputHandler(), 3, 3);

    public ManufactoryEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.MANUFACTORY_ENTITY.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(sidedMultifluidTank, itemHandler));
        setStall(true);
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemHandler.invalidate();
        sidedMultifluidTank.invalidate();
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
        else if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.sidedMultifluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
    }
    // .....???? terrible
    @Override
    public void tick() {
        if (!isLocked()) {
            doRecipeCheck();
        }
        if (!stall) {
            workingRecipe.ifPresent(this::doRecipe);
            vanillaWorkingRecipe.ifPresent(this::doRecipeVanilla);
        }
    }

    public boolean isLocked() {
        return lock;
    }

    public void setLocked(boolean lock) {
        this.lock = lock;
//        if (lock) {
//            stall = false;
//        } else {
//            stall = true;
//        }
    }

    @Override
    public void doRecipeCheck() {
        Optional<ManufactoryRecipe> recipe = level.getRecipeManager().getRecipeFor(Registry.MANUFACTORY_RECIPE.get(), getRecipeInventoryWrapper(), level);
        Optional<CraftingRecipe> recipe2 = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,  craftingInventoryWrapper, level);
        recipe.ifPresent(this::doRecipe);
        recipe2.ifPresent(this::doRecipeVanilla);
        if (recipe.isEmpty() && recipe2.isEmpty() && this.getWorkingRecipeOptional().isPresent()) {
            this.setWorkingRecipe(null);
        }

    }
    // my eyes
    public void doRecipeVanilla(CraftingRecipe recipe) {
        ItemStack result = recipe.assemble(craftingInventoryWrapper, RegistryAccess.EMPTY);
        // recipe doesn't have a result return
        if (result.isEmpty()) return;

        Optional<CraftingRecipe> check = level.getRecipeManager().getRecipeFor(RecipeType.CRAFTING,  craftingInventoryWrapper, level);

        if (isLocked()) {
            resultToShow = result;
        }

        if (check.isEmpty() || !check.get().getResultItem(RegistryAccess.EMPTY).copy().is(result.getItem())) return;

        resultToShow = result;

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            this.vanillaWorkingRecipe = Optional.of(recipe);
            this.duration = 5;
            this.running = true;
            this.maxDuration = 5;
        }

        if (stall) return;

        // finish recipe
        if (getDuration() <= 0) {

            if (!this.itemHandler.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) return;

            Iterator<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients()).iterator();

            int index = 0;

            for (int i = 0; i < itemHandler.getInputHandler().getSlots(); i++) {
                if (!itemHandler.getInputHandler().getStackInSlot(i).isEmpty()) {
                    index = i;
                    break;
                }
            }

            while(ingredients.hasNext()) {
                Ingredient ingredient = ingredients.next();
                for (int i = index; i < itemHandler.getInputHandler().getSlots(); i++) {
                    if (ingredient.test(itemHandler.getInputHandler().getStackInSlot(i))) {
                        this.itemHandler.getInputHandler().getStackInSlot(i).shrink(1);
                        index++;
                        break;
                    }
                }
            }

            this.itemHandler.getOutputHandler().forceInsertItem(0, result, false);

            running = false;
            if (!isLocked()) this.vanillaWorkingRecipe = Optional.empty();
            this.duration = 0;
            syncBlockEntity();
            return;
        }

        // do power consume TODO ideally we want the machine to stop working until energy returns this will be looked into after
        if (!energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
            return;
        };

        energyHandler.consumeEnergy(feTick);
        duration--;

    }

    @Override
    public void doRecipe(RecipeInteraction workingRecipe) {
        ManufactoryRecipe recipe = (ManufactoryRecipe) workingRecipe.getRecipe();
        ItemStack result = recipe.assemble(getRecipeInventoryWrapper(), RegistryAccess.EMPTY);

        // recipe doesn't have a result return
        if (result.isEmpty()) return;

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipe);
            resultToShow = result;
        }

        if (stall) return;

        // finish recipe
        if (getDuration() <= 0) {


            if (!this.itemHandler.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) return;

            // take fluid from tanks

            Iterator<FluidIngredient> fluid_ingredients = new ArrayList<>(recipe.getFluidIngredients()).iterator();

            while (fluid_ingredients.hasNext()) {
                FluidIngredient fluidIngredient = fluid_ingredients.next();
                for (int i = 0; i < this.sidedMultifluidTank.getInputHandler().getTanks(); i++) {
                    if (fluidIngredient.testWithoutAmount(this.sidedMultifluidTank.getInputHandler().getStorageTank(i).getFluid())) {

                        FluidStack fluidStack = new FluidStack(this.sidedMultifluidTank.getInputHandler().getStorageTank(i).getFluid().getFluid(),
                                fluidIngredient.getFluidStacks().get(0).getAmount());

                        sidedMultifluidTank.getInputHandler().forceDrain(fluidStack, IFluidHandler.FluidAction.EXECUTE);

                        // remove from the iterator
                        fluid_ingredients.remove();
                        break;
                    }
                }
            }

            Iterator<Ingredient> ingredients = new ArrayList<>(recipe.getIngredients()).iterator();

            while (ingredients.hasNext()) { //TODO REPLACE WITH SIZE SPECIFIC INGREDIENTS
                Ingredient ingredient = ingredients.next();
                for (int i = 0; i < this.itemHandler.getSlots(); i++) {
                    if (ingredient.test(this.itemHandler.getStackInSlot(i))) {
                        this.itemHandler.getStackInSlot(i).shrink(1);
                    }
                }
            }

            this.itemHandler.getOutputHandler().forceInsertItem(1, result, false);

            running = false;
            if (!isLocked()) this.workingRecipe = Optional.empty();
            this.duration = 0;
            syncBlockEntity();

            return;
        }

        // do power consume TODO ideally we want the machine to stop working until energy returns this will be looked into after
        if (!energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
            return;
        };

        energyHandler.consumeEnergy(feTick);
        duration--;
    }

    public SidedMultifluidTank getSidedMultifluidTank() {
        return sidedMultifluidTank;
    }

    public ItemStack getResultToShow() {
        return resultToShow;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("fluidHandler", this.sidedMultifluidTank.serializeNBT());
        pTag.putBoolean("locked", this.lock);
        pTag.putBoolean("stall", this.stall);
        pTag.putBoolean("result", !resultToShow.isEmpty());
        pTag.put("result_itemstack", resultToShow.serializeNBT());
        pTag.put("itemHandler", this.itemHandler.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.sidedMultifluidTank.deserializeNBT(pTag.getCompound("fluidHandler"));
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
        this.lock = pTag.getBoolean("locked");
        this.stall = pTag.getBoolean("stall");
        this.itemHandler.deserialize(pTag.getCompound("itemHandler"));
        if (pTag.getBoolean("result")) {
            resultToShow = ItemStack.of(pTag.getCompound("result_itemstack"));
        } else {
            resultToShow = ItemStack.EMPTY;
        }
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        tag.put("energyHandler", this.energyHandler.serialize());
        tag.put("fluidHandler", this.sidedMultifluidTank.serializeNBT());
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
        tag.putBoolean("locked", this.lock);
        tag.putBoolean("stall", this.stall);
        tag.putBoolean("result", !resultToShow.isEmpty());
        tag.put("result_itemstack", resultToShow.serializeNBT());
        tag.put("itemHandler", this.itemHandler.serialize());
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Manufactory");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new ManufactoryMenu(p_39954_, p_39955_, p_39956_, this);
    }

    public CraftingInventoryWrapper getCraftingInventoryWrapper() {
        return craftingInventoryWrapper;
    }

    @Override
    public VoxelShape getShape() {
        return Stream.of(
                Block.box(0, 2, 8, 16, 16, 16),
                Block.box(0, 0, 0, 16, 2, 16),
                Block.box(1, 2, 1, 11, 15, 8),
                Block.box(15, 2, 0, 16, 16, 8),
                Block.box(11, 2, 0, 12, 16, 8),
                Block.box(13, 2, 0, 14, 16, 8),
                Block.box(12, 2, 1, 13, 16, 8),
                Block.box(14, 2, 1, 15, 16, 8)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }
}
