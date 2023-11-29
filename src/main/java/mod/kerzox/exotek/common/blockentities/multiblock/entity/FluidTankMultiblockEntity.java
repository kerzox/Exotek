package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.client.gui.menu.multiblock.FluidTankMultiblockMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.FluidTankManager;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FluidTankMultiblockEntity extends ManagerMultiblockEntity<FluidTankManager> {

    public FluidTankMultiblockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ExotekRegistry.BlockEntities.FLUID_TANK_MULTIBLOCK_ENTITY.get(), new FluidTankManager(), pPos, pBlockState);
    }

    @Override
    public FluidTankManager getMultiblockManager() {
        return (FluidTankManager) super.getMultiblockManager();
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Fluid Tank");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new FluidTankMultiblockMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
