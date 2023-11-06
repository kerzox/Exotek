package mod.kerzox.exotek.common.capability.energy.cable_impl;

import mod.kerzox.exotek.common.blockentities.transport.CapabilityTiers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ILevelSubNetwork {

    void tick();
    void attach(LevelNode node);
    void attach(BlockPos pos);
    void detach(BlockPos clickedPos);
    boolean isInNetwork(BlockPos pos);
    NodeList getNetwork();
    int size();
    Level getLevel();
    LevelNode getNodeByPosition(BlockPos pos);
    CapabilityTiers getTier();
}
