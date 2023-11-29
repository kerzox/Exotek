package mod.kerzox.exotek.common.block;

import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ScaffoldBlock extends BasicBlock {

    private static final VoxelShape COLLISION_SHAPE = box(1, 0, 1, 15, 16, 15);

    public static final EnumProperty<Types> SCAFFOLD_TYPE = EnumProperty.create("scaffold_type", Types.class);

    public enum Types implements StringRepresentable {
        DEFAULT("default"),
        WOODEN_TOP("wooden_top");

        String name;

        Types(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name.toLowerCase();
        }

    }

    public ScaffoldBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pHand == InteractionHand.MAIN_HAND && pPlayer.getItemInHand(pHand).getItem() == ExotekRegistry.Items.SOFT_MALLET_ITEM.get() && !pLevel.isClientSide) {
            if (pState.getValue(SCAFFOLD_TYPE) == Types.DEFAULT) {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(SCAFFOLD_TYPE, Types.WOODEN_TOP));
            } else {
                pLevel.setBlockAndUpdate(pPos, pState.setValue(SCAFFOLD_TYPE, Types.DEFAULT));
            }
        }
        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(SCAFFOLD_TYPE);
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return super.getStateForPlacement(pContext).setValue(SCAFFOLD_TYPE, Types.DEFAULT);
    }


}
