package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.ElectrolyzerMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.ElectrolyzerRecipe;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Optional;
import java.util.stream.Stream;

public class ElectrolyzerEntity extends RecipeWorkingBlockEntity implements ICustomCollisionShape {

    private int feTick = 5;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };
    private final SidedMultifluidTank multifluidTank = new SidedMultifluidTank(1, 16000, 2, 8000);
    private final ItemStackHandler itemStackHandler = new ItemStackHandler(3);

    public ElectrolyzerEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.ELECTROLYZER_ENTITY.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(multifluidTank.getInputHandler(), itemStackHandler));
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        multifluidTank.invalidate();
        super.invalidateCaps();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ENERGY) {
            return this.energyHandler.getHandler(side);
        }
        else if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return this.multifluidTank.getHandler(side);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void doRecipeCheck() {
        Optional<ElectrolyzerRecipe> recipe = level.getRecipeManager().getRecipeFor(Registry.ELECTROLYZER_RECIPE.get(), getRecipeInventoryWrapper(), level);
        recipe.ifPresent(this::doRecipe);
    }

    @Override
    public void doRecipe(RecipeInteraction workingRecipe) {
        ElectrolyzerRecipe recipe = (ElectrolyzerRecipe) workingRecipe.getRecipe();
        FluidStack[] results = recipe.assembleFluids(getRecipeInventoryWrapper());

        // recipe doesn't have a result return
        if (results.length == 0) return;

        // check if fluid is still there
        if (multifluidTank.getInputHandler().getFluidInTank(0).isEmpty()) {
            running = false;
            return;
        }

        // hasn't starting running but we have a working recipe
        if (!isRunning()) {
            setRunning(recipe);
        }

        // finish recipe
        if (getDuration() <= 0) {


            // check if output tank 1 is valid
            if (!this.multifluidTank.getOutputHandler().getStorageTank(0).isEmpty()) {
                if (!this.multifluidTank.getOutputHandler().getStorageTank(0).getFluid().isFluidEqual(results[0])) return;
            }

            // check if output tank 2 is valid
            if (!this.multifluidTank.getOutputHandler().getStorageTank(1).isEmpty()) {
                if (!this.multifluidTank.getOutputHandler().getStorageTank(1).getFluid().isFluidEqual(results[1])) return;
            }

            // drain our input tank by recipe amount
            recipe.getFluidIngredient().drain(multifluidTank.getInputHandler().getFluidInTank(0), false);

            // now fill both output tanks
            multifluidTank.getOutputHandler().getStorageTank(0).fill(results[0], IFluidHandler.FluidAction.EXECUTE);
            multifluidTank.getOutputHandler().getStorageTank(1).fill(results[1], IFluidHandler.FluidAction.EXECUTE);

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
        pTag.put("fluidHandler", this.multifluidTank.serialize());
        pTag.put("itemHandler", this.itemStackHandler.serializeNBT());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.multifluidTank.deserializeNBT(pTag.getCompound("fluidHandler"));
        this.itemStackHandler.deserializeNBT(pTag.getCompound("itemHandler"));
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        super.addToUpdateTag(tag);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Electrolyzer Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new ElectrolyzerMenu(p_39954_, p_39955_, p_39956_, this);
    }

    public SidedMultifluidTank getMultifluidTank() {
        return multifluidTank;
    }

    @Override
    public VoxelShape getShape() {
        return Stream.of(
                Block.box(0, 0, 8, 16, 16, 16),
                Block.box(0, 0, 0, 16, 2, 8),
                Block.box(0, 14, 1, 16, 16, 8),
                Block.box(1, 2, 1, 7, 14, 7),
                Block.box(9, 2, 1, 15, 14, 7)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    }
}
