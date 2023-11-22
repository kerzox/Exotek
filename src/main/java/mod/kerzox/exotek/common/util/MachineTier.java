package mod.kerzox.exotek.common.util;

import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum MachineTier implements StringRepresentable {
    DEFAULT("default", 1),
    BASIC("basic", 3),
    ADVANCED("advanced", 5),
    SUPERIOR("superior", 7);

    private String name;
    private int slots;

    MachineTier(String name, int slots) {
        this.name = name;
        this.slots = slots;
    }

    public int getSlots() {
        return slots;
    }

    @Override
    public String getSerializedName() {
        return toString().toLowerCase();
    }


}
