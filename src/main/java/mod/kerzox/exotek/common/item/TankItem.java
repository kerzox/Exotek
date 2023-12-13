package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.capability.fluid.FluidStorageTank;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

public class TankItem extends ExotekBlockItem {

    private int capacity;

    public TankItem(Block Block, Properties p_41383_, int capacity) {
        super(Block, p_41383_);
        this.capacity = capacity;
    }

    @Override
    public void onDestroyed(ItemEntity p_150700_) {

    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new FluidHandlerItemStack(stack, capacity);
    }
}
