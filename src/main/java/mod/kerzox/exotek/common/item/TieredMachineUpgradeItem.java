package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.block.TieredMachineBlock;
import mod.kerzox.exotek.common.util.ITieredMachine;
import mod.kerzox.exotek.common.util.MachineTier;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;

public class TieredMachineUpgradeItem extends MachineUpgradeItem {

    private MachineTier tier = MachineTier.DEFAULT;

    public TieredMachineUpgradeItem(MachineTier tier, Properties p_41383_) {
        super(tier.getSerializedName()+"_upgrade", p_41383_);
        this.tier = tier;
    }

    @Override
    public InteractionResult useOn(UseOnContext p_41427_) {
        BlockState state = p_41427_.getLevel().getBlockState(p_41427_.getClickedPos());
        if (state.getBlock() instanceof TieredMachineBlock) {
            MachineTier blockTier = state.getValue(TieredMachineBlock.TIER);
            if (blockTier.getSlots() < tier.getSlots()) {
                p_41427_.getLevel().setBlockAndUpdate(p_41427_.getClickedPos(), state.setValue(TieredMachineBlock.TIER, tier));
                if (p_41427_.getLevel().getBlockEntity(p_41427_.getClickedPos()) instanceof ITieredMachine machine) machine.onTierChanged(tier);
            }

        }
        return super.useOn(p_41427_);
    }

    public MachineTier getTier() {
        return tier;
    }
}
