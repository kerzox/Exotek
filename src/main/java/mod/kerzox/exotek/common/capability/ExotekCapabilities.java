package mod.kerzox.exotek.common.capability;

import mod.kerzox.exotek.common.capability.deposit.IDeposit;
import mod.kerzox.exotek.common.capability.energy.cable_impl.ILevelNetwork;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;

import static net.minecraftforge.common.capabilities.CapabilityManager.get;

public class ExotekCapabilities {

    public static final Capability<IDeposit> DEPOSIT_CAPABILITY = get(new CapabilityToken<>(){});
    public static final Capability<ILevelNetwork> LEVEL_NETWORK_CAPABILITY = get(new CapabilityToken<>(){});

}
