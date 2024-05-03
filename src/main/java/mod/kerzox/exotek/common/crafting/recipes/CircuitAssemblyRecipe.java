package mod.kerzox.exotek.common.crafting.recipes;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.PatternRecipe;
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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class CircuitAssemblyRecipe extends AbstractRecipe<RecipeInventoryWrapper> implements RecipeInteraction {

    private PatternRecipe.Pattern pattern;
    private final NonNullList<SizeSpecificIngredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final Map<Ingredient, Boolean> matchingFluids = new HashMap<>();
    private final ItemStack result;

    public CircuitAssemblyRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, PatternRecipe.Pattern pattern, SizeSpecificIngredient[] ingredients,
                                 FluidIngredient[] fluidIngredients, int duration) {
        super(type, id, group, duration, ExotekRegistry.CIRCUIT_ASSEMBLY_RECIPE_SERIALIZER.get());
        this.result = result;
        this.pattern = pattern;
        this.fluidIngredients.addAll(Arrays.asList(fluidIngredients));
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.forEach(i -> matchingFluids.put(i, false));
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        this.fluidIngredients.forEach(i -> matchingFluids.put(i, false));

        if (!pContainer.canStorageFluid()) throw new IllegalStateException("You can't have recipe inventory for this recipe without fluid");

        getFluidIngredients().forEach(((ingredient) -> {
            for (int i = 0; i < pContainer.getFluidHandler().getTanks(); i++) {
                if (ingredient.test(pContainer.getFluidHandler().getFluidInTank(i))) {
                    matchingFluids.put(ingredient, true);
                }
            }
        }));

        if (pattern.isMatchVertical() && pattern.isMatchVertical()) {
            return PatternRecipe.hasMatchingShapedRecipe(2, 3, getSizeIngredients(), pContainer) && !matchingFluids.containsValue(false);
        }

        if (!pattern.isMatchHorizontal() && !pattern.isMatchVertical()) {
            return PatternRecipe.hasMatchingShapelessRecipe(2, 3, getSizeIngredients(), pContainer) && !matchingFluids.containsValue(false);
        }

        return PatternRecipe.hasMatchingShapedRecipe(2, 3, getSizeIngredients(), pContainer) && !matchingFluids.containsValue(false);
    }

    public NonNullList<SizeSpecificIngredient> getSizeIngredients() {
        return pattern.getIngredients();
    }

    public NonNullList<FluidIngredient> getFluidIngredients() {
        return fluidIngredients;
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

    @Override
    public boolean requiresCondition() {
        return true;
    }


    public static class Serializer implements RecipeSerializer<CircuitAssemblyRecipe> {

        @Override
        public CircuitAssemblyRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            JsonObject ingredients = json.getAsJsonObject("ingredients");
            FluidIngredient[] fluidIngredients = JsonUtils.deserializeFluidIngredients(ingredients);
            PatternRecipe.Pattern pattern = PatternRecipe.Pattern.getPatternFrom(json);
            ItemStack resultStack = JsonUtils.deserializeItemStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            return new CircuitAssemblyRecipe(ExotekRegistry.CIRCUIT_ASSEMBLY_RECIPE.get(), id, group, resultStack,
                    pattern,
                    pattern.getIngredients().toArray(SizeSpecificIngredient[]::new), fluidIngredients, duration);

        }

        @Override
        public @Nullable CircuitAssemblyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
            String group = buf.readUtf();
            PatternRecipe.Pattern pattern = PatternRecipe.Pattern.fromNetwork(buf);
            int ingredientCount = buf.readVarInt();
            SizeSpecificIngredient[] ingredients = new SizeSpecificIngredient[ingredientCount];
            for (int i = 0; i < ingredients.length; i++) {
                ingredients[i] = (SizeSpecificIngredient) Ingredient.fromNetwork(buf);
            }
            int fluidCount = buf.readVarInt();
            FluidIngredient[] fluidIngredients = new FluidIngredient[fluidCount];
            for (int i = 0; i < fluidIngredients.length; i++) {
                fluidIngredients[i] = FluidIngredient.of(buf);
            }
            ItemStack resultStack = buf.readItem();
            int duration = buf.readVarInt();
            return new CircuitAssemblyRecipe(ExotekRegistry.CIRCUIT_ASSEMBLY_RECIPE.get(), id, group, resultStack, pattern, ingredients, fluidIngredients, duration);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, CircuitAssemblyRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            recipe.pattern.toNetwork(buf);
            buf.writeVarInt(recipe.ingredients.size());
            for (SizeSpecificIngredient ingredient : recipe.ingredients) {
                ingredient.toNetwork(buf);
            }
            buf.writeVarInt(recipe.getFluidIngredients().size());
            for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
                FluidIngredient.Serializer.INSTANCE.write(buf, ingredient);
            }
            buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buf.writeVarInt(recipe.getDuration());
        }

    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack result;
        private final PatternRecipe.Pattern pattern;
        private final FluidIngredient[] fluidIngredients;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack result, PatternRecipe.Pattern pattern, FluidIngredient[] fluidIngredients, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.pattern = pattern;
            this.fluidIngredients = fluidIngredients;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }


        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, PatternRecipe.Pattern pattern, FluidIngredient[] fluidIngredients, int duration) {
            return new DatagenBuilder(name, result, pattern, fluidIngredients, duration, ExotekRegistry.CIRCUIT_ASSEMBLY_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.pattern,
                    this.fluidIngredients,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final ItemStack result;
            private final PatternRecipe.Pattern pattern;
            private final FluidIngredient[] fluidIngredients;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack result, PatternRecipe.Pattern pattern, FluidIngredient[] fluidIngredients, int duration, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.pattern = pattern;
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

            @Override
            public void serializeRecipeData(JsonObject json) {
                if (!this.group.isEmpty()) {
                    json.addProperty("group", this.group);
                }
                json.addProperty("duration", this.duration);
                JsonArray ing2 = new JsonArray();
                for (FluidIngredient ingredient1 : this.fluidIngredients) {
                    ing2.add(ingredient1.toJson());
                }
                JsonObject ingredients = new JsonObject();
                ingredients.add("fluid_ingredient", ing2);
                json.add("ingredients", ingredients);
                pattern.writeToJson(json);
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
