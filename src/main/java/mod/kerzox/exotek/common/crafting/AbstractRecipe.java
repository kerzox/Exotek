package mod.kerzox.exotek.common.crafting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public abstract class AbstractRecipe implements Recipe<RecipeInventoryWrapper> {

    protected final RecipeType<?> type;
    protected final ResourceLocation id;
    protected final String group;
    protected final int duration;

    protected RecipeSerializer<?> serializer;

    public AbstractRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, RecipeSerializer<?> serializer) {
        this.type = type;
        this.id = id;
        this.group = group;
        this.duration = duration;
        this.serializer = serializer;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public boolean canCraftInDimensions(int p_43999_, int p_44000_) {
        return false;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return this.serializer;
    }

    @Override
    public RecipeType<?> getType() {
        return this.type;
    }
}
