package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.blockentities.multiblock.manager.AbstractMultiblockManager;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.IManager;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockException;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// this is really just added to make things readable
public abstract class ManagerMultiblockEntity<T extends AbstractMultiblockManager> extends MultiblockEntity implements MenuProvider, IServerTickable {

    public ManagerMultiblockEntity(BlockEntityType<?> pType, IManager manager, BlockPos pPos, BlockState pBlockState) {
        super(pType, pPos, pBlockState);
        setMultiblockManager(manager);
    }

    @Override
    public void tick() {
        if (getMultiblockManager() != null) {
            getMultiblockManager().tickManager();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        syncBlockEntity();
        getMultiblockManager().deserialize(multiblockData);
        if (!level.isClientSide && !multiblockData.isEmpty()) {
            try {
                MultiblockValidator.attemptMultiblockFormation(
                        getMultiblockManager().getBlueprint(),
                        getBlockState().getValue(HorizontalDirectionalBlock.FACING).getOpposite(),
                        getBlockPos(),
                        getLevel().getBlockState(getBlockPos()), getLevel(), null);
            } catch (MultiblockException e) {
                this.getMultiblockManager().disassemble(level, worldPosition);
            }
        }

    }

    @Override
    public AABB getRenderBoundingBox() {
        return getMultiblockManager() != null ? getMultiblockManager().getRenderingBox() != null ? getMultiblockManager().getRenderingBox() :
                super.getRenderBoundingBox() : super.getRenderBoundingBox();
    }

    @Override
    public T getMultiblockManager() {
        return (T) super.getMultiblockManager();
    }

    public VoxelShape getShape() {
        return Shapes.block();
    }

    public VoxelShape getCustomVisualShape() {
        return null;
    }

}
