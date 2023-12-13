package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class ExotekItem extends Item {

    public ExotekItem(Properties p_41383_) {
        super(p_41383_);
    }

    public static ExotekItem burnableItem(Properties properties, int burnTime) {
        return new ExotekItem(properties) {
            @Override
            public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
                return burnTime;
            }
        };
    }

}
