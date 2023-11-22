package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.client.gui.menu.IndustrialBlastFurnaceMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.IndustrialBlastFurnaceManager;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class IndustrialBlastFurnaceEntity extends ManagerMultiblockEntity<IndustrialBlastFurnaceManager> {

    public IndustrialBlastFurnaceEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.INDUSTRIAL_BLAST_FURNACE_ENTITY.get(), new IndustrialBlastFurnaceManager(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Industrial Blast Furnace");
    }


    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new IndustrialBlastFurnaceMenu(p_39954_, p_39955_, p_39956_, this);
    }

}
