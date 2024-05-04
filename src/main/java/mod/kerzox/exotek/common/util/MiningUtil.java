package mod.kerzox.exotek.common.util;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MiningUtil {

    /**
     * This will scan for all blocks below in the defined radius until you hit voic air
     *
     * @param radius (Radius is x blocks from the world position. (1 is 3x3, 2 (5x5) etc).
     * @return blocks found
     */

    public static List<Pair<BlockPos, BlockState>> scanBlocks(Level level, int radius, BlockPos startingPosition) {
        return scanBlocks(level, radius, Integer.MAX_VALUE, startingPosition);
    }

    /**
     * This will scan for all blocks below in the defined radius until you reach a certain y level
     *
     * @param radius (Radius is x blocks from the world position. (1 is 3x3, 2 (5x5) etc).
     * @return blocks found
     */

    public static List<Pair<BlockPos, BlockState>> scanBlocks(Level level, int radius, int stopAt, BlockPos startingPosition) {
        List<Pair<BlockPos, BlockState>> blocks = new ArrayList<>();
        BlockPos pos = startingPosition;
        int y = pos.getY() - 1;
        while (y != Math.min(stopAt - 1, pos.getY())) {
            for (int x = pos.getX() - radius; x <= pos.getX() + radius; x++) {
                for (int z = pos.getZ() - radius; z <= pos.getZ() + radius; z++) {
                    BlockPos bPos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(bPos);
                    blocks.add(Pair.of(bPos, state));
                    if (state.is(Blocks.VOID_AIR)) return blocks;
                }
            }
            y--;
        }
        return blocks;
    }

    public static List<Pair<BlockPos, BlockState>> filterOres(List<Pair<BlockPos, BlockState>> pairs) {
        return pairs.stream().filter((pair) ->
                pair.getSecond().is(Tags.Blocks.ORES)).collect(Collectors.toList());
    }

    /**
     * TODO add optionals such as fortune and silk touch.
     * Get drops from provided blockState and blockPos
     *
     * @param pos   blocks position in world
     * @param state block state
     * @return drops associated with this block from a netherite pickaxe.
     */

    public static List<ItemStack> getDropsFromState(Level level, BlockPos pos, BlockState state) {

        LootParams.Builder builder = new LootParams.Builder((ServerLevel) level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.NETHERITE_PICKAXE))
                .withOptionalParameter(LootContextParams.BLOCK_ENTITY, level.getBlockEntity(pos));

        return state.getDrops(builder);
    }

}
