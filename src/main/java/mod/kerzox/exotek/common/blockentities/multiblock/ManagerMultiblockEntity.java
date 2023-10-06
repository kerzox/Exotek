package mod.kerzox.exotek.common.blockentities.multiblock;

import mod.kerzox.exotek.common.blockentities.multiblock.util.MultiblockException;
import mod.kerzox.exotek.common.blockentities.multiblock.validator.MultiblockValidator;
import mod.kerzox.exotek.common.util.IServerTickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

// this is really just added to make things readable
public abstract class ManagerMultiblockEntity extends MultiblockEntity implements MenuProvider, IServerTickable {

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
                        getBlockState().getValue(DirectionalBlock.FACING).getOpposite(),
                        getBlockPos(),
                        getLevel().getBlockState(getBlockPos()), getLevel(), null);
            } catch (MultiblockException e) {
                System.out.println(e);
            }
        }

    }

    public VoxelShape getShape() {
        return Shapes.block();
    }
}
