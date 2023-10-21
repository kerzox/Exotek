package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.TurbineRecipe;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TurbineManager extends AbstractMultiblockManager {

    private int rotationGainPerTick = 2;
    private int rotationLossPerTick = rotationGainPerTick * 4;
    private int maxSpeed = 1000;

    private int burnRemaining = 0;
    private int rotationSpeed = 0;

    private boolean running = false;

    private SidedMultifluidTank multifluidTank = new SidedMultifluidTank(1, 32000, 1, 32000);

    public TurbineManager() {
        super("turbine");
        multifluidTank.addInput(Direction.values());
        multifluidTank.addOutput(Direction.values());
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    protected void tick() {
        if (getLevel() != null) {
            FluidStack inputStack = multifluidTank.getInputHandler().getFluidInTank(0);
            if (running) {
                Optional<TurbineRecipe> recipe = getLevel().getRecipeManager().getRecipeFor(Registry.TURBINE_RECIPE.get(), new RecipeInventoryWrapper(multifluidTank), getLevel());

                recipe.ifPresentOrElse(r -> doRecipe(inputStack, r), this::loseSpeed);

                if (rotationSpeed > 0) {
                    BlockEntity blockEntity = getLevel().getBlockEntity(getManagingBlockEntity().getFirst()
                            .relative(getValidationDirection(), 7));
                    if (blockEntity instanceof MultiblockEntity entity) {
                        if (entity.getMultiblockManager() instanceof AlternatorManager alternatorManager) {
                            alternatorManager.addEnergyFromKinetic(rotationSpeed);
                        }
                    }
                }
            } else {
                loseSpeed();
            }

        }
    }

    private void doRecipe(FluidStack inputStack, TurbineRecipe recipe) {
        FluidStack outputStack = multifluidTank.getOutputHandler().getFluidInTank(0);
        int toDrain = recipe.getFluidIngredients().get(0).getProxy().getAmount();
//        if (multifluidTank.getOutputHandler().forceFill(recipe.getResult().copy(), IFluidHandler.FluidAction.SIMULATE) < toDrain) {
//            loseSpeed();
//            return;
//        }
        if (inputStack.getAmount() < toDrain) {
            inputStack.shrink(Math.min(inputStack.getAmount(), toDrain));
            loseSpeed();
            return;
        }
        inputStack.shrink(Math.min(inputStack.getAmount(), toDrain));
        multifluidTank.getOutputHandler().forceFill(recipe.getResult().copy(), IFluidHandler.FluidAction.EXECUTE);
        rotationSpeed = Math.min(maxSpeed, rotationSpeed + rotationGainPerTick);

    }


    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getBurnRemaining() {
        return burnRemaining;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public int getRotationGainPerTick() {
        return rotationGainPerTick;
    }

    public int getRotationLossPerTick() {
        return rotationLossPerTick;
    }

    public int getRotationSpeed() {
        return rotationSpeed;
    }

    private void loseSpeed() {
        rotationSpeed = Math.max(0, rotationSpeed - rotationLossPerTick);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return false;
    }

    @Override
    public boolean onPlayerAttack(Level pLevel, Player pPlayer, BlockPos pPos) {
        return false;
    }

    @Override
    public CompoundTag writeToCache() {
        CompoundTag tag = super.writeToCache();
        tag.put("fluidHandler", this.multifluidTank.serialize());
        tag.putInt("burnRemaining", this.burnRemaining);
        tag.putInt("rotationSpeed", this.rotationSpeed);
        tag.putBoolean("running", this.running);
        return tag;
    }

    @Override
    public void readCache(CompoundTag tag) {
        if (this.multifluidTank != null) this.multifluidTank.deserialize(tag.getCompound("fluidHandler"));
        this.burnRemaining = tag.getInt("burnRemaining");
        this.rotationSpeed = tag.getInt("rotationSpeed");
        this.running = tag.getBoolean("running");
    }

    public SidedMultifluidTank getMultifluidTank() {
        return multifluidTank;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.multifluidTank.getHandler(side));
    }
}
