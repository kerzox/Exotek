package mod.kerzox.exotek.common.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonUtils {

    public static String getStringOr(String pKey, JsonObject pJson, String pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsString();
        } else {
            return pDefaultValue;
        }
    }

    public static int getIntOr(String pKey, JsonObject pJson, int pDefaultValue) {
        JsonElement jsonelement = pJson.get(pKey);
        if (jsonelement != null) {
            return jsonelement.isJsonNull() ? pDefaultValue : jsonelement.getAsInt();
        } else {
            return pDefaultValue;
        }
    }

    public static ItemStack deserializeItemStack(JsonObject json) {
        ItemStack resultStack;
        if (json.get("result").isJsonObject()) {
            resultStack = ShapedRecipe.itemStackFromJson(json.get("result").getAsJsonObject());
        } else {
            String s1 = getStringOr("result", json, "");
            ResourceLocation resourcelocation = new ResourceLocation(s1);
            resultStack = new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation));
        }
        return resultStack;
    }

    public static ItemStack[] deserializeItemStacks(JsonObject json) {
        ItemStack[] resultStack = null;

        if (json.has("item_result")) {
            JsonArray arr = json.getAsJsonArray("item_result");
            resultStack = new ItemStack[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                resultStack[i] = net.minecraftforge.common.crafting.CraftingHelper.getItemStack(arr.get(i).getAsJsonObject(), true, false);
            }
        }
        return resultStack;
    }

    public static SizeSpecificIngredient[] deserializeSizeIngredients(JsonObject json) {
        SizeSpecificIngredient[] ingredients = null;
        if (json.has("ingredient")) {
            if (json.get("ingredient").isJsonArray()) {
                JsonArray ingredientArr = json.get("ingredient").getAsJsonArray();
                ingredients = new SizeSpecificIngredient[ingredientArr.size()];
                for (int i = 0; i < ingredientArr.size(); i++) {
                    if (ingredientArr.get(i).isJsonArray()) {
                        JsonArray arr = ingredientArr.get(i).getAsJsonArray();
                        ingredients = new SizeSpecificIngredient[arr.size()];
                        for (int j = 0; j < arr.size(); j++) {
                            ingredients[j] = SizeSpecificIngredient.Serializer.INSTANCE.parse(arr.get(j).getAsJsonObject());
                        }
                    } else {
                        JsonObject ingredient = ingredientArr.get(i).getAsJsonObject();
                        if (ingredient.has("item")) {
                            ingredients[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                        } else {
                            ingredients = new SizeSpecificIngredient[1];
                            ingredients[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient.getAsJsonObject());
                        }
                    }
                }
            } else if (json.get("ingredient").isJsonObject()) {
                JsonObject ingredient = json.get("ingredient").getAsJsonObject();
                if (ingredient.get("item").isJsonArray()) {
                    JsonArray arr = ingredient.getAsJsonArray();
                    ingredients = new SizeSpecificIngredient[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        ingredients[i] = SizeSpecificIngredient.Serializer.INSTANCE.parse(arr.get(i).getAsJsonObject());
                    }
                } else {
                    if (ingredient.has("item")) {
                        ingredients = new SizeSpecificIngredient[1];
                        ingredients[0] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient);
                    } else {
                        ingredients = new SizeSpecificIngredient[1];
                        ingredients[0] = SizeSpecificIngredient.Serializer.INSTANCE.parse(ingredient.getAsJsonObject());
                    }
                }
            }
        }
        return ingredients;
    }


    public static Ingredient[] deserializeIngredients(JsonObject json) {
        Ingredient[] ingredients = null;
        if (json.has("ingredient")) {
            if (json.get("ingredient").isJsonArray()) {
                JsonArray ingredientArr = json.get("ingredient").getAsJsonArray();
                ingredients = new Ingredient[ingredientArr.size()];
                for (int i = 0; i < ingredientArr.size(); i++) {
                    if (ingredientArr.get(i).isJsonArray()) {
                        JsonArray arr = ingredientArr.get(i).getAsJsonArray();
                        ingredients = new Ingredient[arr.size()];
                        for (int j = 0; j < arr.size(); j++) {
                            ingredients[j] = Ingredient.fromJson(arr.get(j));
                        }
                    } else {
                        JsonObject ingredient = ingredientArr.get(i).getAsJsonObject();
                        if (ingredient.has("item")) {
                            ingredients[i] = Ingredient.fromJson(ingredient);
                        } else {
                            ingredients = new Ingredient[1];
                            ingredients[i] = Ingredient.fromJson(ingredient.getAsJsonObject());
                        }
                    }
                }
            } else if (json.get("ingredient").isJsonObject()) {
                JsonObject ingredient = json.get("ingredient").getAsJsonObject();
                if (ingredient.has("item")) {
                    if (ingredient.get("item").isJsonArray()) {
                        JsonArray arr = ingredient.getAsJsonArray();
                        ingredients = new Ingredient[arr.size()];
                        for (int i = 0; i < arr.size(); i++) {
                            ingredients[i] = Ingredient.fromJson(arr.get(i));
                        }
                    } else {
                        if (ingredient.has("item")) {
                            ingredients = new Ingredient[1];
                            ingredients[0] = Ingredient.fromJson(ingredient);
                        }
                    }
                } else {
                    ingredients = new Ingredient[1];
                    ingredients[0] = Ingredient.fromJson(ingredient.getAsJsonObject());
                }
            }
        }
        return ingredients;
    }

    public static FluidIngredient[] deserializeFluidIngredients(JsonObject json) {
        FluidIngredient[] ingredients = null;
        if (json.has("fluid_ingredient")) {
            if (json.get("fluid_ingredient").isJsonArray()) {
                JsonArray ingredientArr = json.get("fluid_ingredient").getAsJsonArray();
                for (JsonElement element : ingredientArr) {
                    if (element.isJsonArray()) {
                        JsonArray arr = element.getAsJsonArray();
                        ingredients = new FluidIngredient[arr.size()];
                        for (int i = 0; i < arr.size(); i++) {
                            ingredients[i] = FluidIngredient.of(arr.get(i).getAsJsonObject());
                        }
                    } else {
                        JsonObject ingredient = element.getAsJsonObject();
                        if (ingredient.has("item")) {
                            ingredients = new FluidIngredient[1];
                            ingredients[0] = FluidIngredient.of(ingredient);
                        } else {
                            ingredients = new FluidIngredient[1];
                            ingredients[0] = FluidIngredient.of(ingredient.getAsJsonObject());
                        }
                    }
                }
            } else if (json.get("fluid_ingredient").isJsonObject()) {
                JsonObject ingredient = json.get("fluid_ingredient").getAsJsonObject();
                ingredients = new FluidIngredient[1];
                ingredients[0] = FluidIngredient.of(ingredient);
            }
        }
        return ingredients;
    }

    public static FluidStack deserializeFluidStack(JsonObject json) {
        FluidStack resultStack = FluidStack.EMPTY;
        if (json.has("result")) {
            JsonObject result = json.getAsJsonObject("result");
            ResourceLocation fluid = new ResourceLocation(getStringOr("fluid", result, ""));
            int amount = getIntOr("amount", result, 0);
            resultStack = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid), amount);
        }
        return resultStack;
    }

    public static FluidStack[] deserializeFluidStacks(int resultCount, JsonObject json) {
        FluidStack[] resultStack = new FluidStack[resultCount];
        if (json.has("fluid_result")) {
            JsonArray results = json.getAsJsonArray("fluid_result");
            resultStack = new FluidStack[results.size()];
            for (int i = 0; i < results.size(); i++) {
                JsonObject re = results.get(i).getAsJsonObject();
                ResourceLocation fluid = new ResourceLocation(getStringOr("fluid", re, ""));
                int amount = getIntOr("amount", re, 0);
                resultStack[i] = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid), amount);
            }
        } else {
            for (int i = 1; i <= resultCount; i++) {
                JsonObject result = json.getAsJsonObject("result" + i);
                ResourceLocation fluid = new ResourceLocation(getStringOr("fluid", result, ""));
                int amount = getIntOr("amount", result, 0);
                resultStack[i - 1] = new FluidStack(ForgeRegistries.FLUIDS.getValue(fluid), amount);
            }
        }
        return resultStack;
    }

    public static JsonObject serializeItemStack(ItemStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
        if (stack.getCount() != 0) {
            json.addProperty("count", stack.getCount());
        }
        return json;
    }

    public static JsonObject serializeFluidStack(FluidStack stack) {
        JsonObject json = new JsonObject();
        json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
        if (stack.getAmount() != 0) {
            json.addProperty("amount", stack.getAmount());
        }
        return json;
    }

}
