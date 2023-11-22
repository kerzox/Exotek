package mod.kerzox.exotek.common.blockentities.multiblock.entity;

import mod.kerzox.exotek.common.blockentities.multiblock.manager.PumpjackManager;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class PumpjackEntity extends ManagerMultiblockEntity<PumpjackManager> implements IServerTickable, GeoBlockEntity {
    
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private boolean running = false;

    public PumpjackEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registry.BlockEntities.PUMP_JACK_ENTITY.get(), new PumpjackManager(), pPos, pBlockState);
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

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "animation.model.pump2", 0, this::predicate));
    }

    @Override
    public AABB getRenderBoundingBox() {
        return getMultiblockManager() != null ? getMultiblockManager().getRenderingBox() != null ? getMultiblockManager().getRenderingBox() :
                super.getRenderBoundingBox() : super.getRenderBoundingBox();
    }

    @Override
    public PumpjackManager getMultiblockManager() {
        return (PumpjackManager) super.getMultiblockManager();
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        if (running) {
            tAnimationState.getController().setAnimation(RawAnimation.begin().then("animation.model.pump2", Animation.LoopType.LOOP));
        } else {
            return PlayState.STOP;
        }
        return PlayState.CONTINUE;
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public void setRunning(boolean running) {
        this.running = running;
        syncBlockEntity();
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        super.addToUpdateTag(tag);
        tag.putBoolean("running", this.running);
    }

    @Override
    protected void read(CompoundTag pTag) {
        super.read(pTag);
        running = pTag.getBoolean("running");
    }

    @Override
    public VoxelShape getShape() {
        return super.getShape();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
