package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.blockentities.multiblock.IManager;
import mod.kerzox.exotek.common.blockentities.multiblock.ManagerMultiblockEntity;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.FlotationPlantManager;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.MinerManager;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MinerEntity extends ManagerMultiblockEntity implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MinerEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.MINER_ENTITY.get(), new MinerManager(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return null;
    }

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.model.idle");
    protected static final RawAnimation MOVING = RawAnimation.begin().thenLoop("animation.model.drilling");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, (state) -> getMultiblockManager().animationPredicate(state)));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public MinerManager getMultiblockManager() {
        return (MinerManager) this.multiblockManager;
    }
}
