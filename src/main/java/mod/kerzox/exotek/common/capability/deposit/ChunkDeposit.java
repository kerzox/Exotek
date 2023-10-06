package mod.kerzox.exotek.common.capability.deposit;

import com.sk89q.worldedit.world.chunk.Chunk;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.datagen.ChunkDepositData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class ChunkDeposit implements IDeposit, ICapabilitySerializable<CompoundTag> {

    private LevelChunk chunk;
    private FluidDeposit fluidDeposit;
    private OreDeposit oreDeposit;
    private LazyOptional<ChunkDeposit> handler = LazyOptional.of(() -> this);

    public static final String DEPOSIT_NBT_TAG = "deposit_information";

    public ChunkDeposit(LevelChunk chunk) {
        // 30% chance of something spawning 50/50 for either fluid or ore deposit
        if (Math.random() < .3) {
            if (Math.random() <.5) {
                fluidDeposit = FluidDeposit.random();
            } else {
                // Calculate the total probability (sum of all percentages)
                double totalProbability = ChunkDepositData.ORE_VEINS.values().stream().mapToDouble(item -> item.spawnPercentage).sum();

                // Generate a random number between 0 and 1
                Random random = new Random();
                double randomNumber = random.nextDouble();

                // Initialize variables to keep track of the accumulated chance and the chosen item
                double accumulatedChance = 0;
                OreDeposit chosenItem = null;

                // Iterate through the list and find the item that matches the random number
                for (OreDeposit item : ChunkDepositData.ORE_VEINS.values()) {
                    accumulatedChance += item.spawnPercentage / totalProbability;
                    if (randomNumber <= accumulatedChance) {
                        chosenItem = item;
                        break;
                    }
                }

                if (chosenItem != null) oreDeposit = chosenItem.randomizeAmount();
            }
        }
    }

    public static ChunkDeposit getDepositFromPosition(Level level, BlockPos pos) {
        LazyOptional<IDeposit> cap = level.getChunkAt(pos).getCapability(ExotekCapabilities.DEPOSIT_CAPABILITY);
        if (cap.isPresent()) {
            if (cap.resolve().isEmpty()) return null;
            if (cap.resolve().get() instanceof ChunkDeposit deposit) {
                return deposit;
            }
        }
        return null;
    }

    public void setChanged() {

    }

    public FluidDeposit getFluidDeposit() {
        return fluidDeposit;
    }

    public OreDeposit getOreDeposit() {
        return oreDeposit;
    }

    @Override
    public boolean isOreDeposit() {
        return oreDeposit != null;
    }

    @Override
    public boolean isFluidDeposit() {
        return fluidDeposit != null;
    }

    @Override
    public List<OreDeposit.OreStack> getItems() {
        return !isOreDeposit() ? new ArrayList<>() : oreDeposit.getItems();
    }

    @Override
    public List<FluidStack> getFluids() {
        return !isFluidDeposit() ? new ArrayList<>() : fluidDeposit.getFluids();
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (isOreDeposit()) oreDeposit.serialize(tag);
        if (isFluidDeposit()) fluidDeposit.serialize(tag);
        CompoundTag ret = new CompoundTag();
        ret.put(DEPOSIT_NBT_TAG, tag);
        return ret;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains(DEPOSIT_NBT_TAG)) {
            CompoundTag tag = nbt.getCompound(DEPOSIT_NBT_TAG);
            if (tag.contains("item_list")) {
                oreDeposit = new OreDeposit();
                oreDeposit.deserialize(tag);
            }
            if (tag.contains("fluid_list")) {
                fluidDeposit = new FluidDeposit();
                fluidDeposit.deserialize(tag);
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ExotekCapabilities.DEPOSIT_CAPABILITY.orEmpty(cap, handler.cast());
    }
}
