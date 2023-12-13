package mod.kerzox.exotek.common.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ExotekBucket extends BucketItem {

    public ExotekBucket(Supplier<? extends Fluid> supplier, Properties builder) {
        super(supplier, builder);
    }




}
