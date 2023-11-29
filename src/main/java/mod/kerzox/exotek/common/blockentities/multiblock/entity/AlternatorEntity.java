package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.blockentities.multiblock.manager.AlternatorManager;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class AlternatorEntity extends ManagerMultiblockEntity<AlternatorManager> {

    public AlternatorEntity(BlockPos pPos, BlockState pBlockState) {
        super(ExotekRegistry.BlockEntities.ALTERNATOR_ENTITY.get(), new AlternatorManager(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Alternator");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }

}
