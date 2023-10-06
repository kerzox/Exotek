package mod.kerzox.exotek.common.blockentities.storage;

import mod.kerzox.exotek.common.blockentities.ContainerisedBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.PipeTiers;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BatteryBufferEntity extends ContainerisedBlockEntity {

    private PipeTiers tier = PipeTiers.BASIC;
    private SidedEnergyHandler handler = new SidedEnergyHandler(32000);

    public BatteryBufferEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ForgeCapabilities.ENERGY.orEmpty(cap, handler.getHandler(side));
    }

    private void updateCapacity() {
        handler.setCapacity(tier.getTransfer());
    }

    public void setTier(PipeTiers tier) {
        this.tier = tier;
        updateCapacity();
    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }
}
