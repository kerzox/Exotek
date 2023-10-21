package mod.kerzox.exotek.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Arrays;
import java.util.HashSet;

public interface IStrictInventory {

    HashSet<Direction> getInputs();
    HashSet<Direction> getOutputs();

    default void addInput(Direction... side) {
        getInputs().addAll(Arrays.asList(side));
    }

    default void addOutput(Direction... side) {
        getOutputs().addAll(Arrays.asList(side));
    }

    default void removeInputs(Direction... side) {
        Arrays.asList(side).forEach(getInputs()::remove);
    }

    default void removeOutputs(Direction... side) {
        Arrays.asList(side).forEach(getOutputs()::remove);
    }

    default boolean hasInput(Direction side) {
        return getInputs().contains(side);
    }

    default boolean hasOutput(Direction side) {
        return getOutputs().contains(side);
    }

    default CompoundTag serializeInputAndOutput() {
        CompoundTag tag = new CompoundTag();
        int[] dir = new int[] {-1, -1, -1, -1, -1, -1};
        for (int i = 0; i < Direction.values().length; i++) {
           if (getInputs().contains(Direction.values()[i])) {
                dir[i] = 1;
           }
           if (getOutputs().contains(Direction.values()[i])) {
               if (getInputs().contains(Direction.values()[i])) dir[i] = 2;
               else dir[i] = 0;
           }
        }
        tag.putIntArray("directions", dir);
        return tag;
    }

    default void deserializeInputAndOutput(CompoundTag tag) {
        int[] dir = tag.getIntArray("directions");

        if (dir.length > 0) {
            getInputs().clear();
            getOutputs().clear();
            for (int i = 0; i < dir.length; i++) {
                if (dir[i] == -1) {
                    removeOutputs(Direction.values()[i]);
                    removeInputs(Direction.values()[i]);
                }
                if (dir[i] == 0) addOutput(Direction.values()[i]);
                if (dir[i] == 1) addInput(Direction.values()[i]);
                if (dir[i] == 2) {
                    addOutput(Direction.values()[i]);
                    addInput(Direction.values()[i]);
                }
            }
        }

    }

}
