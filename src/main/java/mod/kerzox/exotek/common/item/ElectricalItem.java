package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.client.gui.screen.SolarPanelScreen;
import mod.kerzox.exotek.common.capability.ExotekCapabilities;
import mod.kerzox.exotek.common.capability.energy.ForgeEnergyStorage;
import mod.kerzox.exotek.common.capability.energy.cable_impl.LevelEnergyNetwork;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class ElectricalItem extends Item {

    private int capacity;
    private int extract;
    private int insert;

    public static final String ENERGY_NBT_ITEM_TAG = "energy_item_nbt";

    public ElectricalItem(Properties p_41383_, int capacity, int extract, int insert) {
        super(p_41383_);
        this.capacity = capacity;
        this.extract = extract;
        this.insert = insert;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
        p_41433_.getMainHandItem().getCapability(ForgeCapabilities.ENERGY).ifPresent(cap -> cap.receiveEnergy(1000, false));
        return super.use(p_41432_, p_41433_, p_41434_);
    }

    @Override
    public boolean isBarVisible(ItemStack p_150899_) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_) {
        Optional<IEnergyStorage> storage = p_41421_.getCapability(ForgeCapabilities.ENERGY).resolve();
        storage.ifPresent(cap -> {
            {
                p_41423_.add(Component.literal("Energy: " + SolarPanelScreen.abbreviateNumber(cap.getEnergyStored(), true)
                        + "/" + SolarPanelScreen.abbreviateNumber(cap.getMaxEnergyStored(), true)));
            }
        });
        super.appendHoverText(p_41421_, p_41422_, p_41423_, p_41424_);
    }


    @Override
    public int getBarWidth(ItemStack p_150900_) {
        Optional<IEnergyStorage> storage = p_150900_.getCapability(ForgeCapabilities.ENERGY).resolve();
        int energy = 13 * storage.map(IEnergyStorage::getEnergyStored).orElse(0)
                / storage.map(IEnergyStorage::getMaxEnergyStored).orElse(0);
        return Math.round(energy);
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new ICapabilityProvider() {

//            @Override
//            public CompoundTag serializeNBT() {
//                CompoundTag tag = new CompoundTag();
//                tag.put("energy", storage.serializeNBT());
//                return tag;
//            }
//
//            @Override
//            public void deserializeNBT(CompoundTag nbt) {
//                storage.read(nbt);
//            }



            private ForgeEnergyStorage storage = new ForgeEnergyStorage(capacity, extract, insert) {

                @Override
                public int getEnergyStored() {
                    deserializeNBT(stack.getTag().getCompound(ENERGY_NBT_ITEM_TAG).get("energy"));
                    return super.getEnergyStored();
                }

                public int getEnergyStoredServer() {
                    return super.getEnergyStored();
                }

                @Override
                public Tag serializeNBT() {
                    return IntTag.valueOf(this.getEnergyStoredServer());
                }

                @Override
                public void deserializeNBT(Tag nbt) {
                    if (nbt instanceof IntTag) super.deserializeNBT(nbt);
                }

                @Override
                protected void onContentsChanged() {
                    CompoundTag energy = new CompoundTag();
                    energy.put("energy", serializeNBT());
                    stack.getOrCreateTag().put(ENERGY_NBT_ITEM_TAG, energy);
                }
            };
            private LazyOptional<ForgeEnergyStorage> handler = LazyOptional.of(() -> storage);

            @Override
            public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
                return ForgeCapabilities.ENERGY.orEmpty(cap, handler.cast());
            }

        };
    }
}
