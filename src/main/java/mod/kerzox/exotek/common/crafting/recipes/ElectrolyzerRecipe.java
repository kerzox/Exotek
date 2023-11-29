package mod.kerzox.exotek.common.crafting.recipes;

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
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;


public class ElectrolyzerRecipe extends AbstractRecipe<RecipeInventoryWrapper> implements RecipeInteraction {

    private final NonNullList<FluidIngredient> fluidIngredients = NonNullList.create();
    private int duration;
    private final FluidStack[] result;

    public ElectrolyzerRecipe(RecipeType<?> type, ResourceLocation id, String group, FluidStack[] result, int duration, FluidIngredient fluidIngredient) {
        super(type, id, group, duration, ExotekRegistry.ELECTROLYZER_RECIPE_SERIALIZER.get());
        this.result = result;
        this.fluidIngredients.add(fluidIngredient);
    }

    public FluidStack[] getResultFluids() {
        return result;
    }

    public FluidStack[] assembleFluids(RecipeInventoryWrapper wrapper) {
        return new FluidStack[] {this.result[0].copy(), this.result[1].copy()};
    }

    @Override
    public boolean matches(RecipeInventoryWrapper pContainer, Level pLevel) {
        if (!pContainer.canStorageFluid()) return false;
        return getFluidIngredient().test(pContainer.getFluidHandler().getFluidInTank(0));
    }
    // shouldnt use
    @Override
    public ItemStack assemble(RecipeInventoryWrapper p_44001_, RegistryAccess p_267165_) {
        return FluidUtil.getFilledBucket(this.result[0].copy());
    }

    // shouldnt use
    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return FluidUtil.getFilledBucket(this.result[0]);
    }

    public FluidIngredient getFluidIngredient() {
        return fluidIngredients.get(0);
    }

    @Override
    public AbstractRecipe getRecipe() {
        return this;
    }

    @Override
    public boolean requiresCondition() {
        return true;
    }

    public static class Serializer implements RecipeSerializer<ElectrolyzerRecipe> {

        @Override
        public ElectrolyzerRecipe fromJson(ResourceLocation pRecipeId, JsonObject pSerializedRecipe) {
            String group = JsonUtils.getStringOr("group", pSerializedRecipe, "");
            int duration = JsonUtils.getIntOr("duration", pSerializedRecipe, 0);
            JsonObject ingredients = pSerializedRecipe.getAsJsonObject("ingredients");
            FluidIngredient fluidIngredient = FluidIngredient.of(ingredients.getAsJsonObject("fluid_ingredient"));
            JsonObject results = pSerializedRecipe.getAsJsonObject("results");
            FluidStack[] result = JsonUtils.deserializeFluidStacks(2, results);
            return new ElectrolyzerRecipe(ExotekRegistry.ELECTROLYZER_RECIPE.get(), pRecipeId, group, result, duration, fluidIngredient);
        }

        @Override
        public @Nullable ElectrolyzerRecipe fromNetwork(ResourceLocation pRecipeId, FriendlyByteBuf pBuffer) {
            String group = pBuffer.readUtf();
            int duration = pBuffer.readInt();
            FluidIngredient fluidIngredient = (FluidIngredient) Ingredient.fromNetwork(pBuffer);
            FluidStack[] results = new FluidStack[2];
            results[0] = pBuffer.readFluidStack();
            results[1] = pBuffer.readFluidStack();
            return new ElectrolyzerRecipe(ExotekRegistry.ELECTROLYZER_RECIPE.get(), pRecipeId, group, results, duration, fluidIngredient);
        }

        @Override
        public void toNetwork(FriendlyByteBuf pBuffer, ElectrolyzerRecipe pRecipe) {
            pBuffer.writeUtf(pRecipe.getGroup());
            pBuffer.writeInt(pRecipe.getDuration());
            pRecipe.getFluidIngredient().toNetwork(pBuffer);
            pBuffer.writeFluidStack(pRecipe.getResultFluids()[0]);
            pBuffer.writeFluidStack(pRecipe.getResultFluids()[1]);
        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final FluidIngredient fluidIngredient;
        private final FluidStack[] result;
        private final int duration;
        private String group;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, String group, int duration, FluidIngredient fluidIngredient, FluidStack[] result, RecipeSerializer<?> supplier) {
            this.name = name;
            this.duration = duration;
            this.fluidIngredient = fluidIngredient;
            this.result = result;
            this.group = group;
            this.supplier = supplier;
        }

        public static DatagenBuilder addRecipe(ResourceLocation name, FluidIngredient fluidIngredient, int duration, FluidStack... result) {
            if (result.length > 3 || result.length <= 1) throw new IllegalStateException(name + " : recipe has illegal number of fluidstacks for results can only have 2 results");
            return new DatagenBuilder(name, Exotek.MODID, duration, fluidIngredient, result, ExotekRegistry.ELECTROLYZER_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.duration,
                    this.fluidIngredient,
                    this.result,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final int duration;
            private final FluidIngredient fluidIngredient;
            private final FluidStack[] result;
            private String group;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, int duration, FluidIngredient fluidIngredient, FluidStack[] result, RecipeSerializer<?> supplier) {
                this.name = name;
                this.duration = duration;
                this.group = group;
                this.fluidIngredient = fluidIngredient;
                this.result = result;
                this.supplier = supplier;
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
            public void serializeRecipeData(JsonObject pJson) {
                pJson.addProperty("group", group);
                pJson.addProperty("duration", duration);
                JsonObject ingredient = new JsonObject();
                ingredient.add("fluid_ingredient", this.fluidIngredient.toJson());
                pJson.add("ingredients", ingredient);
                JsonObject results = new JsonObject();
                results.add("result1", serializeFluidStack(result[0]));
                results.add("result2", serializeFluidStack(result[1]));
                pJson.add("results", results);
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
