package mod.kerzox.exotek.common.capability.upgrade;

import net.minecraft.nbt.CompoundTag;

public class UpgradeCard {

    private String name;

    public UpgradeCard(String name) {
        this.name = name;
    }

    public UpgradeCard(CompoundTag tag) {
        UpgradeCard card = new UpgradeCard("empty");
        card.read(tag);
    }

    public String getName() {
        return name;
    }

    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("name", this.name);
        return tag;
    }

    public void read(CompoundTag tag) {
        this.name = tag.getString("name");
    }
}
