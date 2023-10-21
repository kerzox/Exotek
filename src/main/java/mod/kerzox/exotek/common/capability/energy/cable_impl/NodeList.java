package mod.kerzox.exotek.common.capability.energy.cable_impl;

import net.minecraft.core.BlockPos;

import java.util.HashSet;
import java.util.logging.Level;

public class NodeList {

    private HashSet<LevelNode> nodes = new HashSet<>();

    public void removeNodeByPosition(BlockPos pos) {
        this.nodes.removeIf(n -> n.getWorldPosition().equals(pos));
    }

    public void removeNode(LevelNode pos) {
        this.nodes.remove(pos);
    }

    public void addNode(LevelNode node) {
        if (hasPosition(node.getWorldPosition())) return; // don't add another node
        this.nodes.add(node);
    }

    public void addByPosition(BlockPos pos) {
        if (hasPosition(pos)) return; // don't add another node
        this.nodes.add(new LevelNode(pos));
    }

    public boolean hasPosition(BlockPos pos) {
        for (LevelNode node : this.nodes) {
            if (node.getWorldPosition().equals(pos)) return true;
        }
        return false;
    }

    public int size() {
        return this.nodes.size();
    }

    public HashSet<LevelNode> getNodes() {
        return nodes;
    }

    public LevelNode getByPos(BlockPos pos) {
        for (LevelNode node : this.nodes) {
            if (node.getWorldPosition().equals(pos)) return node;
        }
        return null;
    }
}
