package mod.kerzox.exotek.common.block;

import mod.kerzox.exotek.client.gui.screen.SolarPanelScreen;
import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import mod.kerzox.exotek.registry.ConfigConsts;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EnergyCellBlock extends Block {

    private CapabilityTiers tiers;

    public EnergyCellBlock(Properties p_49795_, CapabilityTiers tiers) {
        super(p_49795_);
        this.tiers = tiers;
    }

    @Override
    public void appendHoverText(ItemStack p_49816_, @Nullable BlockGetter p_49817_, List<Component> p_49818_, TooltipFlag p_49819_) {
        p_49818_.add(Component.literal("Capacity: " + SolarPanelScreen.abbreviateNumber(getCapacity(), true)));
    }

    public int getCapacity() {
        if (tiers == CapabilityTiers.BASIC) {
            return ConfigConsts.BATTERY_CAPACITY * 5;
        }
        else if (tiers == CapabilityTiers.ADVANCED) {
            return (ConfigConsts.BATTERY_CAPACITY * 5) * 4;
        }
        else if (tiers == CapabilityTiers.HYPER) {
            return ((ConfigConsts.BATTERY_CAPACITY * 5) * 4) * 4;
        }
        return 0;
    }

    public CapabilityTiers getTiers() {
        return tiers;
    }
}
