package mod.kerzox.exotek.common.block;

import mod.kerzox.exotek.common.blockentities.WorkstationEntity;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.Nullable;

public class WorkstationBlock extends BasicEntityBlock {

    public WorkstationBlock(Properties p_49795_) {
        super(p_49795_);
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, LivingEntity pPlacer, ItemStack pStack) {
//        Direction facing = pState.getValue(DirectionalBlock.FACING);
//        if (facing == Direction.NORTH) {
//            pLevel.setBlock(pPos.east(), Registry.Blocks.WORKSTATION_PART_BLOCK.get().defaultBlockState(), 3);
//        }

    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
        BlockState state = super.getStateForPlacement(pContext);
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        if (facing == Direction.NORTH) {
            BlockState workstation_part = ExotekRegistry.Blocks.WORKSTATION_PART_BLOCK.get().getStateForPlacement(pContext);
            if (pContext.getLevel().getBlockState(pContext.getClickedPos().east()).getBlock() instanceof AirBlock) {
                pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos().east(), workstation_part);
            } else {
                return null;
            }
        }
        else if (facing == Direction.EAST) {
            BlockState workstation_part = ExotekRegistry.Blocks.WORKSTATION_PART_BLOCK.get().getStateForPlacement(pContext);
            if (pContext.getLevel().getBlockState(pContext.getClickedPos().south()).getBlock() instanceof AirBlock) {
                pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos().south(), workstation_part);
            } else {
                return null;
            }
        }
        else if (facing == Direction.SOUTH) {
            BlockState workstation_part = ExotekRegistry.Blocks.WORKSTATION_PART_BLOCK.get().getStateForPlacement(pContext);
            if (pContext.getLevel().getBlockState(pContext.getClickedPos().west()).getBlock() instanceof AirBlock) {
                pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos().west(), workstation_part);
            } else {
                return null;
            }
        }
        else if (facing == Direction.WEST) {
            BlockState workstation_part = ExotekRegistry.Blocks.WORKSTATION_PART_BLOCK.get().getStateForPlacement(pContext);
            if (pContext.getLevel().getBlockState(pContext.getClickedPos().north()).getBlock() instanceof AirBlock) {
                pContext.getLevel().setBlockAndUpdate(pContext.getClickedPos().north(), workstation_part);
            } else {
                return null;
            }
        }
        return state;
    }

    @Override
    public BlockState updateShape(BlockState p_60541_, Direction p_60542_, BlockState p_60543_, LevelAccessor p_60544_, BlockPos p_60545_, BlockPos p_60546_) {
        if (!hasConnection(p_60544_, p_60545_, p_60541_) && !p_60544_.isClientSide()) {
            p_60544_.destroyBlock(p_60545_, true);
        }
        return super.updateShape(p_60541_, p_60542_, p_60543_, p_60544_, p_60545_, p_60546_);
    }

    public boolean hasConnection(LevelAccessor level, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
        if (facing == Direction.NORTH) {
            if (level.getBlockState(pos.east()).getBlock() instanceof WorkstationBlock.WorkstationPart workstationBlock) {
                return true;
            }
        }
        else if (facing == Direction.EAST) {
            if (level.getBlockState(pos.south()).getBlock() instanceof WorkstationBlock.WorkstationPart workstationBlock) {
                return true;
            }
        }
        else if (facing == Direction.SOUTH) {
            if (level.getBlockState(pos.west()).getBlock() instanceof WorkstationBlock.WorkstationPart workstationBlock) {
                return true;
            }
        }
        else  if (facing == Direction.WEST) {
            if (level.getBlockState(pos.north()).getBlock() instanceof WorkstationBlock.WorkstationPart workstationBlock) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onPlace(BlockState p_60566_, Level p_60567_, BlockPos p_60568_, BlockState p_60569_, boolean p_60570_) {
        super.onPlace(p_60566_, p_60567_, p_60568_, p_60569_, p_60570_);
    }

    @Override
    public void onRemove(BlockState p_60515_, Level p_60516_, BlockPos p_60517_, BlockState p_60518_, boolean p_60519_) {
        super.onRemove(p_60515_, p_60516_, p_60517_, p_60518_, p_60519_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new WorkstationEntity(p_153215_, p_153216_);
    }



    public static class WorkstationPart extends ExotekBlock {

        public static final BooleanProperty NORTH = BooleanProperty.create("north");
        public static final BooleanProperty SOUTH = BooleanProperty.create("south");
        public static final BooleanProperty WEST = BooleanProperty.create("west");
        public static final BooleanProperty EAST = BooleanProperty.create("east");
        public static final BooleanProperty UP = BooleanProperty.create("up");
        public static final BooleanProperty DOWN = BooleanProperty.create("down");

        public WorkstationPart(Properties p_49795_) {
            super(p_49795_);
            this.registerDefaultState(
                    this.stateDefinition.any()
                            .setValue(NORTH, false)
                            .setValue(EAST, false)
                            .setValue(SOUTH, false)
                            .setValue(WEST, false)
                            .setValue(UP, false)
                            .setValue(DOWN, false)
            );
        }

        @Override
        public RenderShape getRenderShape(BlockState p_60550_) {
            return RenderShape.INVISIBLE;
        }

        @Override
        public boolean canSurvive(BlockState p_60525_, LevelReader p_60526_, BlockPos p_60527_) {
            return hasConnection(p_60525_);
        }

        @Override
        protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
            pBuilder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
            super.createBlockStateDefinition(pBuilder);
        }

        @Override
        public BlockState updateShape(BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos) {
            return checkConnection(pLevel, pCurrentPos, pState);
        }

        @Override
        public @Nullable BlockState getStateForPlacement(BlockPlaceContext pContext) {
            BlockState state = super.getStateForPlacement(pContext);
            return checkConnection(pContext.getLevel(), pContext.getClickedPos(), state);
        }

        public BlockState checkConnection(LevelAccessor level, BlockPos pos, BlockPos pNeighborPos, BlockState state) {
            BlockState state1 = level.getBlockState(pNeighborPos);
            if (state1.getBlock() instanceof WorkstationBlock workstationBlock) {
                Direction facing = state1.getValue(HorizontalDirectionalBlock.FACING);
                if (facing == Direction.NORTH) {
                    if (level.getBlockState(pos.west()).getBlock() instanceof WorkstationBlock workstationBlock1) {
                        return state.setValue(WEST, true);
                    }
                } else if (facing == Direction.EAST) {
                    if (level.getBlockState(pos.north()).getBlock() instanceof WorkstationBlock workstationBlock1) {
                        return state.setValue(NORTH, true);
                    }
                } else if (facing == Direction.SOUTH) {
                    if (level.getBlockState(pos.east()).getBlock() instanceof WorkstationBlock workstationBlock1) {
                        return state.setValue(EAST, true);
                    }
                } else if (facing == Direction.WEST) {
                    if (level.getBlockState(pos.south()).getBlock() instanceof WorkstationBlock workstationBlock1) {
                        return state.setValue(SOUTH, true);
                    }
                }
            }
            return state;
        }

        public BlockState checkConnection(LevelAccessor level, BlockPos pos, BlockState state) {
            Direction facing = state.getValue(HorizontalDirectionalBlock.FACING);
            if (facing == Direction.NORTH) {
                if (level.getBlockState(pos.west()).getBlock() instanceof WorkstationBlock workstationBlock) {
                    return state.setValue(WEST, true);
                }
            }
            else if (facing == Direction.EAST) {
                if (level.getBlockState(pos.north()).getBlock() instanceof WorkstationBlock workstationBlock) {
                    return state.setValue(NORTH, true);
                }
            }
            else if (facing == Direction.SOUTH) {
                if (level.getBlockState(pos.east()).getBlock() instanceof WorkstationBlock workstationBlock) {
                    return state.setValue(EAST, true);
                }
            }
            else  if (facing == Direction.WEST) {
                if (level.getBlockState(pos.south()).getBlock() instanceof WorkstationBlock workstationBlock) {
                    return state.setValue(SOUTH, true);
                }
            }
            if (!level.isClientSide()) level.destroyBlock(pos, false);
            return state;
        }

//        public BlockState setBlockStateCorrectly(Direction toFace, Direction mainBodyDirection, BlockState state) {
//            return state.setValue()
//        }

        public boolean hasConnection(BlockState state) {
            if (state.getValue(NORTH)) {
                return true;
            }
            else if (state.getValue(EAST)) {
                return true;
            }
            else if (state.getValue(SOUTH)) {
                return true;
            }
            else if (state.getValue(WEST)) {
                return true;
            }
            else if (state.getValue(UP)) {
                return true;
            }
            else return state.getValue(DOWN);
        }

    }

}
