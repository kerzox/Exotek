package mod.kerzox.exotek.common.crafting;

import com.google.gson.JsonObject;
import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Consumer;


public class CustomShapedRecipe extends AbstractRecipe {

    public CustomShapedRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, RecipeSerializer<?> serializer) {
        super(type, id, group, duration, serializer);
    }



    @Override
    public boolean requiresCondition() {
        return false;
    }

    @Override
    public boolean matches(RecipeInventoryWrapper p_44002_, Level p_44003_) {
        return false;
    }

    @Override
    public ItemStack assemble(RecipeInventoryWrapper p_44001_, RegistryAccess p_267165_) {
        return null;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess p_267052_) {
        return null;
    }

    public static class Serializer implements RecipeSerializer<CustomShapedRecipe> {

        @Override
        public CustomShapedRecipe fromJson(ResourceLocation p_44103_, JsonObject p_44104_) {
            return null;
        }

        @Override
        public @Nullable CustomShapedRecipe fromNetwork(ResourceLocation p_44105_, FriendlyByteBuf p_44106_) {
            return null;
        }

        @Override
        public void toNetwork(FriendlyByteBuf p_44101_, CustomShapedRecipe p_44102_) {

        }
    }

    public static class DatagenBuilder {
        private final ResourceLocation name;
        private final ItemStack result;
        private final PatternRecipe.Pattern pattern;
        private String group;
        final int duration;
        private final RecipeSerializer<?> supplier;

        public DatagenBuilder(ResourceLocation name, ItemStack result, PatternRecipe.Pattern pattern, int duration, RecipeSerializer<?> supplier) {
            this.name = name;
            this.result = result;
            this.pattern = pattern;
            this.group = Exotek.MODID;
            this.duration = duration;
            this.supplier = supplier;
        }

        public static CustomShapedRecipe.DatagenBuilder addShapedRecipe(
                ResourceLocation name,
                ItemStack result,
                int duration,
                PatternRecipe.Pattern pattern) {
            return new CustomShapedRecipe.DatagenBuilder(name, result, pattern, duration, Registry.CIRCUIT_ASSEMBLY_RECIPE_SERIALIZER.get());
        }

        public void build(Consumer<FinishedRecipe> consumer) {
            consumer.accept(new CustomShapedRecipe.DatagenBuilder.Factory(
                    this.name,
                    this.group == null ? "" : this.group,
                    this.result,
                    this.pattern,
                    this.duration,
                    this.supplier));
        }

        public static class Factory implements FinishedRecipe {
            private final ResourceLocation name;
            private final ItemStack result;
            private final PatternRecipe.Pattern pattern;
            private String group;
            final int duration;
            private final RecipeSerializer<?> supplier;

            public Factory(ResourceLocation name, String group, ItemStack result, PatternRecipe.Pattern pattern, int duration, RecipeSerializer<?> supplier) {
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

            @Override
            public void serializeRecipeData(JsonObject json) {
                if (!this.group.isEmpty()) {
                    json.addProperty("group", this.group);
                }
                pattern.writeToJson(json);
                json.addProperty("duration", this.duration);
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
