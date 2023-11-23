package mod.kerzox.exotek.common.blockentities.machine;

import mod.kerzox.exotek.client.gui.menu.CentrifugeMenu;
import mod.kerzox.exotek.common.blockentities.BasicBlockEntity;
import mod.kerzox.exotek.common.blockentities.MachineBlockEntity;
import mod.kerzox.exotek.common.blockentities.RecipeWorkingBlockEntity;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.deposit.ChunkDeposit;
import mod.kerzox.exotek.common.capability.deposit.OreDeposit;
import mod.kerzox.exotek.common.capability.energy.SidedEnergyHandler;
import mod.kerzox.exotek.common.capability.fluid.SidedMultifluidTank;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.crafting.RecipeInteraction;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.ingredient.FluidIngredient;
import mod.kerzox.exotek.common.crafting.ingredient.SizeSpecificIngredient;
import mod.kerzox.exotek.common.crafting.recipes.CentrifugeRecipe;
import mod.kerzox.exotek.common.crafting.recipes.GroundSampleDrillRecipe;
import mod.kerzox.exotek.common.util.IServerTickable;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;

public class GroundSampleDrillEntity extends MachineBlockEntity implements GeoBlockEntity, IServerTickable {

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
        super(Registry.BlockEntities.GROUND_SAMPLE_DRILL_ENTITY.get(), pos, state);
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
                // give data

                level.getChunkAt(this.worldPosition).getCapability(ExotekCapabilities.DEPOSIT_CAPABILITY).ifPresent(cap -> {

                    if (cap instanceof ChunkDeposit chunkDeposit) {

                        if (chunkDeposit.isOreDeposit()) {
                            ItemStack stack = new ItemStack(Items.WRITTEN_BOOK);

                            CompoundTag tag = new CompoundTag();

                            stack.addTagElement("filtered_title", StringTag.valueOf(chunkDeposit.getOreDeposit().getName()));

                            ListTag tag2 = new ListTag();
                            for (OreDeposit.OreStack item : chunkDeposit.getOreDeposit().getItems()) {
                                tag2.add(StringTag.valueOf("\nAmount"+item.getItemStack().getCount()));
                            }

                            stack.addTagElement("pages", tag2);
                            stack.addTagElement("title", StringTag.valueOf(chunkDeposit.getOreDeposit().getName()));
                            stack.addTagElement("author", StringTag.valueOf("Sample Drill"));
                            stack.addTagElement("resolved", StringTag.valueOf(String.valueOf((byte) 1)));

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
            if (!running) {
                running = true;
                duration = 20*10;
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
        pTag.put("energyHandler", this.energyHandler.serialize());
        pTag.put("fluidHandler", this.sidedMultifluidTank.serializeNBT());
        pTag.put("itemHandler", this.itemStackHandler.serialize());
        pTag.putBoolean("running", this.running);
    }

    @Override
    protected void read(CompoundTag pTag) {
        this.energyHandler.deserialize(pTag.getCompound("energyHandler"));
        this.sidedMultifluidTank.deserializeNBT(pTag.getCompound("fluidHandler"));
        this.duration = pTag.getInt("duration");
        this.maxDuration = pTag.getInt("max_duration");
        this.itemStackHandler.deserialize(pTag.getCompound("itemHandler"));
        this.running = pTag.getBoolean("running");
    }

    @Override
    protected void addToUpdateTag(CompoundTag tag) {
        tag.put("energyHandler", this.energyHandler.serialize());
        tag.put("fluidHandler", this.sidedMultifluidTank.serializeNBT());
        tag.put("itemHandler", this.itemStackHandler.serialize());
        tag.putBoolean("running", this.running);
        tag.putInt("max_duration", this.maxDuration);
        tag.putInt("duration", this.duration);
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
