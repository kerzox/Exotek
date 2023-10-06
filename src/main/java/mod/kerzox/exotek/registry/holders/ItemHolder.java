package mod.kerzox.exotek.registry.holders;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class ItemHolder {

    private List<Types> addon;
    private String name;
    private int tint;
    private Supplier<Item> item;

    public ItemHolder(String name, int tint, Supplier<Item> item, Types... addons) {
        this.name = name;
        this.tint = tint;
        this.addon = Arrays.stream(addons).toList();
        this.item = item;
    }

    public Supplier<Item> getItem() {
        return item;
    }

    public String getName() {
        return name;
    }

    public int getTint() {
        return tint;
    }

    public List<Types> getAddons() {
        return addon;
    }

    public enum Types implements StringRepresentable {
        BLOCK,
        STONE_ORE,
        DEEPSLATE_ORE,
        SHEET,
        CHUNK,
        NUGGET,
        INGOT,
        PLATE,
        ROD,
        IMPURE_DUST,
        DUST,
        CLUSTER,
        GEAR;

        @Override
        public String getSerializedName() {
            return "_" + toString().toLowerCase();
        }

        public boolean isOre() {
            return this == STONE_ORE || this == DEEPSLATE_ORE;
        }

        public boolean isBlock() {
            return this == STONE_ORE || this == DEEPSLATE_ORE || this == BLOCK || this == SHEET;
        }

    }

}
