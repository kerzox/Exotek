package mod.kerzox.exotek.common.crafting.recipes;

import mod.kerzox.exotek.common.crafting.AbstractRecipe;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

// this is just an additional smelting in case we only want recipes that are only usable by powered furnaces

public class PoweredFurnaceRecipe extends AbstractRecipe {

    public PoweredFurnaceRecipe(RecipeType<?> type, ResourceLocation id, String group, int duration, RecipeSerializer<?> serializer) {
        super(type, id, group, duration, serializer);
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

    @Override
    public boolean requiresCondition() {
        return true;
    }
}
