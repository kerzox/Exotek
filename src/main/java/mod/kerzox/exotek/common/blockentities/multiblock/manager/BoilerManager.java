package mod.kerzox.exotek.common.blockentities.multiblock.manager;

import mod.kerzox.exotek.common.blockentities.multiblock.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
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

public class BoilerManager extends AbstractMultiblockManager {

    private ItemStackInventory inventory = new ItemStackInventory(1, 0);
    private SidedMultifluidTank multifluidTank = new SidedMultifluidTank(1, 32000, 1, 32000);
    private int heat = 0;
    private int heatNeeded = 100;
    private int burnTime = 0;
    private int tick = 0;
    private int maxHeat = 350;

    public BoilerManager() {
        super("boiler");
        multifluidTank.addInput(Direction.values());
        multifluidTank.addOutput(Direction.values());
    }

    @Override
    protected void tick() {
        tick++;
        FluidStack inputStack = multifluidTank.getInputHandler().getFluidInTank(0);
        ItemStack itemStack = inventory.getStackInSlot(0);

        if (burnTime <= 0) {
            burnTime = ForgeHooks.getBurnTime(itemStack, null);
            if (burnTime != 0) itemStack.shrink(1);
        }

        // generate heat
        if (burnTime != 0) {
            burnTime--;
            if (burnTime % 40 == 0) {
                if (heat < maxHeat) heat++;
            }
        } else {
            if (heat > 0) {
                if (tick % (20 * 6) == 0) { // loses heat 6 seconds
                    heat--;
                }
            }
        }

        if (!ForgeRegistries.FLUIDS.tags().getTag(FluidTags.WATER).contains(inputStack.getFluid())) return;

        if (heat >= heatNeeded) {

            int steamGeneration = (int) Math.round(0.002 * Math.pow(heat, 2) + 0);

            FluidStack outputStack = new FluidStack(Registry.Fluids.STEAM.getFluid().get(), steamGeneration);
            inputStack.shrink(multifluidTank.getOutputHandler().forceFill(outputStack, IFluidHandler.FluidAction.EXECUTE));
        }

    }

    public int getHeat() {
        return heat;
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
        tag.put("itemHandler", this.inventory.serialize());
        tag.put("fluidHandler", this.multifluidTank.serialize());
        tag.putInt("heat", this.heat);
        return tag;
    }

    @Override
    public void readCache(CompoundTag tag) {
        if(this.inventory != null)  this.inventory.deserialize(tag.getCompound("itemHandler"));
        if (this.multifluidTank != null) this.multifluidTank.deserialize(tag.getCompound("fluidHandler"));
        this.heat = tag.getInt("heat");
    }

    public SidedMultifluidTank getMultifluidTank() {
        return multifluidTank;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull MultiblockEntity multiblockEntity, @NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return this.inventory.getHandler(side);
        }
        return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.multifluidTank.getHandler(side));
    }
}
