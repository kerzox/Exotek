package mod.kerzox.exotek.common.blockentities.transport;

import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

public class GraphUtil {

    public static HashSet<IPipe<?>> DepthFirstSearch(PipeNetwork<?> network, IPipe<?> startingNode) {
        Queue<IPipe<?>> queue = new LinkedList<>();
        HashSet<IPipe<?>> visited = new HashSet<>();
        Level level = startingNode.getBE().getLevel();

        queue.add(startingNode);
        visited.add(startingNode);

        while (!queue.isEmpty()) {

            IPipe<?> current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockEntity blockEntity = level.getBlockEntity(current.getBE().getBlockPos().relative(dir));
                if (blockEntity instanceof IPipe<?> pipe &&
                        !visited.contains(pipe) && network.pipesInNetwork.contains(pipe) && network.isValidPipe(pipe)
                ) {
                    visited.add(pipe);
                    queue.add(pipe);
                }
            }

        }
        return visited;
    }

    public static boolean BreadthFirstSearchCanReach(IPipe<?> startingNode, IPipe<?> endNode, PipeNetwork<?> network) {
        return BreadthFirstSearchWithEndPoint(startingNode, endNode, network).contains(endNode);
    }

    public static HashSet<IPipe<?>> BreadthFirstSearchWithEndPoint(IPipe<?> startingNode, IPipe<?> endNode, PipeNetwork<?> network) {
        Queue<IPipe<?>> queue = new LinkedList<>();
        HashSet<IPipe<?>> visited = new HashSet<>();
        Map<IPipe<?>, IPipe<?>> parents = new HashMap<>();
        HashSet<IPipe<?>> populatedPipe = new HashSet<>();
        Level level = startingNode.getBE().getLevel();


        queue.add(startingNode);
        visited.add(startingNode);

        while(!queue.isEmpty()) {
            IPipe<?> current = queue.poll();
            
            if (current == endNode) break;

            for (Direction direction : Direction.values()) {
                BlockEntity blockEntity = level.getBlockEntity(current.getBE().getBlockPos().relative(direction));

                if (blockEntity instanceof IPipe<?> pipe &&
                    !visited.contains(pipe) && network.pipesInNetwork.contains(pipe) && network.isValidPipe(pipe)
                ) {

                    queue.add(pipe);
                    visited.add(pipe);
                    parents.put(pipe, current);

                }

            }
            
        }

        if (!visited.contains(endNode)) {
            // no valid path
            return new HashSet<>();
        }

        IPipe<?> current = endNode;

        while (current != null) {
            populatedPipe.add(current);
            current = parents.get(current);
        }

        return populatedPipe;

    }

}
