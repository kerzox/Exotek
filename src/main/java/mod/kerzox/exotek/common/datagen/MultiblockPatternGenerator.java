package mod.kerzox.exotek.common.datagen;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.block.multiblock.MultiblockBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.data.BlockPredicate;
import mod.kerzox.exotek.common.blockentities.multiblock.data.MultiblockPattern;
import mod.kerzox.exotek.common.blockentities.multiblock.data.MultiblockStructure;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.BufferedWriter;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MultiblockPatternGenerator {

    public static final Path MULTIBLOCK_DIR = FMLPaths.getOrCreateGameRelativePath(FMLPaths.GAMEDIR.get().resolve(Exotek.MODID + "/structures"));
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

    public static final int id = 0;

    public static void init() {

//        MultiblockStructure debugStruct = new MultiblockStructure(3, 3, 3);
//        BlockPredicate[] debugPredicates = BlockPredicate.box(new BlockPos(debugStruct.length(), debugStruct.height(), debugStruct.width()), Blocks.GOLD_BLOCK);
//
//        for (int i = 0; i < debugPredicates.length; i++) {
//            if (debugPredicates[i].getPosition().equals(new BlockPos(0, 0, 1))) {
//                debugPredicates[i] = new BlockPredicate(new BlockPos(0, 0, 1), true, true, Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK);
//            }
//            else if (debugPredicates[i].getPosition().equals(new BlockPos(0, 0, 2))) {
//                debugPredicates[i] = new BlockPredicate(new BlockPos(0, 0, 2), true, true, Blocks.DIAMOND_BLOCK, Blocks.GOLD_BLOCK);
//            }
//        }
//
//        createPattern(true, "pump_jack", Registry.Blocks.PUMP_JACK_MANAGER_BLOCK.get(), false, debugStruct, debugPredicates);

    }

    public static void saveToStructureFile(Level level, Direction facing, BlockPos[] positions, BlockPos min, BlockPos max) {
        System.out.println("Saving to file");
        MultiblockStructure structure = new MultiblockStructure(max.getX() - min.getX(), max.getZ() - min.getZ(), max.getY() - min.getY());
        List<BlockPredicate> predicates = new ArrayList<>();
        List<Block> blocks = new ArrayList<>();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int y = min.getY(); y <= max.getY(); y++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockPos worldPos = new BlockPos(x - min.getX(), y - min.getY(), z - min.getZ());
                    Block block = level.getBlockState(pos).getBlock();
                    predicates.add(new BlockPredicate(worldPos.rotate(Rotation.CLOCKWISE_90), false, false, block));
                }
            }
        }
//        for (int x = 0; x <= structure.length(); x++) {
//            for (int y = 0; y <= structure.height(); y++) {
//                for (int z = 0; z <= structure.width(); z++) {
//                    BlockPos pos = new BlockPos(x, y, z);
//                    //new BlockPos(x - min.getX(), y - min.getY(), z - min.getZ())
//                    BlockPos dir = positions[1];
//                    BlockPos worldPos = dir.offset(x, y, z);
//                    Block block = level.getBlockState(worldPos).getBlock();
//                    predicates.add(new BlockPredicate(pos, false, false, block));
//                }
//            }
//        }
        createPattern(false, "generated_multiblock_structure_" + id, Registry.Blocks.MULTIBLOCK_INVISIBLE_BLOCK.get(), false, structure, predicates.toArray(BlockPredicate[]::new));
    }

    public static void createPattern(boolean replace, String name, MultiblockBlock block, boolean xzAgnostic, MultiblockStructure structure, BlockPredicate... predicates) {
        if (!replace && new File(MULTIBLOCK_DIR.normalize().toString()).isFile()) {
            return;
        }

        MultiblockPattern pattern = MultiblockPattern.of(name, block, xzAgnostic, structure, predicates);
        String s = GSON.toJson(pattern.toJson());
        try (BufferedWriter bufferedwriter = Files.newBufferedWriter(MULTIBLOCK_DIR.resolve(name+".json"))) {
            bufferedwriter.write(s);
        } catch (Exception ignored) {

        }
    }

}
