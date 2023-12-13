package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.blockentities.storage.StorageCrateBlockEntity;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.common.blockentities.transport.energy.EnergyCableEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.EnergySubNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import mod.kerzox.exotek.common.util.MachineTier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

public class StorageCrateItem extends ExotekBlockItem {

    private MachineTier tiers;

    public StorageCrateItem(Block Block, Properties p_41383_, MachineTier tiers) {
        super(Block, p_41383_);
        this.tiers = tiers;
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext ctx, BlockState p_40579_) {
        if (ctx.getPlayer().isShiftKeyDown()) return false;
        if (super.placeBlock(ctx, p_40579_)) {
            if (ctx.getLevel().getBlockEntity(ctx.getClickedPos()) instanceof StorageCrateBlockEntity entity) {
                entity.resizeSlots(tiers);
            }
            return true;
        }
        return false;
    }


    public MachineTier getTier() {
        return tiers;
    }

}
