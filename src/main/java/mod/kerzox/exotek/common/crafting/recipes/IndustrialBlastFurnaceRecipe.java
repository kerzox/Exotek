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
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;


public class IndustrialBlastFurnaceRecipe extends AbstractRecipe<RecipeInventoryWrapper> implements RecipeInteraction {

    private final NonNullList<Ingredient> ingredients = NonNullList.create();
    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private final Map<Ingredient, Boolean> matching = new HashMap<>();
    private final Map<Ingredient, Boolean> matchingFluids = new HashMap<>();
    private final ItemStack result;
    private final int heat;

    public IndustrialBlastFurnaceRecipe(RecipeType<?> type, ResourceLocation id, String group, ItemStack result, Ingredient[] ingredients,
                                        FluidIngredient[] fluidIngredients, int duration, int heat) {
        super(type, id, group, duration, ExotekRegistry.INDUSTRIAL_BLAST_FURNACE_RECIPE_SERIALIZER.get());
        this.result = result;
        this.fluidIngredients.addAll(Arrays.asList(fluidIngredients));
        this.ingredients.addAll(Arrays.asList(ingredients));
        this.ingredients.forEach(i -> matching.put(i, false));
        this.fluidIngredients.forEach(i -> matchingFluids.put(i, false));
        this.heat = heat;
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        if (!pContainer.canStorageFluid()) return false;

        boolean item = getIngredients().get(0).test(pContainer.getItem(0));

        if (getFluidIngredients().get(0).isEmpty() && item) return true;

        boolean fluid = getFluidIngredients().get(0).test(pContainer.getFluidHandler().getFluidInTank(0));

        return item && fluid;
    }

    public int getHeat() {
        return heat;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.ingredients;
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


    public static class Serializer implements RecipeSerializer<IndustrialBlastFurnaceRecipe> {

        @Override
        public IndustrialBlastFurnaceRecipe fromJson(ResourceLocation id, JsonObject json) {
            String group = JsonUtils.getStringOr("group", json, "");
            JsonObject ingredients = json.getAsJsonObject("ingredients");
            Ingredient[] ingredient = JsonUtils.deserializeIngredients(ingredients);
            FluidIngredient[] fluidIngredients = JsonUtils.deserializeFluidIngredients(ingredients);
            ItemStack resultStack = JsonUtils.deserializeItemStack(json);
            int duration = JsonUtils.getIntOr("duration", json, 0);
            int heat = JsonUtils.getIntOr("heat", json, 1500);
            return new IndustrialBlastFurnaceRecipe(ExotekRegistry.INDUSTRIAL_BLAST_FURNACE_RECIPE.get(), id, group, resultStack, ingredient, fluidIngredients, duration, heat);

        }

        @Override
        public @Nullable IndustrialBlastFurnaceRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
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
            ItemStack resultStack = buf.readItem();
            int duration = buf.readVarInt();
            int heat = buf.readVarInt();
            return new IndustrialBlastFurnaceRecipe(ExotekRegistry.INDUSTRIAL_BLAST_FURNACE_RECIPE.get(), id, group, resultStack, ingredients, fluidIngredients, duration, heat);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buf, IndustrialBlastFurnaceRecipe recipe) {
            buf.writeUtf(recipe.getGroup());
            buf.writeVarInt(recipe.getIngredients().size());
            for (Ingredient ingredient : recipe.getIngredients()) {
                ingredient.toNetwork(buf);
            }
            buf.writeVarInt(recipe.getFluidIngredients().size());
            for (FluidIngredient ingredient : recipe.getFluidIngredients()) {
                FluidIngredient.Serializer.INSTANCE.write(buf, ingredient);
            }
            buf.writeItem(recipe.getResultItem(RegistryAccess.EMPTY));
            buf.writeVarInt(recipe.getDuration());
            buf.writeVarInt(recipe.getHeat());
        }

    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack result;
        private final Ingredient[] ingredient;
        private final FluidIngredient[] fluidIngredients;
        private String group;
        private final int duration;
        private final int heat;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack result, Ingredient[] ingredient, FluidIngredient[] fluidIngredients, int duration, int heat, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.ingredient = ingredient;
            this.fluidIngredients = fluidIngredients;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
            this.heat = heat;
        }


        public static DatagenBuilder addRecipe(ResourceLocation name, ItemStack result, Ingredient ingredients, FluidIngredient fluidIngredients, int duration, int heat) {
            return new DatagenBuilder(name, result, new Ingredient[]{ingredients}, new FluidIngredient[]{fluidIngredients}, duration, heat, ExotekRegistry.INDUSTRIAL_BLAST_FURNACE_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.ingredient,
                    this.fluidIngredients,
                    this.duration,
                    this.heat,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final ItemStack result;
            private final Ingredient[] ingredient;
            private final FluidIngredient[] fluidIngredients;
            private String group;
            private final int duration;
            private int heat;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack result, Ingredient[] ingredient, FluidIngredient[] fluidIngredients, int duration, int heat, RecipeSerializer<?> supplier) {
                this.name = name;
                this.group = group;
                this.result = result;
                this.ingredient = ingredient;
                this.fluidIngredients = fluidIngredients;
                this.duration = duration;
                this.heat = heat;
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
                json.addProperty("heat", this.heat);
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
