package mod.kerzox.exotek.common.blockentities.multiblock.validator.data;

import com.google.gson.JsonObject;
import net.minecraft.core.BlockPos;
import net.minecraft.util.GsonHelper;

public class MultiblockStructure {

    private BlockPos min;
    private BlockPos max;

    public MultiblockStructure(BlockPos min, BlockPos max) {
        this.max = max;
        this.min = min;
    }
    public MultiblockStructure(BlockPos max) {
        this.max = max;
        this.min = new BlockPos(0, 0, 0);
    }

    public MultiblockStructure(JsonObject json) {
        JsonObject object = json.getAsJsonObject("structure_size");
        this.max = new BlockPos(GsonHelper.getAsInt(object, "length"), GsonHelper.getAsInt(object, "height"), GsonHelper.getAsInt(object, "width"));
        this.min = new BlockPos(0, 0, 0);
    }

    public MultiblockStructure(int length, int width, int height) {
        this.max = new BlockPos(length, height, width);
        this.min = new BlockPos(0, 0, 0);
    }

    public BlockPos getMax() {
        return max;
    }

    public BlockPos getMin() {
        return min;
    }

    public BlockPos getCenter() {
        return new BlockPos((getMin().getX() + getMax().getX()) / 2,
                (getMin().getY() + getMax().getY()) / 2,
                (getMin().getZ() + getMax().getZ()) / 2);
    }

    public int length() {
        return max.getX();
    }

    public int width() {
        return max.getZ();
    }

    public int height() {
        return max.getY();
    }

    public BlockPos[] getStructureAsArray() {
        BlockPos[] ret = new BlockPos[length()*width()*height()];
        int i = 0;
        for (int x = getMin().getX(); x <= getMax().getX(); x++) {
            for (int y = getMin().getY(); y <= getMax().getY(); y++) {
                for (int z = getMin().getZ(); z <= getMax().getZ(); z++) {
                    ret[i] = new BlockPos(x, y, z);
                    i++;
                }
            }
        }
        return ret;
    }

}
