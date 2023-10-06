package mod.kerzox.exotek.common.capability.deposit;

import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class FluidDeposit implements IDepositResource {

    private static final int minimumDepositAmount = 64000; // in mb
    private static final int maximumDepositAmount = minimumDepositAmount * 3; // in mb
    protected List<FluidStack> fluids = new ArrayList<>();

    public FluidDeposit(List<FluidStack> fluidStacks) {
        this.fluids = fluidStacks;
    }

    public FluidDeposit() {
        this.fluids = new ArrayList<>();
    }

    public List<FluidStack> getFluids() {
        return fluids;
    }

    public int getReservoirAmount() {
        return fluids.get(0).getAmount();
    }

    public static FluidDeposit random() {
        Random random = new Random();
        Fluid fluid = Fluids.EMPTY;
        switch (random.nextInt(2)) {
            case 0 -> fluid = Registry.Fluids.PETROLEUM.getFluid().get();
            case 1 -> fluid = Fluids.WATER;
            case 2 -> fluid = Fluids.LAVA;
        }
        return new FluidDeposit(List.of(new FluidStack(fluid, random.nextInt(minimumDepositAmount, maximumDepositAmount))));


    }

    @Override
    public void serialize(CompoundTag tag) {
        ListTag fluidList = new ListTag();
        for (FluidStack fluid : getFluids()) {
            CompoundTag tag1 = new CompoundTag();
            fluidList.add(fluid.writeToNBT(tag1));
        }
        tag.put("fluid_list", fluidList);
    }

    @Override
    public void deserialize(CompoundTag tag) {
        ListTag fluidList = tag.getList("fluid_list", Tag.TAG_COMPOUND);
        for (int i = 0; i < fluidList.size(); i++) {
            fluids.add(FluidStack.loadFluidStackFromNBT(fluidList.getCompound(i)));
        }
    }
}
