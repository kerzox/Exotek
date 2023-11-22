package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.block.TieredMachineBlock;
import mod.kerzox.exotek.common.network.PacketHandler;
import mod.kerzox.exotek.common.util.ITieredMachine;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class MachineUpgradeItem extends ExotekItem {

    private String upgradeName;

    public MachineUpgradeItem(String upgradeName, Properties p_41383_) {
        super(p_41383_);
        this.upgradeName = upgradeName;
    }

    public String getUpgradeName() {
        return upgradeName;
    }
}
