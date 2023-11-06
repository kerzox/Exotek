package mod.kerzox.exotek.common.block.transport;

import mod.kerzox.exotek.common.block.BasicBlock;
import mod.kerzox.exotek.common.block.ExotekBlock;
import mod.kerzox.exotek.common.blockentities.transport.IPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class DirectionConnectableBlock extends BasicBlock {

    public enum Connection implements StringRepresentable {
        NONE("none"),
        CONNECTED("connected");

        private final String name;
        Connection(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        @Override
        public String getSerializedName() {
            return getName();
        }
    }

    public DirectionConnectableBlock(Properties p_49795_) {
        super(p_49795_);
    }


    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.INVISIBLE;
    }

}
