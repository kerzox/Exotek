package mod.kerzox.exotek.common.crafting.recipes;

import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.util.JsonUtils;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

// this is just an additional smelting in case we only want recipes that are only usable by powered furnaces

public class MaceratorRecipe extends AbstractRecipe implements RecipeInteraction {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final ItemStack result;

    public MaceratorRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, Ingredient[] ingredients, int duration) {
        super(type, id, group, duration, Registry.MACERATOR_RECIPE_SERIALIZER.get());
        this.result = result;
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.ingredients.forEach(i -> matching.put(i, false));
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        /*TODO
        Write the recipe matching code for this recipe
         */
        this.ingredients.forEach(i -> matching.put(i, false));
        ingredients.forEach(((ingredient) -> {
            for (int i = 0; i < pContainer.getContainerSize(); i++) {
                if (ingredient.test(pContainer.getItem(i))) {
                    matching.put(ingredient, true);
                }
            }
        }));

        return !matching.containsValue(false);
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper p_44001_, RegistryAccess p_267165_) {
        return result.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return result;
    }

    @Override
    public AbstractRecipe getRecipe() {
        return this;
    }


    public static class Serializer implements RecipeSerializer<MaceratorRecipe> {

        @Override
        public MaceratorRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            Ingredient[] ingredients = JsonUtils.deserializeIngredients(json);
            ItemStack resultStack = JsonUtils.deserializeItemStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            return new MaceratorRecipe(Registry.MACERATOR_RECIPE.get(), id, group, resultStack, ingredients, duration);

        }

        @Override
        public @Nullable MaceratorRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            int ingredientCount = buf.readVarInt();
            Ingredient[] ingredients = new Ingredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = Ingredient.fromNetwork(buf);
            }
            ItemStack resultStack = buf.readItem();
            int duration = buf.readVarInt();
            return new MaceratorRecipe(Registry.MACERATOR_RECIPE.get(), id, group, resultStack, ingredients, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, MaceratorRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
            buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buf.writeVarInt(recipe.getDuration());
        }

    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack result;
        private final Ingredient ingredient;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack result, Ingredient ingredient, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.ingredient = ingredient;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, Ingredient ingredient, int duration) {
            return new DatagenBuilder(name, result, ingredient, duration, Registry.MACERATOR_RECIPE_SERIALIZER.get());
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
            private final ItemStack result;
            private final Ingredient ingredient;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack result, Ingredient ingredient, int duration, RecipeSerializer<?> supplier) {
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
                json.add("result", serializeItemStacks(this.result));
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
