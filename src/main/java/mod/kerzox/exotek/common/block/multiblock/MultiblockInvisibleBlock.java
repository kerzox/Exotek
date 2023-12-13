package mod.kerzox.exotek.common.block.multiblock;

import mod.kerzox.exotek.common.blockentities.multiblock.entity.ManagerMultiblockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;

public class MultiblockInvisibleBlock<T extends BlockEntity> extends MultiblockBlock<T> {


    public MultiblockInvisibleBlock(RegistryObject<BlockEntityType<T>> type, Properties pProperties) {
        super(type, pProperties);
    }

    @Override
    public RenderShape getRenderShape(BlockState p_60550_) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getVisualShape(BlockState p_60479_, BlockGetter p_60480_, BlockPos p_60481_, CollisionContext p_60482_) {
        if (p_60480_.getBlockEntity(p_60481_) instanceof ManagerMultiblockEntity manager) {
            if (manager.getCustomVisualShape() != null) {
                return manager.getCustomVisualShape();
            }
        }
        return super.getVisualShape(p_60479_, p_60480_, p_60481_, p_60482_);
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState p_60578_, BlockGetter p_60579_, BlockPos p_60580_) {
        return super.getOcclusionShape(p_60578_, p_60579_, p_60580_);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState p_60572_, BlockGetter p_60573_, BlockPos p_60574_, CollisionContext p_60575_) {
        return super.getCollisionShape(p_60572_, p_60573_, p_60574_, p_60575_);
    }

    @Override
    public VoxelShape getInteractionShape(BlockState p_60547_, BlockGetter p_60548_, BlockPos p_60549_) {
        return super.getInteractionShape(p_60547_, p_60548_, p_60549_);
    }

    @Override
    public VoxelShape getShape(BlockState p_60555_, BlockGetter p_60556_, BlockPos p_60557_, CollisionContext p_60558_) {
        return super.getShape(p_60555_, p_60556_, p_60557_, p_60558_);
    }
}
