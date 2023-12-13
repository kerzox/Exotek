package mod.kerzox.exotek.common.blockentities.machine;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.kerzox.exotek.common.blockentities.CapabilityBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.OreDeposit;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.common.util.JsonUtils;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.json.JsonString;
import java.util.ArrayList;
import java.util.List;

public class GroundSampleDrillEntity extends CapabilityBlockEntity implements GeoBlockEntity, IServerTickable {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int feTick = 5;
    protected boolean running;
    protected int duration;
    protected int maxDuration;

    private final SidedEnergyHandler energyHandler = new SidedEnergyHandler(16000){
        @Override
        protected void onContentsChanged() {
            syncBlockEntity();
        }

    };

    private final SidedMultifluidTank sidedMultifluidTank = new SidedMultifluidTank(1, 32000, 1, 32000) {
        @Override
        protected void onContentsChanged(IFluidHandler handlerAffected) {
            if (!(handlerAffected instanceof OutputWrapper)) { // ignore output handler
                running = false;
            }
        }

    };

    private final ItemStackInventory itemStackHandler = new ItemStackInventory(1, 2) {
        @Override
        protected void onContentsChanged(IItemHandlerModifiable handler, int slot) {
            if (!(handler instanceof OutputHandler)) { // ignore output handler
                running = false;
            }
        }
    };

    public GroundSampleDrillEntity(BlockPos pos, BlockState state) {
        super(ExotekRegistry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.get(), pos, state);
        addCapabilities(itemStackHandler, sidedMultifluidTank, energyHandler);
    }

    @Override
    public void tick() {
        if (running) {
            if (duration > 0) {
                if (!this.energyHandler.hasEnough(feTick)) {
                    return;
                }
                this.energyHandler.consumeEnergy(feTick);
                duration--;
            } else {

                level.getChunkAt(this.worldPosition).getCapability(ExotekCapabilities.DEPOSIT_CAPABILITY).ifPresent(cap -> {

                    if (cap instanceof ChunkDeposit chunkDeposit) {

                        if (chunkDeposit.isFluidDeposit()) {
                            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

                            CompoundTag tag = new CompoundTag();

                            stack.addTagElement("filtered_title", StringTag.valueOf("Fluids because im retarded and forgot to actually implement these properly should still exist and stuff"));
                            ListTag listtag = new ListTag();
                            String str = "'";

                            for (FluidStack fluid : chunkDeposit.getFluids()) {
                                str += "" + fluid.getDisplayName().getString().toString() + "\n";
                                str += "Amount: " + fluid.getAmount() + "\n";
                            }

                            str += "'";

                            listtag.add(StringTag.valueOf(str));


                            stack.addTagElement("pages", listtag);
                            stack.addTagElement("title", StringTag.valueOf("Fluids"));
                            stack.addTagElement("author", StringTag.valueOf("Sample Drill"));
                            stack.addTagElement("resolved", StringTag.valueOf(String.valueOf((byte) 1)));

                            System.out.println(stack.getTag());

                            ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                            level.addFreshEntity(entity);
                        }

                        if (chunkDeposit.isOreDeposit()) {
                            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

                            CompoundTag tag = new CompoundTag();

                            stack.addTagElement("filtered_title", StringTag.valueOf(chunkDeposit.getOreDeposit().getName()));
                            ListTag listtag = new ListTag();
                            String str = "'";

                            for (OreDeposit.OreStack item : chunkDeposit.getItems()) {
                                str += "" + item.getItemStack().getItem().toString() + "\n";
                                str += "Amount: " + item.getItemStack().getCount() + "\n";
                            }

                            str += "'";

                            listtag.add(StringTag.valueOf(str));


                            stack.addTagElement("pages", listtag);
                            stack.addTagElement("title", StringTag.valueOf(chunkDeposit.getOreDeposit().getName()));
                            stack.addTagElement("author", StringTag.valueOf("Sample Drill"));
                            stack.addTagElement("resolved", StringTag.valueOf(String.valueOf((byte) 1)));

                            System.out.println(stack.getTag());

                            ItemEntity entity = new ItemEntity(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                            level.addFreshEntity(entity);
                        }


                    }

                });

                running = false;
                syncBlockEntity();
            }
        }
    }

    @Override
    public boolean onPlayerClick(Level pLevel, Player pPlayer, BlockPos pPos, InteractionHand pHand, BlockHitResult pHit) {
        if (!pLevel.isClientSide && pHand == InteractionHand.MAIN_HAND) {
            ItemStack stack = pPlayer.getMainHandItem();
            if (!running) {
                running = true;
                duration = 20 * 10;
                maxDuration = duration;
                syncBlockEntity();
            }
        }
        return super.onPlayerClick(pLevel, pPlayer, pPos, pHand, pHit);
    }

    public SidedMultifluidTank getSidedMultifluidTank() {
        return sidedMultifluidTank;
    }


    @Override
    protected void write(CompoundTag pTag) {
        pTag.putBoolean("running", this.running);
        pTag.putInt("max_duration", this.maxDuration);
        pTag.putInt("duration", this.duration);
    }


    @Override
    protected void read(CompoundTag pTag) {
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
        this.running = pTag.getBoolean("running");
    }

    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.model.idle");
    protected static final RawAnimation MOVING = RawAnimation.begin().thenLoop("animation.model.drill_up_down");

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }

    private <T extends GeoAnimatable> PlayState predicate(AnimationState<T> tAnimationState) {
        return running ? tAnimationState.setAndContinue(MOVING)
                : tAnimationState.setAndContinue(IDLE);
    }


}
