package mod.kerzox.exotek.registry.holders;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.Supplier;

public class BlockHolder {

    private final Supplier<Block> block;
    private String name;
    private int tint;

    public BlockHolder(String name, int tint, Function<BlockBehaviour.Properties, Block> block, BlockBehaviour.Properties prop) {
        this.name = name;
        this.tint = tint;
        this.block = () -> block.apply(prop);
    }

    public BlockHolder(String name, int tint, Supplier<Block> blockSupplier) {
        this.name = name;
        this.tint = tint;
        this.block = blockSupplier;
    }

    public int getTint() {
        return tint;
    }

    public String getName() {
        return name;
    }

    public Supplier<Block> getBlock() {
        return block;
    }
}
