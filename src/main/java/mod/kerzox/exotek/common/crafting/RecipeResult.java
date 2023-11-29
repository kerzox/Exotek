package mod.kerzox.exotek.common.crafting;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

public class RecipeResult {

    private boolean tag;
    private ResourceLocation location;
    private int count;

    public RecipeResult(ResourceLocation location, int count, boolean tag) {
        this.tag = tag;
        this.count =count;
        this.location = location;
    }

    public static RecipeResult of(ItemStack stack) {
        return new RecipeResult(ForgeRegistries.ITEMS.getKey(stack.getItem()), stack.getCount(), false);
    }

    public static RecipeResult of(TagKey<Item> tag, int count) {
        return new RecipeResult(tag.location(), count, true);
    }

    public boolean isTag() {
        return tag;
    }

    public ResourceLocation getLocation() {
        return location;
    }

    public List<ItemStack> getItems() {
        List<ItemStack> stacks = new ArrayList<>();
        if (!tag) {
            return List.of(new ItemStack(BuiltInRegistries.ITEM.get(location), count));
        }
        for (Item item : ForgeRegistries.ITEMS.tags().getTag(ItemTags.create(location))) {
            stacks.add(new ItemStack(item, count));
        }
        return stacks;
    }

    public int getCount() {
        return this.count;
    }
}
