package mod.kerzox.exotek.common.block;

import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import net.minecraft.world.level.block.Block;

public class EnergyCellBlock extends Block {

    private CapabilityTiers tiers;

    public EnergyCellBlock(Properties p_49795_, CapabilityTiers tiers) {
        super(p_49795_);
        this.tiers = tiers;
    }

    public CapabilityTiers getTiers() {
        return tiers;
    }
}
