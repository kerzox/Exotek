package mod.kerzox.exotek.common.block.machine;

import mod.kerzox.exotek.common.block.BasicEntityBlock;
import mod.kerzox.exotek.common.util.ICustomCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

public class MachineEntityBlock<T extends BlockEntity> extends BasicEntityBlock {

    protected RegistryObject<BlockEntityType<T>> type;

    public MachineEntityBlock(RegistryObject<BlockEntityType<T>> type, Properties p_49795_) {
        super(p_49795_);
        this.type = type;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        if (level.getBlockEntity(pos) instanceof ICustomCollisionShape hasCustomShape) {
            return hasCustomShape.getShape() != null ? hasCustomShape.getShape() : super.getShape(state, level, pos, ctx);
        }
        return super.getShape(state, level, pos, ctx);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return this.type.get().create(pPos, pState);
    }

}
