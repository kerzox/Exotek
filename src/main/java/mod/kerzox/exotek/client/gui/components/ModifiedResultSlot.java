package mod.kerzox.exotek.client.gui.components;

import mod.kerzox.exotek.client.gui.menu.container.ItemStackCraftingContainer;
import mod.kerzox.exotek.common.crafting.RecipeInventoryWrapper;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

public class ModifiedResultSlot<T extends Recipe<RecipeInventoryWrapper>> extends ContainerSlotComponent {

    private final ItemStackCraftingContainer craftSlots;
    private final Player player;
    private int removeCount;
    private RecipeType<T> recipeType;

    public ModifiedResultSlot(RecipeType<T> recipeType, Player player, ItemStackCraftingContainer craftSlots, Container p_40168_, int p_40224_, int p_40225_, int p_40226_) {
        super(p_40168_, p_40224_, p_40225_, p_40226_);
        this.craftSlots = craftSlots;
        this.player = player;
        this.recipeType = recipeType;
    }

    public boolean mayPlace(ItemStack p_40178_) {
        return false;
    }

    public ItemStack remove(int p_40173_) {
        if (this.hasItem()) {
            this.removeCount += Math.min(p_40173_, this.getItem().getCount());
        }

        return super.remove(p_40173_);
    }

    protected void onQuickCraft(ItemStack p_40180_, int p_40181_) {
        this.removeCount += p_40181_;
        this.checkTakeAchievements(p_40180_);
    }

    protected void onSwapCraft(int p_40183_) {
        this.removeCount += p_40183_;
    }

    protected void checkTakeAchievements(ItemStack p_40185_) {
        if (this.removeCount > 0) {
            p_40185_.onCraftedBy(this.player.level(), this.player, this.removeCount);
            net.minecraftforge.event.ForgeEventFactory.firePlayerCraftingEvent(this.player, p_40185_, this.craftSlots);
        }

        Container container = this.container;
        if (container instanceof RecipeHolder recipeholder) {
            recipeholder.awardUsedRecipes(this.player, this.craftSlots.getItems());
        }

        this.removeCount = 0;
    }

    public void onTake(Player p_150638_, ItemStack p_150639_) {
        this.checkTakeAchievements(p_150639_);
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(p_150638_);
        NonNullList<ItemStack> nonnulllist = p_150638_.level().getRecipeManager().getRemainingItemsFor(recipeType, new RecipeInventoryWrapper(craftSlots.getItemHandlerModifiable()), p_150638_.level());
        net.minecraftforge.common.ForgeHooks.setCraftingPlayer(null);
        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = this.craftSlots.getItem(i);
            ItemStack itemstack1 = nonnulllist.get(i);
            if (!itemstack.isEmpty()) {
                this.craftSlots.removeItem(i, 1);
                itemstack = this.craftSlots.getItem(i);
            }

            if (!itemstack1.isEmpty()) {
                if (itemstack.isEmpty()) {
                    this.craftSlots.setItem(i, itemstack1);
                } else if (ItemStack.isSameItemSameTags(itemstack, itemstack1)) {
                    itemstack1.grow(itemstack.getCount());
                    this.craftSlots.setItem(i, itemstack1);
                } else if (!this.player.getInventory().add(itemstack1)) {
                    this.player.drop(itemstack1, false);
                }
            }
        }

    }
}
