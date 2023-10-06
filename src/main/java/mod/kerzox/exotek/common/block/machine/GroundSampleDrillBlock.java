package mod.kerzox.exotek.common.block.machine;

import mod.kerzox.exotek.common.block.BasicEntityBlock;
import mod.kerzox.exotek.common.blockentities.machine.GroundSampleDrillEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class GroundSampleDrillBlock extends MachineEntityBlock<GroundSampleDrillEntity> {

    public GroundSampleDrillBlock(Properties properties) {
        super(Registry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.getType(), properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }
}
