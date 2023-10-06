package mod.kerzox.exotek.common.blockentities.multiblock.data;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;

import java.util.Arrays;
import java.util.function.Predicate;

public class BlockPredicate implements Predicate<Block> {

    private boolean anchor;
    private boolean unique;
    private BlockPos position;
    private Block[] validBlocks;

    public BlockPredicate(BlockPos pos, boolean anchor, boolean unique, Block... validBlocks) {
        this.position = pos;
        this.validBlocks = validBlocks;
        this.anchor = anchor;
        this.unique = unique;
    }

    public static BlockPredicate anchor(BlockPos pos, Block valid) {
        return new BlockPredicate(pos, true, true, valid);
    }

    public static BlockPredicate[] box(BlockPos max, Block... valid) {
        BlockPredicate[] predicates = new BlockPredicate[max.getX() * max.getY() * max.getZ()];
        int pred = 0;
        for (int i = 0; i < max.getX(); i++) {
            for (int j = 0; j < max.getY(); j++) {
                for (int k = 0; k < max.getZ(); k++) {
                        predicates[pred] = new BlockPredicate(new BlockPos(i, j, k), false, false, valid);
                        pred++;
                    }
                }
            }
        return predicates;
    }

    public BlockPos getPosition() {
        return position;
    }

    public BlockPos rotated(Rotation rotation) {
        return position.rotate(rotation);
    }

    public boolean isAnchor() {
        return anchor;
    }

    public boolean isUnique() {
        return unique;
    }

    public Block[] getValidBlocks() {
        return validBlocks;
    }

    public boolean testAnchorBlock(Block block) {
        return validBlocks[0].equals(block);
    }
    @Override
    public boolean test(Block block) {
        return Arrays.asList(validBlocks).contains(block);
    }
}
