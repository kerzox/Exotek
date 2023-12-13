package mod.kerzox.exotek.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.util.JsonUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

import javax.json.Json;
import java.util.*;

public class PatternRecipe {

    public static boolean hasMatchingRow(int rows, int cols, PatternRecipe.Pattern pattern, RecipeInventoryWrapper inventory) {
        int timesMatched = pattern.ingredients.size();
        for (int row = 0; row < rows; row++) {
            int ingredientIndex = 0;
            timesMatched = pattern.ingredients.size();
            for (int col = 0; col < cols; col++) {
                int index = (row * cols) + col;
                ItemStack inventoryItemStack = inventory.getItem(index);
                if (pattern.ingredients.get(ingredientIndex).test(inventoryItemStack)) {
                    timesMatched--;
                }
                if (timesMatched == 0) return true;
                ingredientIndex++;
            }
        }

        return timesMatched == 0;
    }

    public static boolean hasMatchingShapedRecipe(int rows, int cols, NonNullList<SizeSpecificIngredient> ingredients, RecipeInventoryWrapper inventory) {
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int index = (row * cols) + col;
                ItemStack inventoryItemStack = inventory.getItem(index);
                if (!ingredients.get(index).test(inventoryItemStack)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean hasMatchingShapelessRecipe(int rows, int cols, NonNullList<SizeSpecificIngredient> ingredients, RecipeInventoryWrapper inventory) {

        Map<SizeSpecificIngredient, Boolean> matching = new HashMap<>();
        ingredients.forEach(i -> matching.put(i, false));
        List<Integer> checkedSlot = new ArrayList<>();

        ingredients.forEach(((ingredient) -> {
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                if (ingredient.test(inventory.getItem(i))) {
                    if (!checkedSlot.contains(i)) {
                        matching.put(ingredient, true);
                        checkedSlot.add(i);
                        break;
                    }
                }
            }
        }));

        return !matching.containsValue(false);
    }

    public static class Pattern {

        private boolean matchHorizontal;
        private boolean matchVertical;
        private int rows;
        private int cols;
        private List<String> pattern = new ArrayList<>();
        private Map<Character, SizeSpecificIngredient> predicateMap = new HashMap<>();

        private NonNullList<SizeSpecificIngredient> ingredients;

        public Pattern(int rows, int cols, boolean matchHorizontal, boolean matchVertical) {
            this.rows = rows;
            this.cols = cols;
            this.matchHorizontal = matchHorizontal;
            this.matchVertical = matchVertical;
        }

        public static Pattern of(int rows, int cols, boolean matchHorizontal, boolean matchVertical) {
            return new Pattern(rows, cols, matchHorizontal, matchVertical);
        }

        public Map<Character, SizeSpecificIngredient> getPredicateMap() {
            return predicateMap;
        }

        public NonNullList<SizeSpecificIngredient> getIngredientsFromMap() {
            NonNullList<SizeSpecificIngredient> list = NonNullList.create();
            list.addAll(this.predicateMap.values());
            return list;
        }

        public NonNullList<SizeSpecificIngredient> getIngredients() {
            return ingredients;
        }

        public static Pattern fromNetwork(FriendlyByteBuf buf) {
            Pattern pattern = new Pattern(buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());
            pattern.ingredients = NonNullList.create();
            SizeSpecificIngredient[] ingredients = new SizeSpecificIngredient[buf.readVarInt()];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = (SizeSpecificIngredient) Ingredient.fromNetwork(buf);
            }
            pattern.ingredients.addAll(Arrays.asList(ingredients));
            return pattern;
        }

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeInt(this.rows);
            buf.writeInt(this.cols);
            buf.writeBoolean(this.matchHorizontal);
            buf.writeBoolean(this.matchVertical);
            buf.writeVarInt(ingredients.size());
            for (SizeSpecificIngredient ingredient : ingredients) {
                ingredient.toNetwork(buf);
            }
        }

        public static Pattern getPatternFrom(JsonObject object) {
            JsonObject patternObject = object.getAsJsonObject("recipe_pattern");
            Pattern pattern = Pattern.of(
                    JsonUtils.getIntOr("rows", patternObject, 0),
                    mod.kerzox.exotek.common.util.JsonUtils.getIntOr("cols", patternObject, 0),
                    mod.kerzox.exotek.common.util.JsonUtils.getBooleanOr("match_horizontal", patternObject, false),
                    mod.kerzox.exotek.common.util.JsonUtils.getBooleanOr("match_vertical", patternObject, false)
            );
            pattern.ingredients = getAsIngredientList(patternObject);
            return pattern;
        }

        public static NonNullList<SizeSpecificIngredient> getAsIngredientList(JsonObject object) {
            NonNullList<SizeSpecificIngredient> list = NonNullList.create();

            JsonArray pattern = object.getAsJsonArray("pattern");
            JsonObject keys = object.getAsJsonObject("key");

            for (JsonElement jsonElement : pattern) {
                String row = jsonElement.getAsString();
                for (char c : row.toCharArray()) {
                    SizeSpecificIngredient ingredient = (c != ' ') ? SizeSpecificIngredient.Serializer.INSTANCE.parse(keys.getAsJsonObject(String.valueOf(c))) : SizeSpecificIngredient.of();
                    list.add(ingredient);
                }
            }

            return list;
        }

        public void writeToJson(JsonObject object) {
            JsonObject patt = new JsonObject();
            JsonObject keys = new JsonObject();
            patt.addProperty("rows", rows);
            patt.addProperty("cols", cols);
            patt.addProperty("match_horizontal", matchHorizontal);
            patt.addProperty("match_vertical", matchVertical);
            predicateMap.forEach((symbol, ing) -> {
                keys.add(String.valueOf(symbol), ing.toJson());
            });
            patt.add("key", keys);

            JsonArray arrayPattern = new JsonArray();
            for (String row : pattern) {
                arrayPattern.add(row);
            }
            patt.add("pattern", arrayPattern);
            object.add("recipe_pattern", patt);
        }

        public Pattern horizontalMatch() {
            matchHorizontal = true;
            return this;
        }

        public Pattern verticalMatch() {
            matchVertical = true;
            return this;
        }

        public boolean isMatchHorizontal() {
            return matchHorizontal;
        }

        public boolean isMatchVertical() {
            return matchVertical;
        }

        public Pattern row(String pred) {
            if (pred.length() != rows) throw new IllegalArgumentException("Provided row pattern is not valid length " + pred.length() + " should be " + rows);
            for (char c : pred.toCharArray()) {
                if (!predicateMap.containsKey(c) && c != ' ') throw new IllegalArgumentException("Symbol doesn't exist " + c);
            }
            pattern.add(pred);
            if (pattern.size() > rows+cols) throw new IllegalStateException("Pattern is larger than the defined shape");
            return this;
        }

        public Pattern set(int index, String pred) {
            if (pred.length() != cols) throw new IllegalArgumentException("Provided col pattern is not valid length " + pred.length() + " should be " + cols);
            pattern.set(index, pred);
            if (pattern.size() > rows+cols) throw new IllegalStateException("Pattern is larger than the defined shape");
            return this;
        }

        public Pattern addPredicate(Character symbol, SizeSpecificIngredient ingredient) {
            if (predicateMap.containsKey(symbol)) {
                throw new IllegalArgumentException(symbol + " already defined in the pattern");
            }
            predicateMap.put(symbol, ingredient);
            return this;
        }

    }
}
