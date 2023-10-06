package mod.kerzox.exotek.common.blockentities.storage;

import mod.kerzox.exotek.common.blockentities.ContainerisedBlockEntity;
import mod.kerzox.exotek.common.capability.fluid.SidedSingleFluidTank;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicReference;

public class FluidTankSingleEntity extends ContainerisedBlockEntity implements IServerTickable {

    private SidedSingleFluidTank fluidTank = new SidedSingleFluidTank(32000);

    public FluidTankSingleEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.FLUID_TANK_ENTITY.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.FLUID_HANDLER.orEmpty(cap, this.fluidTank.getHandler(side));
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Fluid tank");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        pPlayer.sendSystemMessage(Component.literal("Fluid Amount: " + this.fluidTank.getFluidAmount()));
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.fluidTank.invalidate();
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.put("fluidHandler", this.fluidTank.serialize());
    }

    @Override
    protected void read(CompoundTag pTag) {
       this.fluidTank.deserialize(pTag.getCompound("fluidHandler"));
    }

    @Override
    public void tick() {
       if (!fluidTank.getFluid().isEmpty()) {
           for (Direction direction : Direction.values()) {
               BlockEntity be = level.getBlockEntity(worldPosition.relative(direction));
               if (be != null) {
                   AtomicReference<Integer> amount = new AtomicReference<>();
                   be.getCapability(ForgeCapabilities.FLUID_HANDLER, direction.getOpposite()).ifPresent(cap -> {
                       amount.set(cap.fill(fluidTank.getFluid(), IFluidHandler.FluidAction.SIMULATE));
                       if (amount.get() != 0) {
                           FluidStack drained = this.fluidTank.forceDrain(amount.get(), IFluidHandler.FluidAction.EXECUTE);
                           cap.fill(drained, IFluidHandler.FluidAction.EXECUTE);
                       }
                   });
               }
           }
       }
    }
}
