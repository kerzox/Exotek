package mod.kerzox.exotek.common.item;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

public class CompressorDieItem extends Item {

    public enum Dies implements StringRepresentable {
        PLATE,
        ROD,
        WIRE,
        GEAR;

        @Override
        public String getSerializedName() {
            return toString().toLowerCase();
        }
    }

    private Dies dye;

    public CompressorDieItem(Dies dye) {
        super(new Item.Properties());
        this.dye = dye;
    }

    public Dies getDye() {
        return dye;
    }
}
