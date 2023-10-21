package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.client.gui.menu.CokeOvenMenu;
import mod.kerzox.exotek.client.gui.menu.ExotekBlastFurnaceMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.ManagerMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.BlastFurnaceManager;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.CokeOvenManager;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.PumpjackManager;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CokeOvenEntity extends ManagerMultiblockEntity<CokeOvenManager> {

    public CokeOvenEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.COKE_OVEN_ENTITY.get(), new CokeOvenManager(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Coke Oven");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new CokeOvenMenu(p_39954_, p_39955_, p_39956_, this);
    }

}
