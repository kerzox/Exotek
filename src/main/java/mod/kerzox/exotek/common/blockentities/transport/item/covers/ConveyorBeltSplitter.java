package mod.kerzox.exotek.common.blockentities.transport.item.covers;

import mod.kerzox.exotek.Exotek;
import mod.kerzox.exotek.client.render.WrappedPose;
import mod.kerzox.exotek.client.render.transfer.AbstractConveyorCoverRenderer;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltInventory;
import mod.kerzox.exotek.common.blockentities.transport.item.ConveyorBeltRampEntity;
import mod.kerzox.exotek.common.blockentities.transport.item.IConveyorBelt;
import mod.kerzox.exotek.common.capability.item.ItemStackHandlerUtils;
import mod.kerzox.exotek.common.capability.item.ItemStackInventory;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import mod.kerzox.exotek.common.item.ConveyorBeltCoverItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;

import java.util.*;

public class ConveyorBeltSplitter extends AbstractConveyorCover {

    public static final ResourceLocation SPLITTER_MODEL = new ResourceLocation(Exotek.MODID, "block/conveyor_belt_splitter");

    private int prevCount;
    private int index;

    @Override
    protected void writeToData(CompoundTag tag) {

    }

    @Override
    protected void readData(CompoundTag tag) {

    }

    private Optional<IItemHandler> getInventory() {
        BlockEntity blockEntity = getLevel().getBlockEntity(getPos().relative(getFacing()));
        if (blockEntity != null && !(blockEntity instanceof IConveyorBelt<?>)) {
            return blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        }
        return Optional.empty();
    }

    private IItemHandler getConveyorInventory() {
        return getConveyorImpl().getBelt().getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get();
    }

    @Override
    public AABB getCollision() {
        if (this.getConveyorImpl() == null) return super.getCollision();
        switch (getConveyorImpl().getBeltDirection()) {
            case EAST -> {
                return new AABB(
                        this.getPos().getX() + 9/16f, this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1);
            }
            case WEST -> {
                return new AABB(
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 7/16f, this.getPos().getY() + 1, this.getPos().getZ() + 1);
            }
            case SOUTH -> {
                return new AABB(
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ() + 9/16f,
                        this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 1);
            }
            default -> {
                return new AABB(
                        this.getPos().getX(), this.getPos().getY(), this.getPos().getZ(),
                        this.getPos().getX() + 1, this.getPos().getY() + 1, this.getPos().getZ() + 7/16f);
            }
        }

    }

    @Override
    public boolean effectsConveyorExtract() {
        return true;
    }



    @Override
    public boolean onConveyorExtract(ConveyorBeltItemStack itemStackEntity, Vec3 itemStackVectorPos, IConveyorBelt<?> beltPassingItem, IConveyorBelt<?> ourBelt) {
        int outputs = getSurroundingBelts().size();
        index = (index + 1) % outputs;

        // pick one of the surrounding belt inventories
        IConveyorBelt<?> belt = getSurroundingBelts().get(index).getBelt();
        ConveyorBeltInventory beltInventory = belt.getBelt().getInventory();

        if (beltInventory.conveyorBeltInsert(0, itemStackEntity)) {
            BlockPos pos = belt.getBelt().getBlockPos();

            double x = pos.getX() + 0.5f;
            double y = pos.getY() + 0.5f;
            double z = pos.getZ() + 0.5f;

            double itemEntitySize = (4/16d);

            // normal belts

            if (belt.getBeltDirection() == Direction.NORTH) {
                z = z + (5/16f);
            }

            if (belt.getBeltDirection() == Direction.SOUTH) {
                z = z - (5/16f);
            }

            if (belt.getBeltDirection() == Direction.EAST) {
                x = x - 5/16f;
            }

            if (belt.getBeltDirection() == Direction.WEST) {
                x = x + 5/16f;
            }

            // for ramps

            if (belt.getBelt() instanceof ConveyorBeltRampEntity rampEntity) {
                y = y + 3/16f;
            }

            itemStackEntity.setDeltaMovement(Vec3.ZERO);
            itemStackEntity.setPos(x, y, z);

            return true;
        }

        return false;
    }

    //    private boolean attemptToTransferItems() {
//        int outputs = getSurroundingBelts().size();
//        if (outputs < 1) { // just to exit as quick as we can
//            return false;
//        }
//
//        // this just checks if it can find the item entity on the conveyor and if its within the bound
//        for (ConveyorBeltItemStack entity : getLevel().getEntitiesOfClass(ConveyorBeltItemStack.class, getCollision())) {
//            ItemStack ret = getValidConveyor().extractItem(0, 1, true);
//            if (!ret.isEmpty() && ret.is(entity.getTransportedStack().getItem()))
//                ItemStackHandlerUtils.insertAndModifyStack(this.inventory, getValidConveyor().extractItem(0, 1, false));
//        }
//
//        if (this.inventory.getStackFromInputHandler(0).isEmpty()) return;
//        ConveyorBeltInventory beltInventory = getSurroundingBelts().get(index % outputs).getBelt().getInventory();
//        ItemStack remaining = beltInventory.insertItem(0, this.inventory.getInputHandler().forceExtractItem(0, 1, true), false);
//        this.inventory.getStackFromInputHandler(0).shrink(this.inventory.getStackFromInputHandler(0).getCount() - remaining.getCount());
//
//        index++;
//        prevCount = outputs;
//    }

    @Override
    public void tick(IConveyorBelt<?> belt) {

    }

    @Override
    public void onBeltUpdate(IConveyorBelt<?> beltOnTopOf, IConveyorBelt<?> causeOfUpdate, Direction directionFrom) {

    }

    /**
     * Find all belts that surround this splitter
     * While ignoring all belts that are pushing into this conveyor belt (if the belt direction of the neighbour is not equal to this looped direction we skip) and the belt must be empty
     * @return the list of belts that fit these conditionals
     */

    private List<IConveyorBelt<?>> getSurroundingBelts() {
        List<IConveyorBelt<?>> list = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            if (getLevel().getBlockEntity(getPos().relative(direction)) instanceof IConveyorBelt<?> b) {
                if (b.getBeltDirection() == direction) list.add(b);
            }
        }
        return list;
    }

    @Override
    public IConveyorCover create(ConveyorBeltCoverItem conveyorBeltCoverItem) {
        return new ConveyorBeltSplitter();
    }


    // client stuff

    // we want to cancel the render so players can not see the itemstack.
    @Override
    public boolean beforeItemStackRender(WrappedPose wp, ItemStack stack, MultiBufferSource bufferSource, int combinedLight, BakedModel model) {
        return true;
    }

    @Override
    protected AbstractConveyorCoverRenderer instance() {
        return new AbstractConveyorCoverRenderer(SPLITTER_MODEL) {

            @Override
            public void init() {

            }

            @Override
            protected void render(ConveyorBeltEntity entity, IConveyorCover cover,
                                  float partialTicks,
                                  WrappedPose poseStack,
                                  MultiBufferSource bufferSource,
                                  int combinedLight,
                                  int combinedOverlay) {
                poseStack.translate(0, 8/16f, 0);
                Minecraft.getInstance().getBlockRenderer().getModelRenderer().tesselateWithAO(Minecraft.getInstance().level, getModel(), entity.getBlockState(),
                        entity.getBlockPos(),
                        poseStack.asStack(), bufferSource.getBuffer(RenderType.cutout()), true, Minecraft.getInstance().level.getRandom(), entity.getBlockPos().asLong(), combinedOverlay, ModelData.EMPTY,
                        RenderType.cutout());

            }
        };
    }


}
