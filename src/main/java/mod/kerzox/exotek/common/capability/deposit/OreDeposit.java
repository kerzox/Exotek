package mod.kerzox.exotek.common.capability.deposit;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.common.util.JsonUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OreDeposit implements IDepositResource {

    protected String name;
    protected float spawnPercentage;
    protected int amount;
    protected List<OreStack> items = new ArrayList<>();
    protected int min, max;

    private OreDeposit(String name, float spawnPercentage, int amount, int min, int max, List<OreStack> itemStacks) {
        this.name = name;
        this.spawnPercentage = spawnPercentage;
        this.amount = amount;
        this.items = itemStacks;
        this.min = min;
        this.max = max;

        for (OreStack item : items) {
            item.setItemStack(new ItemStack(item.getItemStack().getItem(), Math.round(amount * item.getPercentage())));
        }

    }

    public OreDeposit randomizeAmount() {
        return of(this.name, this.spawnPercentage, this.min, this.max, this.items);
    }

    public OreDeposit() {
        this.items = new ArrayList<>();
    }

    public ItemStack mineAt(int index, int amount) {
        ItemStack copy = this.items.get(index).getItemStack().copy();
        return copy;
    }

    public List<OreStack> getItems() {
        return items;
    }

    private JsonObject itemToJson(OreStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.getItemStack().getItem()).toString());
        json.addProperty("percentage", stack.getPercentage());
        if (stack.getItemStack().hasTag())
            json.addProperty("nbt", stack.getItemStack().getTag().toString());
        return json;
    }

    public static OreDeposit fromJson(JsonObject json) {
        String name = JsonUtils.getStringOr("vein_name", json, "invalid name");
        float spawnPercentage = GsonHelper.getAsFloat(json, "spawn_percentage", 0);
        JsonArray array = json.getAsJsonArray("range");
        int min = array.get(0).getAsInt();
        int max = array.get(1).getAsInt();
        JsonArray items1 = json.getAsJsonArray("items");
        List<OreStack> items = new ArrayList<>();
        for (JsonElement jsonElement : items1) {
            JsonObject itemData = jsonElement.getAsJsonObject();
            items.add(new OreStack(CraftingHelper.getItemStack(itemData, true), GsonHelper.getAsFloat(itemData, "percentage")));
        }
        return of(name, spawnPercentage, min, max, items);
    }

    @Override
    public void serialize(CompoundTag tag) {
        tag.putString("name", this.getName());
        tag.putFloat("spawn_percentage", this.spawnPercentage);
        tag.putInt("amount", this.amount);
//        tag.putIntArray("range", new int[] {this.min, this.max});
        ListTag itemList = new ListTag();
        for (OreStack item : getItems()) {
            CompoundTag tag1 = new CompoundTag();
            ResourceLocation resourcelocation = BuiltInRegistries.ITEM.getKey(item.getItemStack().getItem());
            tag1.putString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
            tag1.putInt("count", item.itemStack.getCount());
            tag1.putFloat("percentage", item.getPercentage());
            itemList.add(tag1);

        }
        tag.put("item_list", itemList);
    }

    public int[] getVeinAmounts() {
        int[] amounts = new int[getItems().size()];
        for (int i = 0; i < amounts.length; i++) {
            amounts[i] = this.items.get(i).getItemStack().getCount();
        }
        return amounts;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        this.name = tag.getString("name");
        this.spawnPercentage = tag.getFloat("spawn_percentage");
        this.amount = tag.getInt("amount");
//        int[] range = tag.getIntArray("range");
//        this.min = range[0];
//        this.max = range[1];
        ListTag itemList = tag.getList("item_list", Tag.TAG_COMPOUND);
        for (int i = 0; i < itemList.size(); i++) {
            CompoundTag ore = itemList.getCompound(i);
            Item item = BuiltInRegistries.ITEM.get(new ResourceLocation(ore.getString("id")));
            int count = ore.getInt("count");
            items.add(new OreStack(new ItemStack(item, count), ore.getFloat("percentage")));
        }
    }

    public static OreDeposit of(String name, float spawnPercentage, int min, int max, List<OreStack> stacks) {
        if (name.isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if (spawnPercentage < 0 || spawnPercentage > 1) throw new IllegalArgumentException("Spawn percentage must be between 0 - 1");
        if (stacks.size() == 0) throw new IllegalArgumentException("Items cannot be empty");
        return new OreDeposit(name, spawnPercentage, new Random().nextInt(min, max), min, max, stacks);
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public float getSpawnPercentage() {
        return spawnPercentage;
    }

    public String getName() {
        return this.name;
    }

    public static class OreStack {

        private ItemStack itemStack;
        private float percentage;

        public OreStack(ItemStack stack, float percentage) {
            this.itemStack = stack;
            this.percentage = percentage;
        }

        public ItemStack getItemStack() {
            return itemStack;
        }

        public float getPercentage() {
            return percentage;
        }

        public void setItemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
        }
    }

}
