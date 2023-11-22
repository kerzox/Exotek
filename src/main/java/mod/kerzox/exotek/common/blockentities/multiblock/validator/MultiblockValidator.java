package mod.kerzox.exotek.common.blockentities.multiblock.validator;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.ManagerMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.entity.MultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.data.BlockPredicate;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.data.MultiblockPattern;
import mod.kerzox.exotek.common.datagen.MultiblockPatternGenerator;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.io.*;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockValidator {

    private static final Map<String, IBlueprint> MULTIBLOCK_BLUEPRINTS = new HashMap<>();

    public static void attemptMultiblockFormation(IBlueprint blueprint,
                                                  Direction facing,
                                                  BlockPos clickedPos,
                                                  BlockState blockState,
                                                  Level level,
                                                  Player player) throws MultiblockException {

        Map<BlockPos, BlockState> blocks = new HashMap<>();
        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;

        MultiblockPattern pattern = blueprint.getPattern();

        for (BlockPredicate predicate : pattern.getPredicates()) {
            BlockPos pos = predicate.getPosition();
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();

        }

        BlockPos minimum = new BlockPos(minX, minY, minZ);
        BlockPos maximum = new BlockPos(maxX, maxY, maxZ);

        int anchorChecks = pattern.getAnchorPredicates().size();

        BlockPos translatePos = clickedPos;

        validatorLoop:
        for (BlockPredicate anchorPredicate : pattern.getAnchorPredicates()) {

            blocks.clear();

            translatePos = clickedPos.subtract(rotatePositions(facing, anchorPredicate.getPosition()));

            // check for corner pieces

//            if (!anchorPredicate.getPosition().equals(minimum) &&
//                    !anchorPredicate.getPosition().equals(new BlockPos(pattern.getStructure().length() - 1, 0, 0)) &&
//                    !anchorPredicate.getPosition().equals(new BlockPos(0, 0, pattern.getStructure().width() - 1))) {
//
//            }

            for (int x = minimum.getX(); x <= maximum.getX(); x++) {
                for (int y = minimum.getY(); y <= maximum.getY(); y++) {
                    for (int z = minimum.getZ(); z <= maximum.getZ(); z++) {

                        BlockPos currentPositionInStructure = new BlockPos(x, y, z);
                        BlockPos perspectivePosition = rotatePositions(facing, currentPositionInStructure).offset(translatePos);
                        BlockPredicate predicate = pattern.getPredicateFromPos(currentPositionInStructure);
                        BlockState block = level.getBlockState(perspectivePosition);

                        if (level.getBlockEntity(perspectivePosition) instanceof MultiblockEntity tileBlockEntity) {
                            block = tileBlockEntity.getMimicState();
                        }

                        //  level.setBlockAndUpdate(perspectivePosition, predicate.getValidBlocks()[0].defaultBlockState());

                        if (player != null && player.isCreative() && player.isShiftKeyDown()) {
                            level.setBlockAndUpdate(perspectivePosition, predicate.getValidBlocks()[0].defaultBlockState());
                        } else {
                            if (!predicate.test(block.getBlock())) {
                                anchorChecks--;
                                if (anchorChecks <= 0) {
                                    fail(predicate, perspectivePosition, block.getBlock());
                                }
                                continue validatorLoop;
                            }

//                        if (predicate.getValidBlocks()[0] == Blocks.AIR) continue validatorLoop;

                            if (blocks.containsValue(block) && predicate.isUnique() && !(predicate.isAnchor())) {
                                throw new MultiblockException("Structure has too many " + block.getBlock().getDescriptionId(), perspectivePosition);
                            }
                        }

                        blocks.put(perspectivePosition, block);
                    }
                }
            }

//            int anchorCount = 0;
//
//            for (BlockState state : blocks.values()) {
//                if (anchorPredicate.testAnchorBlock(state.getBlock())) anchorCount++;
//            }
//
//            if (anchorCount > 1) throw new MultiblockException("Structure has too many anchor blocks");

        }

        if (player != null && player.isCreative() && player.isShiftKeyDown()) {
            return;
        }

        if (!pattern.canForm(level, blocks)) throw new MultiblockException("Multiblock failed to form, check exceptions for details.", null);

        // if brain doesn't exist make one.
        if (!(level.getBlockEntity(clickedPos) instanceof ManagerMultiblockEntity brainBlockEntity) && !level.isClientSide) {
            level.setBlockAndUpdate(clickedPos, pattern.getManagingBlock().defaultBlockState().setValue(HorizontalDirectionalBlock.FACING, facing.getOpposite()));
        }

        BlockEntity be = level.getBlockEntity(clickedPos);

        if (be instanceof ManagerMultiblockEntity brainBlockEntity) {
            brainBlockEntity.getMultiblockManager().setManagingBlockEntity(brainBlockEntity);
            brainBlockEntity.getMultiblockManager().build(facing, level, blocks, pattern);
        }

        if (player != null) player.sendSystemMessage(Component.literal("Multiblock has valid structure"));

    }

    private static void fail(BlockPredicate predicate, BlockPos pos5, Block block) throws MultiblockException {
        String str = "";
        for (Block validBlock : predicate.getValidBlocks()) {
            str += str + " or " + validBlock.getDescriptionId();
        }
        throw new MultiblockException("Invalid block at: " + pos5.toShortString() + "\nFound " + block.getDescriptionId() + " it should be " + str, pos5);
    }

    public static BlockPos rotatePositions(Direction facing, BlockPos pos5) {
        if (facing == Direction.EAST) {

        } else if (facing == Direction.SOUTH) {
            pos5 = pos5.rotate(Rotation.CLOCKWISE_90);
        } else if (facing == Direction.WEST) {
            pos5 = pos5.rotate(Rotation.CLOCKWISE_180);
        } else if (facing == Direction.NORTH) {
            pos5 = pos5.rotate(Rotation.COUNTERCLOCKWISE_90);
        }
        return pos5;
    }


    public static boolean blueprintExists(String name) {
        return MULTIBLOCK_BLUEPRINTS.containsKey(name);
    }

    public static void loadStructureFiles() {
        try {
            System.out.println("Loading Multiblock Patterns!");
            MULTIBLOCK_BLUEPRINTS.clear();
            Files.walk(MultiblockPatternGenerator.MULTIBLOCK_DIR).forEach(path -> {
                parseJson(path.toFile());
            });
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void parseJson(File file) {
        try {
            if (!file.isDirectory()) {
                BufferedReader readIn = new BufferedReader(new InputStreamReader((new FileInputStream(file))));
                MultiblockPattern pattern = MultiblockPattern.fromJson(GsonHelper.parse(readIn));
                MULTIBLOCK_BLUEPRINTS.put(pattern.getName(), new Blueprint(pattern));
                System.out.println("Loaded: " + pattern.getName());
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public static Map<String, IBlueprint> getBlueprints() {
        return MULTIBLOCK_BLUEPRINTS;
    }

    public static IBlueprint getBlueprint(String name) {
        return MULTIBLOCK_BLUEPRINTS.get(name);
    }

    public static BlockPos[] getMinAndMax(List<BlockPos> posList) {
        int minX = 0, minY = 0, minZ = 0, maxX = 0, maxY = 0, maxZ = 0;

        for (BlockPos pos : posList) {
            if (pos.getX() < minX) minX = pos.getX();
            if (pos.getY() < minY) minY = pos.getY();
            if (pos.getZ() < minZ) minZ = pos.getZ();
            if (pos.getX() > maxX) maxX = pos.getX();
            if (pos.getY() > maxY) maxY = pos.getY();
            if (pos.getZ() > maxZ) maxZ = pos.getZ();
        }

        BlockPos minimum = new BlockPos(minX, minY, minZ);
        BlockPos maximum = new BlockPos(maxX, maxY, maxZ);

        return new BlockPos[] {minimum, maximum};
    }


}
