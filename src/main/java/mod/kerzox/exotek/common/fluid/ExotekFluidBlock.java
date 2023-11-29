package mod.kerzox.exotek.common.fluid;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

import java.util.function.Supplier;

public class ExotekFluidBlock extends LiquidBlock {

    public ExotekFluidBlock(FlowingFluid p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
    }

    public ExotekFluidBlock(Supplier<? extends FlowingFluid> p_54694_, Properties p_54695_) {
        super(p_54694_, p_54695_);
    }



    @Override
    public void onPlace(BlockState p_54754_, Level p_54755_, BlockPos p_54756_, BlockState p_54757_, boolean p_54758_) {

    }

}
