package mod.kerzox.exotek.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.PatternRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.util.JsonUtils;
import mod.kerzox.exotek.registry.ExotekRegistry;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class WorkstationRecipe extends AbstractRecipe<RecipeInventoryWrapper> implements RecipeInteraction {

    private PatternRecipe.Pattern pattern;
    private final NonNullList<SizeSpecificIngredient> ingredients = NonNullList.create();
    private final Map<SizeSpecificIngredient, Boolean> matching = new HashMap<>();
    private final Map<Ingredient, Boolean> matchingFluids = new HashMap<>();
    private final ItemStack itemResults;

    public WorkstationRecipe(RecipeType<?> type, ResourceLocation id, String group, PatternRecipe.Pattern pattern, ItemStack result, int duration) {
        super(type, id, group, duration, ExotekRegistry.WORKSTATION_RECIPE_SERIALIZER.get());
        this.itemResults = result;
        this.ingredients.forEach(i -> matching.put(i, false));
        this.pattern = pattern;
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        boolean itemsMatch = false;

        if (pattern.isMatchVertical() && pattern.isMatchHorizontal()) {
            // do shaped
            if (PatternRecipe.hasMatchingShapedRecipe(3, 3, pattern.getIngredients(), pContainer)) itemsMatch = true;
        }
        else if (pattern.isMatchHorizontal()) {
            // do horizontal only
            if (PatternRecipe.hasMatchingRow(3, 3, pattern, pContainer)) itemsMatch = true;
        }
        else if (pattern.isMatchVertical()) {

        }
        else {
             if (PatternRecipe.hasMatchingShapelessRecipe(3, 3, pattern.getIngredients(), pContainer)) itemsMatch = true;
        }

        return itemsMatch && !matchingFluids.containsValue(false);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return NonNullList.create();
    }

    public NonNullList<SizeSpecificIngredient> getSizeIngredients() {
        return pattern.getIngredients();
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper p_44001_, RegistryAccess p_267165_) {
        return this.itemResults.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return this.itemResults;
    }

    @Override
    public AbstractRecipe getRecipe() {
        return this;
    }

    @Override
    public boolean requiresCondition() {
        return true;
    }


    public static class Serializer implements RecipeSerializer<WorkstationRecipe> {

        @Override
        public WorkstationRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            JsonObject ingredients = json.getAsJsonObject("ingredients");
            FluidIngredient[] fluidIngredients = JsonUtils.deserializeFluidIngredients(ingredients);
            PatternRecipe.Pattern pattern = PatternRecipe.Pattern.getPatternFrom(json);
            JsonObject results = json.getAsJsonObject("results");
            ItemStack[] itemResult = JsonUtils.deserializeItemStacks(results);
            int duration = JsonUtils.getIntOr("duration", json, 0);

            if (itemResult.length > 1) throw new IllegalStateException("Results can't be more than 1");

            return new WorkstationRecipe(ExotekRegistry.WORKSTATION_RECIPE.get(), id, group,
                    pattern, itemResult[0], duration);

        }

        @Override
        public @Nullable WorkstationRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            PatternRecipe.Pattern pattern = PatternRecipe.Pattern.fromNetwork(buf);
            int itemCount = buf.readVarInt();
            ItemStack[] itemResults = new ItemStack[itemCount];
            for (int i = 0; i < itemResults.length; i++) {
                itemResults[i] = buf.readItem();
            }
            int duration = buf.readVarInt();
            return new WorkstationRecipe(ExotekRegistry.WORKSTATION_RECIPE.get(), id, group, pattern, itemResults[0], duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, WorkstationRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            recipe.pattern.toNetwork(buf);
            buf.writeVarInt(1);
            buf.writeItem(recipe.itemResults);
            buf.writeVarInt(recipe.getDuration());
        }

    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack[] result;
        private final PatternRecipe.Pattern pattern;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack[] result, PatternRecipe.Pattern pattern, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.pattern = pattern;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }


        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, PatternRecipe.Pattern pattern, int duration) {
            return new DatagenBuilder(
                    name, new ItemStack[] {result}, pattern, duration, ExotekRegistry.WORKSTATION_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.pattern,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final ItemStack[] result;
            private final PatternRecipe.Pattern pattern;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack[] result, PatternRecipe.Pattern pattern, int duration, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.pattern = pattern;
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

            private JsonObject serializeFluidStack(FluidStack stack) {
                JsonObject json = new JsonObject();
                json.addProperty("fluid", ForgeRegistries.FLUIDS.getKey(stack.getFluid()).toString());
                if (stack.getAmount() != 0) {
                    json.addProperty("amount", stack.getAmount());
                }
                return json;
            }


            @Override
            public void serializeRecipeData(JsonObject json) {
                if (!this.group.isEmpty()) {
                    json.addProperty("group", this.group);
                }
                json.addProperty("duration", this.duration);
                JsonObject ingredients = new JsonObject();
                json.add("ingredients", ingredients);
                pattern.writeToJson(json);

                JsonObject results = new JsonObject();

                JsonArray item_results = new JsonArray();
                for (ItemStack itemStack : this.result) {
                    item_results.add(serializeItemStacks(itemStack));
                }
                results.add("item_result", item_results);

                json.add("results", results);
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