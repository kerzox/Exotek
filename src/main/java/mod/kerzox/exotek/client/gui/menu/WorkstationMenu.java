package mod.kerzox.exotek.client.gui.menu;

import mod.kerzox.exotek.client.gui.components.ModifiedResultSlot;
import mod.kerzox.exotek.client.gui.components.SlotComponent;
import mod.kerzox.exotek.client.gui.menu.container.ItemStackCraftingContainer;
import mod.kerzox.exotek.common.blockentities.WorkstationEntity;
import mod.kerzox.exotek.common.capability.IStrictCombinedItemHandler;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import mod.kerzox.exotek.common.crafting.recipes.WorkstationRecipe;
import mod.kerzox.exotek.registry.ExotekRegistry;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.Optional;

public class WorkstationMenu extends DefaultMenu<WorkstationEntity> {

    private final ItemStackCraftingContainer craftSlots = new ItemStackCraftingContainer(
            (IStrictCombinedItemHandler) blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve().get(), 3, 3, this);
    private final ResultContainer resultSlots = new ResultContainer();

    private Slot outputSlot;

    public WorkstationMenu(int pContainerId, Inventory playerInventory, Player player, WorkstationEntity blockEntity) {
        super(ExotekRegistry.Menus.WORKSTATION_GUI.get(), pContainerId, playerInventory, player, blockEntity);
        // do layout of inventory + hotbar
        layoutPlayerInventorySlots(8, 84);
        // add item slots from capability
//        blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(cap -> {
//            addSlotBox(cap, 0, 28, 20, 3, 18, 3, 18);
//            addSlot(cap, 9, 119, 38);
//        });

        addContainerSlotBox(craftSlots, 0, 28, 20, 3, 18, 3, 18);
        addSlot(new ModifiedResultSlot<WorkstationRecipe>(ExotekRegistry.WORKSTATION_RECIPE.get(), player, craftSlots, resultSlots, 9, 119, 38));
        outputSlot = addSlot(new SlotComponent(blockEntity.getHandler().getOutputHandler(), 9, 119, 38, false));

    }

    public Slot getOutputSlot() {
        return outputSlot;
    }

    /*
            if (getMenu().getBlockEntity().getTab() == 1) {
            Optional<WorkstationRecipe> recipe = Minecraft.getInstance().level.getRecipeManager().getRecipeFor(Registry.WORKSTATION_RECIPE.get(),
                    getMenu().getBlockEntity().getRecipeInventoryWrapper(),
                    Minecraft.getInstance().level);
            if (slot instanceof SlotComponent component && component.getSlotIndex() == 9 && recipe.isPresent()) {
                PacketHandler.sendToServer(new CompoundTagPacket("finish_recipe"));
                if (!component.getItem().isEmpty() && this.menu.getCarried().is(component.getItem().getItem())) {
                    this.menu.getCarried().grow(1);
                }
            } else {
                super.slotClicked(slot, p_97779_, p_97780_, p_97781_);
            }
     */

    protected void slotChangedCraftingGrid(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, ItemStackCraftingContainer p_150550_, ResultContainer p_150551_) {
        if (!p_150548_.isClientSide) {
            ServerPlayer serverplayer = (ServerPlayer)p_150549_;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<WorkstationRecipe> recipe = getBlockEntity().getLevel().getRecipeManager().getRecipeFor(ExotekRegistry.WORKSTATION_RECIPE.get(),
                        new RecipeInventoryWrapper(craftSlots.getItemHandlerModifiable()),
                        getBlockEntity().getLevel());
            if (recipe.isPresent()) {
                WorkstationRecipe craftingrecipe = recipe.get();
                if (p_150551_.setRecipeUsed(p_150548_, serverplayer, craftingrecipe)) {
                    ItemStack itemstack1 = craftingrecipe.assemble(new RecipeInventoryWrapper(p_150550_.getItemHandlerModifiable()), p_150548_.registryAccess());
                    if (itemstack1.isItemEnabled(p_150548_.enabledFeatures())) {
                        itemstack = itemstack1;
                    }
                }
            }

            p_150551_.setItem(0, itemstack);
            p_150547_.setRemoteSlot(0, itemstack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(p_150547_.containerId, p_150547_.incrementStateId(), 0, itemstack));
        }
    }

    @Override
    public void slotsChanged(Container p_38868_) {
        super.slotsChanged(p_38868_);
        if (getBlockEntity() != null && getBlockEntity().getLevel() != null) {
            slotChangedCraftingGrid(this, getBlockEntity().getLevel(), this.player, this.craftSlots, this.resultSlots);
        }
//        if (getBlockEntity() != null && getBlockEntity().getLevel() != null) {
//            if (!getBlockEntity().getLevel().isClientSide) {
//                Optional<WorkstationRecipe> recipe = getBlockEntity().getLevel().getRecipeManager().getRecipeFor(Registry.WORKSTATION_RECIPE.get(),
//                        getBlockEntity().getRecipeInventoryWrapper(),
//                        getBlockEntity().getLevel());
//                recipe.ifPresent(workstationRecipe -> resultSlots.setItem(0, workstationRecipe.assemble(this.getBlockEntity().getRecipeInventoryWrapper(), RegistryAccess.EMPTY)));
//            }
//        }
    }

    @Override
    protected ItemStack attemptToShiftIntoMenu(Player player, ItemStack returnStack, ItemStack copied, int index) {
        if (!this.moveItemStackTo(copied, 36, 37, false)) {
            return ItemStack.EMPTY;
        }
        return copied;
    }

    @Override
    public boolean stillValid(Player p_38874_) {
        return true;
    }
}
