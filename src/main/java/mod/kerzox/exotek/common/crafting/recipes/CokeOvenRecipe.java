package mod.kerzox.exotek.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class CokeOvenRecipe extends AbstractRecipe<RecipeInventoryWrapper> implements RecipeInteraction {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final Map<Ingredient, Boolean> matchingFluids = new HashMap<>();
    private final ItemStack itemResults;
    private final FluidStack fluidResults;

    public CokeOvenRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, FluidStack fluidResults, Ingredient[] ingredients,
                          FluidIngredient[] fluidIngredients, int duration) {
        super(type, id, group, duration, ExotekRegistry.COKE_OVEN_RECIPE_SERIALIZER.get());
        this.itemResults = result;
        this.fluidResults = fluidResults;
        this.fluidIngredients.addAll(Arrays.asList(fluidIngredients));
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.forEach(i -> matchingFluids.put(i, false));
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        return ingredients.get(0).test(pContainer.getItem(0));
    }

    public FluidStack getFluidResults() {
        return fluidResults;
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
    }


    @Override
    public ItemStack assemble(RecipeInventoryWrapper p_44001_, RegistryAccess p_267165_) {
        return itemResults.copy();
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return itemResults;
    }

    @Override
    public AbstractRecipe getRecipe() {
        return this;
    }

    @Override
    public boolean requiresCondition() {
        return true;
    }


    public static class Serializer implements RecipeSerializer<CokeOvenRecipe> {

        @Override
        public CokeOvenRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            JsonObject ingredients = json.getAsJsonObject("ingredients");
            Ingredient[] ingredient = JsonUtils.deserializeIngredients(ingredients);
            FluidIngredient[] fluidIngredients = JsonUtils.deserializeFluidIngredients(ingredients);
            JsonObject results = json.getAsJsonObject("results");
            ItemStack[] itemResult = JsonUtils.deserializeItemStacks(results);
            FluidStack[] fluidResult = JsonUtils.deserializeFluidStacks(2, results);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            return new CokeOvenRecipe(ExotekRegistry.COKE_OVEN_RECIPE.get(), id, group, itemResult[0], fluidResult[0], ingredient, fluidIngredients, duration);

        }

        @Override
        public @Nullable CokeOvenRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            int ingredientCount = buf.readVarInt();
            Ingredient[] ingredients = new Ingredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = Ingredient.fromNetwork(buf);
            }
            int fluidCount = buf.readVarInt();
            FluidIngredient[] fluidIngredients = new FluidIngredient[fluidCount];
            for (int i = 0; i < fluidIngredients.length; i++) {
                fluidIngredients[i] = FluidIngredient.of(buf);
            }
            int itemCount = buf.readVarInt();
            ItemStack[] itemResults = new ItemStack[fluidCount];
            for (int i = 0; i < itemResults.length; i++) {
                itemResults[i] = buf.readItem();
            }
            int fluidCount2 = buf.readVarInt();
            FluidStack[] fluidResults = new FluidStack[fluidCount];
            for (int i = 0; i < fluidResults.length; i++) {
                fluidResults[i] = FluidStack.readFromPacket(buf);
            }
            int duration = buf.readVarInt();
            return new CokeOvenRecipe(ExotekRegistry.COKE_OVEN_RECIPE.get(), id, group, itemResults[0], fluidResults[0], ingredients, fluidIngredients, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CokeOvenRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
            buf.writeVarInt(recipe.getFluidIngredients().size());
            for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
                FluidIngredient.Serializer.INSTANCE.write(buf, ingredient);
            }
            buf.writeVarInt(1);
            buf.writeItem(recipe.itemResults);
            buf.writeVarInt(1);
            recipe.fluidResults.writeToPacket(buf);
           // buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buf.writeVarInt(recipe.getDuration());
        }

    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack[] result;
        private final FluidStack[] fresult;
        private final Ingredient[] ingredient;
        private final FluidIngredient[] fluidIngredients;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack[] result, FluidStack[] fresult, Ingredient[] ingredient, FluidIngredient[] fluidIngredients, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.fresult = fresult;
            this.ingredient = ingredient;
            this.fluidIngredients = fluidIngredients;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }

        /**
         * Add recipe for the engraving machine
         *
         * @param name
         * @param result
         * @param duration
         * @param ingredients First ingredient is the special type usually the lens/mold or something
         * @return
         */
        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, FluidStack fresult, int duration, Ingredient[] ingredients) {
            return new DatagenBuilder(name, new ItemStack[]{result}, new FluidStack[]{fresult}, ingredients,
                    new FluidIngredient[]{FluidIngredient.of(FluidStack.EMPTY)}, duration, ExotekRegistry.COKE_OVEN_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.fresult,
                    this.ingredient,
                    this.fluidIngredients,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final ItemStack[] result;
            private final FluidStack[] fresult;
            private final Ingredient[] ingredient;
            private final FluidIngredient[] fluidIngredients;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack[] result, FluidStack[] fresult, Ingredient[] ingredient, FluidIngredient[] fluidIngredients, int duration, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.fresult = fresult;
                this.ingredient = ingredient;
                this.fluidIngredients = fluidIngredients;
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

                JsonArray ing = new JsonArray();

                for (Ingredient ingredient1 : this.ingredient) {
                    ing.add(ingredient1.toJson());
                }

                JsonArray ing2 = new JsonArray();

                for (FluidIngredient ingredient1 : this.fluidIngredients) {
                    ing2.add(ingredient1.toJson());
                }

                JsonObject ingredients = new JsonObject();
                ingredients.add("ingredient", ing);
                ingredients.add("fluid_ingredient", ing2);
                json.add("ingredients", ingredients);

                JsonObject results = new JsonObject();

                JsonArray item_results = new JsonArray();
                for (ItemStack itemStack : this.result) {
                    item_results.add(serializeItemStacks(itemStack));
                }

                JsonArray fluid_results = new JsonArray();
                for (FluidStack fluidStack : this.fresult) {
                    fluid_results.add(serializeFluidStack(fluidStack));
                }

                results.add("item_result", item_results);
                results.add("fluid_result", fluid_results);

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
