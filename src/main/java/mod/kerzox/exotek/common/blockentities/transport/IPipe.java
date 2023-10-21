package mod.kerzox.exotek.common.blockentities.transport;

import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

public interface IPipe<T> {
    Map<Direction, LazyOptional<T>> getPipeConnectable();
    void connectTo(Direction direction, BlockEntity connectableCapability);
    void disconnectFrom(Direction direction);
    void setNetwork(PipeNetwork<T> network);
    PipeNetwork<T> getNetwork();
    BasicBlockEntity getBE();
    PipeNetwork<T> createNetwork();
    CapabilityTiers getTier();
    void update();
}
