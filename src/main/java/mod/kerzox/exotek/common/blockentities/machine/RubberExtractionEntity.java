package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.CompressorMenu;
import mod.kerzox.exotek.client.gui.menu.RubberExtractionMenu;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.FluidStorageTank;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.EngraverRecipe;
import mod.kerzox.exotek.common.crafting.recipes.MaceratorRecipe;
import mod.kerzox.exotek.common.crafting.recipes.RubberExtractionRecipe;
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
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class RubberExtractionEntity extends RecipeWorkingBlockEntity<RubberExtractionRecipe> {

    private int feTick = 20;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };
    private final SidedSingleFluidTank sidedSingleFluidTank = new SidedSingleFluidTank(16000);
    private final ItemStackHandler hiddenInventory = new ItemStackHandler(1);

    private Direction currentWorkingDirection = Direction.NORTH;

    public RubberExtractionEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.RUBBER_EXTRACTION_ENTITY.get(), Registry.RUBBER_EXTRACTION_RECIPE.get(), pos, state);
        setRecipeInventory(new RecipeInventoryWrapper(hiddenInventory));
        addCapabilities(energyHandler, sidedSingleFluidTank);
    }

    @Override
    public void tick() {
        System.out.println(sidedSingleFluidTank.getFluid().getAmount());
        Optional<RubberExtractionRecipe> recipe = Optional.empty();
        for (Direction direction : Direction.values()) {
            Block block = level.getBlockState(worldPosition.relative(direction)).getBlock();
            hiddenInventory.setStackInSlot(0, new ItemStack(block));
            recipe = level.getRecipeManager().getRecipeFor(Registry.RUBBER_EXTRACTION_RECIPE.get(), getRecipeInventoryWrapper(), level);
            if (recipe.isPresent()) {
                currentWorkingDirection = direction;
                break;
            }
        }
        recipe.ifPresent(this::doRecipe);
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

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("fluidHandler", this.sidedSingleFluidTank.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.sidedSingleFluidTank.deserialize(pTag.getCompound("fluidHandler"));
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
        return Component.literal("Compressor Machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new RubberExtractionMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
