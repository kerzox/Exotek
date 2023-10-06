package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.Config;
import mod.kerzox.exotek.client.gui.menu.FurnaceMenu;
import mod.kerzox.exotek.common.blockentities.ContainerisedBlockEntity;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.util.IServerTickable;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class FurnaceEntity extends ContainerisedBlockEntity implements IServerTickable {

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }
    };
    private final ItemStackInventory itemHandler = new ItemStackInventory(1, 1);
    private final RecipeInventoryWrapper recipeInventoryWrapper = new RecipeInventoryWrapper(itemHandler);
    private Optional<SmeltingRecipe> workingRecipe = Optional.empty();
    protected boolean running;
    protected int duration;
    protected int maxDuration;

    // add modifiers on tiers or something
    private int feTick = Config.FURNACE_FE_USAGE_PER_TICK;

    public FurnaceEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.FURNACE_ENTITY.get(), pos, state);
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
    public void tick() {
        if (workingRecipe.isEmpty()) doRecipeCheck();
        else doRecipe(workingRecipe.get());
    }

    public void doRecipeCheck() {
        Optional<SmeltingRecipe> vanillaFurnaceRecipes = level.getRecipeManager().getRecipeFor(RecipeType.SMELTING, recipeInventoryWrapper, level);
        vanillaFurnaceRecipes.ifPresent(this::doRecipe);
    }

    public void doRecipe(SmeltingRecipe recipe) {

        ItemStack result = recipe.assemble(recipeInventoryWrapper, RegistryAccess.EMPTY);

        if (itemHandler.getInputHandler().getStackInSlot(0).isEmpty()) {
            running = false;
            return;
        }

        // recipe doesn't have a result return
        if (result.isEmpty()) return;

        // hasn't starting running but we have a working recipe
        if (!running) {
            this.workingRecipe = Optional.of(recipe);
            this.duration = recipe.getCookingTime();
            this.maxDuration = recipe.getCookingTime();
            this.running = true;
        }

        // finish recipe
        if (duration <= 0) {
            running = false;
            // simulate the transfer of items
            ItemStack simulation = this.itemHandler.getInputHandler().forceExtractItem(0, 1, true);

            // check if we are still able to finish by placing the item etc. (Might change this) TODO
            if (simulation.isEmpty()) return;
            if (!this.itemHandler.getOutputHandler().forceInsertItem(0, result, true).isEmpty()) return;

            this.itemHandler.getInputHandler().getStackInSlot(0).shrink(1);
            this.itemHandler.getOutputHandler().forceInsertItem(0, result, false);

            syncBlockEntity();

            this.workingRecipe = Optional.empty();

            this.duration = 0;

            return;

        }

        // do power consume TODO ideally we want the machine to stop working until energy returns this will be looked into after
        if (!energyHandler.hasEnough(feTick)) {
            energyHandler.consumeEnergy(Math.max(0 , Math.min(feTick, energyHandler.getEnergy())));
            return;
        };

        energyHandler.consumeEnergy(feTick);
        duration--;

        //syncBlockEntity();
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
        tag.put("energyHandler", this.energyHandler.serialize());
        tag.put("itemHandler", this.itemHandler.serialize());
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
    }

    @Override
    public void invalidateCaps() {
        energyHandler.invalidate();
        itemHandler.invalidate();
        super.invalidateCaps();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Furnace Menu");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new FurnaceMenu(p_39954_, p_39955_, p_39956_, this);
    }

}
