package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.client.gui.menu.multiblock.IndustrialMiningDrillMenu;
import mod.kerzox.exotek.common.blockentities.multiblock.manager.MinerManager;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MinerEntity extends ManagerMultiblockEntity<MinerManager> implements GeoBlockEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MinerEntity(BlockPos pPos, BlockState pBlockState) {
        super(ExotekRegistry.BlockEntities.MULTIBLOCK_MINER_ENTITY.get(), new MinerManager(), pPos, pBlockState);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Industrial Mining Drill");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new IndustrialMiningDrillMenu(p_39954_, p_39955_, p_39956_, this);
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
