package mod.kerzox.exotek.common.fluid;

import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class ExotekFluidBlock extends LiquidBlock {

    public ExotekFluidBlock(FlowingFluid p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
    }

    public ExotekFluidBlock(Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
    }
}
