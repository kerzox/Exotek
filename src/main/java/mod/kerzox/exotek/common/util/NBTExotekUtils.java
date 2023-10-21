package mod.kerzox.exotek.common.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NBTExotekUtils {

    public static void saveBlockPositions(CompoundTag tag, Collection<BlockPos> positions) {
        ListTag arr = new ListTag();
        for (BlockPos pos : positions) {
            arr.add(NbtUtils.writeBlockPos(pos));
        }
        tag.put("positions", arr);
    }

    public static Collection<BlockPos> getBlockPositions(CompoundTag tag) {
        ListTag list = tag.getList("positions", Tag.TAG_COMPOUND);
        Collection<BlockPos> collection = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            collection.add(NbtUtils.readBlockPos(list.getCompound(i)));
        }
        return collection;
    }

}
