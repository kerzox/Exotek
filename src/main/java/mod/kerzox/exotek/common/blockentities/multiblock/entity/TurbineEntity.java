package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.client.gui.menu.TurbineMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.TurbineManager;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class TurbineEntity extends ManagerMultiblockEntity<TurbineManager> {

    public TurbineEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.TURBINE_ENTITY.get(), new TurbineManager(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Turbine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new TurbineMenu(p_39954_, p_39955_, p_39956_, this);
    }



}
