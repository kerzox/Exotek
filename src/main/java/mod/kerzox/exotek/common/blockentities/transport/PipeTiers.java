package mod.kerzox.exotek.common.blockentities.transport;

import net.minecraft.util.StringRepresentable;

public enum PipeTiers implements StringRepresentable {

    BASIC("basic", 250),
    ADVANCED("advanced", 500),
    HYPER("hyper", 1000);

    String name;
    int transfer;

    PipeTiers(String name, int transfer) {
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
