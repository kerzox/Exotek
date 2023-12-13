package mod.kerzox.exotek.common.blockentities.storage;

import mod.kerzox.exotek.client.gui.menu.StorageCrateMenu;
import mod.kerzox.exotek.common.blockentities.CapabilityBlockEntity;
import mod.kerzox.exotek.common.blockentities.MachineBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.capability.item.SidedItemStackHandler;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.common.util.MachineTier;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class StorageCrateBlockEntity extends MachineBlockEntity implements MenuProvider {

    private int DOUBLE_CHEST_SLOTS = 54;
    private SidedItemStackHandler stackHandler = new SidedItemStackHandler(54);
    private MachineTier tiers;

    public StorageCrateBlockEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.STORAGE_CRATE_ENTITY.get(), pos, state);
        addCapabilities(stackHandler);
    }

    public void resizeSlots(MachineTier tiers) {
        switch (tiers) {
            case DEFAULT -> stackHandler.resize(DOUBLE_CHEST_SLOTS);
            case BASIC -> stackHandler.resize(DOUBLE_CHEST_SLOTS * 2);
            case ADVANCED -> stackHandler.resize(DOUBLE_CHEST_SLOTS * 3);
            case SUPERIOR -> stackHandler.resize(DOUBLE_CHEST_SLOTS * 4);
        }
        this.tiers = tiers;
    }

    public MachineTier getTier() {
        return tiers;
    }

    @Override
    protected void write(CompoundTag pTag) {
        pTag.putString("tier", this.tiers.getSerializedName());
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.tiers = MachineTier.valueOf(pTag.getString("tier").toUpperCase());
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Storage Crate");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new StorageCrateMenu(p_39954_, p_39955_, p_39956_, this);
    }

}
