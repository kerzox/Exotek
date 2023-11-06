package mod.kerzox.exotek.common.blockentities.transport;

import net.minecraft.util.StringRepresentable;

public enum CapabilityTiers implements StringRepresentable {

    BASIC("basic", 500),
    ADVANCED("advanced", 1000),
    HYPER("hyper", 2500);

    String name;
    int transfer;

    CapabilityTiers(String name, int transfer) {
        this.name = name;
        this.transfer = transfer;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return getName().toLowerCase();
    }

    public int getTransfer() {
        return this.transfer;
    }
}
