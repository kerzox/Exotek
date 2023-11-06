package mod.kerzox.exotek.common.item;

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
