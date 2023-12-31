package mod.kerzox.exotek.common.capability;

import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.IEnergyCapabilityLevelNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.IFluidCapabilityLevelNetwork;
import mod.kerzox.exotek.common.capability.energy.cable_impl.ILevelNetwork;
import mod.kerzox.exotek.common.capability.upgrade.IUpgradableMachine;
import mod.kerzox.exotek.common.capability.utility.ICycleItem;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class ExotekCapabilities {

    public static final Capability<IDeposit> DEPOSIT_CAPABILITY = get(new CapabilityToken<>(){});
    public static final Capability<ILevelNetwork> LEVEL_NETWORK_CAPABILITY = get(new CapabilityToken<>(){});
    public static final Capability<IUpgradableMachine> UPGRADABLE_MACHINE = get(new CapabilityToken<>(){});
    public static final Capability<IEnergyCapabilityLevelNetwork> ENERGY_LEVEL_NETWORK_CAPABILITY = get(new CapabilityToken<>(){});
    public static final Capability<IFluidCapabilityLevelNetwork> FLUID_LEVEL_NETWORK_CAPABILITY = get(new CapabilityToken<>(){});
    public static final Capability<ICycleItem> CYCLE_ITEM_CAPABILITY = get(new CapabilityToken<>(){});
}
