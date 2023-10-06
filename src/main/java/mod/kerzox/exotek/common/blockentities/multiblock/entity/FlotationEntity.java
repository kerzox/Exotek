package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.blockentities.multiblock.ManagerMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.FlotationPlantManager;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class FlotationEntity extends ManagerMultiblockEntity {

    public FlotationEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.FROTH_FLOTATION_ENTITY.get(), new FlotationPlantManager(), pPos, pBlockState);
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
