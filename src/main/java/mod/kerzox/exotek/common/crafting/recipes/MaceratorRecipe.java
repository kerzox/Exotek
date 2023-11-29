package mod.kerzox.exotek.common.crafting.recipes;

import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.RecipeResult;
import mod.kerzox.exotek.common.util.JsonUtils;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

// recipeResult was made because i was dumb and kinda forgot the purpose of tags LOL

public class MaceratorRecipe extends AbstractRecipe<RecipeInventoryWrapper> {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final RecipeResult result;

    public MaceratorRecipe(RecipeType<?> type, ResourceLocation id, String group, RecipeResult result, Ingredient ingredients, int duration) {
        super(type, id, group, duration, ExotekRegistry.MACERATOR_RECIPE_SERIALIZER.get());
        this.result = result;
        this.ingredients.add(ingredients);
        this.ingredients.forEach(i -> matching.put(i, false));
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        return ingredients.get(0).test(pContainer.getItem(0));
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper p_44001_, RegistryAccess p_267165_) {
        return result.getItems().get(0).copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return result.getItems().get(0);
    }

    public RecipeResult getResult() {
        return result;
    }

    @Override
    public AbstractRecipe getRecipe() {
        return this;
    }

    @Override
    public boolean requiresCondition() {
        return true;
    }


    public static class Serializer implements RecipeSerializer<MaceratorRecipe> {

        @Override
        public MaceratorRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            Ingredient ingredients = Ingredient.fromJson(json.get("ingredient"));
            RecipeResult resultStack = RecipeResult.of(ItemStack.EMPTY);
            if (json.get("result").isJsonObject()) {
                JsonObject jsonObject = json.get("result").getAsJsonObject();
                if (jsonObject.has("item")) resultStack = RecipeResult.of(ShapedRecipe.itemStackFromJson(json.get("result").getAsJsonObject()));
                else {
                    ResourceLocation location = new ResourceLocation(JsonUtils.getStringOr("tag", jsonObject, ""));
                    resultStack = new RecipeResult(location, JsonUtils.getIntOr("count", jsonObject, 0), true);
                }
            } else {
                String s1 = JsonUtils.getStringOr("result", json, "");
                ResourceLocation resourcelocation = new ResourceLocation(s1);
                resultStack = RecipeResult.of(new ItemStack(ForgeRegistries.ITEMS.getValue(resourcelocation)));
            }
            int duration = JsonUtils.getIntOr("duration", json, 0);
            return new MaceratorRecipe(ExotekRegistry.MACERATOR_RECIPE.get(), id, group, resultStack, ingredients, duration);

        }

        @Override
        public @Nullable MaceratorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            Ingredient ingredients = Ingredient.fromNetwork(buf);
            RecipeResult recipeResult = new RecipeResult(buf.readResourceLocation(), buf.readInt(), buf.readBoolean());
            int duration = buf.readVarInt();
            return new MaceratorRecipe(ExotekRegistry.MACERATOR_RECIPE.get(), id, group, recipeResult, ingredients, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MaceratorRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
            buf.writeResourceLocation(recipe.getResult().getLocation());
            buf.writeInt(recipe.getResult().getCount());
            buf.writeBoolean(recipe.getResult().isTag());
            buf.writeVarInt(recipe.getDuration());
        }

    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final RecipeResult result;
        private final Ingredient ingredient;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, RecipeResult result, Ingredient ingredient, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.ingredient = ingredient;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, Ingredient ingredient, int duration) {
            return new DatagenBuilder(name, RecipeResult.of(result), ingredient, duration, ExotekRegistry.MACERATOR_RECIPE_SERIALIZER.get());
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, RecipeResult result, Ingredient ingredient, int duration) {
            return new DatagenBuilder(name, result, ingredient, duration, ExotekRegistry.MACERATOR_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new DatagenBuilder.Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.ingredient,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final RecipeResult result;
            private final Ingredient ingredient;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, RecipeResult result, Ingredient ingredient, int duration, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.ingredient = ingredient;
                this.duration = duration;
                this.supplier = supplier;
            }

            private JsonObject serializeItemStacks(ItemStack stack) {
                JsonObject json = new JsonObject();
                json.addProperty("item", Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString());
                if (stack.getCount() != 0) {
                    json.addProperty("count", stack.getCount());
                }
                return json;
            }

            @Override
            public void serializeRecipeData(JsonObject json) {
                if (!this.group.isEmpty()) {
                    json.addProperty("group", this.group);
                }
                json.addProperty("duration", this.duration);
                json.add("ingredient", this.ingredient.toJson());
                if (this.result.isTag()) {
                    JsonObject element = new JsonObject();
                    element.addProperty("tag", result.getLocation().toString());
                    element.addProperty("count", result.getCount());
                    json.add("result", element);
                } else json.add("result", serializeItemStacks(this.result.getItems().get(0)));
            }

            @Override
            public ResourceLocation getId() {
                return this.name;
            }

            @Override
            public RecipeSerializer<?> getType() {
                return this.supplier;
            }

            @Nullable
            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Nullable
            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        }
    }
}
