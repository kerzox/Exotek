package mod.kerzox.exotek.common.blockentities.transport;

import net.minecraft.util.StringRepresentable;

public enum IOTypes implements StringRepresentable {

    DEFAULT("default"),
    PUSH("push"),
    EXTRACT("extract"),
    NONE("none");

    String name;

    IOTypes(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String getSerializedName() {
        return getName().toLowerCase();
    }

}
