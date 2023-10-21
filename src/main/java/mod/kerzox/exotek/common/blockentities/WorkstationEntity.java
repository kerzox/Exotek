package mod.kerzox.exotek.common.blockentities;

import mod.kerzox.exotek.client.gui.menu.WorkstationMenu;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WorkstationEntity extends ContainerisedBlockEntity {

    public WorkstationEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.WORKSTATION_ENTITY.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new WorkstationMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
