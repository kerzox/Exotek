package mod.kerzox.exotek.common.blockentities.multiblock.entity.dynamic;

import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;

public class BrinePoolEntity extends DynamicMultiblockEntity {

    private Master master = createMaster();

    public BrinePoolEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.BRINE_POOL_ENTITY.get(), pos, state);
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (getMaster() != null && !pLevel.isClientSide && pPlayer.isShiftKeyDown()) {
            pPlayer.sendSystemMessage(Component.literal("Master Interface: " + getMaster()));
            pPlayer.sendSystemMessage(Component.literal("Master Tile: " + getMaster().getTile()));
            pPlayer.sendSystemMessage(Component.literal("Master Tile Position: " + getMaster().getTile().getBlockPos().toShortString()));
            pPlayer.sendSystemMessage(Component.literal("Network Size: " + getMaster().getEntities().size()));
            if(getMultifluidTank() != null) {
                pPlayer.sendSystemMessage(Component.literal("Input tank: " + getMultifluidTank().getFluidInTank(0).getAmount()));
                pPlayer.sendSystemMessage(Component.literal("Output tank: " + getMultifluidTank().getFluidInTank(1).getAmount()));
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public SidedMultifluidTank getMultifluidTank() {

        if (getMaster() == null) return null;

        LazyOptional<IFluidHandler> fluidHandlerLazyOptional = getMaster().getCapability(ForgeCapabilities.FLUID_HANDLER);

        if (fluidHandlerLazyOptional.isPresent()) {
            if (fluidHandlerLazyOptional.resolve().isPresent() && fluidHandlerLazyOptional.resolve().get() instanceof SidedMultifluidTank.PlayerWrapper tank) {
                return tank.getInventory();
            }
        }

        return null;
    }

    public void tick() {
        if (getMaster() != null) {
            getMaster().tick();
        }
    }

    @Override
    public void setMaster(Master master) {
        this.master = master;
    }

    @Override
    public Master getMaster() {
        return master;
    }

    @Override
    public Master createMaster() {
        return new Master(this) {

            public static int capacity = 8000;

            private int duration;
            private int maxDuration;
            private boolean running;
            private ItemStackInventory itemStackHandler = new ItemStackInventory(0, 1);
            private SidedMultifluidTank sidedMultifluidTank = new SidedMultifluidTank(1, capacity, 1, capacity);
            private SidedMultifluidTank prev = sidedMultifluidTank;

            @Override
            protected boolean preAttachment(DynamicMultiblockEntity toAttach) {

                if (toAttach instanceof BrinePoolEntity entity) {
                    SidedMultifluidTank old = entity.getMultifluidTank();

                    if (old != prev && (old != sidedMultifluidTank)) {
                        FluidStack inputStack = old.getInputHandler().getFluidInTank(0);
                        FluidStack outputStack = old.getOutputHandler().getFluidInTank(0);

                        if (!inputStack.isEmpty()) {
                            // check if the fluid is the same as well we don't want to connect to another one that isn't the same
                            if (!inputStack.getFluid().isSame(sidedMultifluidTank.getInputHandler().getFluidInTank(0).getFluid())) {
                                if (!sidedMultifluidTank.getInputHandler().getFluidInTank(0).isEmpty()) return false;
                            }
                            inputStack.shrink(sidedMultifluidTank.getInputHandler().fill(inputStack, IFluidHandler.FluidAction.EXECUTE));
                        }

                        if (!outputStack.isEmpty()) {
                            // check if the fluid is the same as well we don't want to connect to another one that isn't the same
                            if (!outputStack.getFluid().isSame(sidedMultifluidTank.getOutputHandler().getFluidInTank(0).getFluid())) {
                                if (!sidedMultifluidTank.getOutputHandler().getFluidInTank(0).isEmpty()) return false;
                            }
                            outputStack.shrink(sidedMultifluidTank.getOutputHandler().forceFill(outputStack, IFluidHandler.FluidAction.EXECUTE));
                        }

                    }

                    prev = old;

                }

                return true;
            }

            @Override
            protected void onAttachment(DynamicMultiblockEntity attached) {
                if (sidedMultifluidTank != null) {
                    int totalSize = getEntities().size();
                    sidedMultifluidTank.setBothCapacities(totalSize * capacity);
                }
            }

            @Override
            public boolean isValidEntity(DynamicMultiblockEntity entity) {
                return entity instanceof BrinePoolEntity;
            }

            @Override
            protected void onDetachment(DynamicMultiblockEntity detached, HashSet<Master> masters) {
                FluidStack inputReturned = sidedMultifluidTank.getInputHandler().getFluidInTank(0);
                FluidStack outputReturned = sidedMultifluidTank.getOutputHandler().getFluidInTank(0);

                for (Master network : masters) {
                    SidedMultifluidTank newFluidTank = ((BrinePoolEntity)network.getTile()).getMultifluidTank();
                    if (!inputReturned.isEmpty()) inputReturned.shrink(newFluidTank.getInputHandler().fill(inputReturned, IFluidHandler.FluidAction.EXECUTE));
                    if (!outputReturned.isEmpty()) outputReturned.shrink(newFluidTank.getOutputHandler().forceFill(outputReturned, IFluidHandler.FluidAction.EXECUTE));
                }


            }

            @Override
            public void tick() {

                FluidStack inputStack = sidedMultifluidTank.getInputHandler().getFluidInTank(0);

                if (!inputStack.isEmpty()) {
                    if (inputStack.getFluid() == Fluids.WATER) {
                        FluidStack outputStack = new FluidStack(ExotekRegistry.Fluids.BRINE.getFluid().get(), 250);
                        inputStack.shrink(sidedMultifluidTank.getOutputHandler().forceFill(outputStack, IFluidHandler.FluidAction.EXECUTE));
                    }

                }

            }

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, sidedMultifluidTank.getHandler(side));
            }
        };
    }


}
