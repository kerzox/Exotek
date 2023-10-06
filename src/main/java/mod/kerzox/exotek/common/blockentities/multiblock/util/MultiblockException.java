package mod.kerzox.exotek.common.blockentities.multiblock.util;

import net.minecraft.core.BlockPos;

public class MultiblockException extends Exception {

    private BlockPos offendingPosition;

    public MultiblockException(String msg, BlockPos offendingPosition) {
        super(msg);
        this.offendingPosition = offendingPosition;
    }

    public BlockPos getOffendingPosition() {
        return offendingPosition;
    }
}
