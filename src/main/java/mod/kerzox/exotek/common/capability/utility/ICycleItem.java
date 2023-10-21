package mod.kerzox.exotek.common.capability.utility;

import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ICycleItem {

    void cycle(boolean reverse);

}
