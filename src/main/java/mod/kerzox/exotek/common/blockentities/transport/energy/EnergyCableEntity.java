package mod.kerzox.exotek.common.blockentities.transport.energy;

import mod.kerzox.exotek.client.gui.menu.transfer.EnergyCableMenu;
import mod.kerzox.exotek.common.block.transport.EnergyCableBlock;
import mod.kerzox.exotek.common.blockentities.ContainerisedBlockEntity;
import mod.kerzox.exotek.registry.Registry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnergyCableEntity extends ContainerisedBlockEntity {

    private HashMap<Direction, EnergyCableBlock.Connection> connectedSides = new HashMap<>(Map.of(
            Direction.NORTH, EnergyCableBlock.Connection.NONE,
            Direction.SOUTH, EnergyCableBlock.Connection.NONE,
            Direction.EAST, EnergyCableBlock.Connection.NONE,
            Direction.WEST, EnergyCableBlock.Connection.NONE,
            Direction.UP, EnergyCableBlock.Connection.NONE,
            Direction.DOWN, EnergyCableBlock.Connection.NONE));

    public EnergyCableEntity(BlockPos pos, BlockState state) {
        super(Registry.BlockEntities.ENERGY_CABLE_ENTITY.get(), pos, state);
    }

    public void addVisualConnection(Direction facing) {
        connectedSides.put(facing, EnergyCableBlock.Connection.CONNECTED);
        syncBlockEntity();
    }
    public void removeVisualConnection(Direction facing) {
        connectedSides.put(facing, EnergyCableBlock.Connection.NONE);
        syncBlockEntity();
    }

    public HashMap<Direction, EnergyCableBlock.Connection> getConnectedSides() {
        return connectedSides;
    }

    public Set<Direction> getConnectionsAsDirections() {
        HashSet<Direction> directions = new HashSet<>();
        connectedSides.forEach((d, c) -> {
            if (c == EnergyCableBlock.Connection.CONNECTED) directions.add(d);
        });
        return directions;
    }

    @Override
    protected void write(CompoundTag pTag) {
        ListTag directions = new ListTag();
        connectedSides.forEach((d, c) -> {
            CompoundTag tag = new CompoundTag();
            tag.putString("direction", d.getSerializedName());
            tag.putBoolean("connection", c == EnergyCableBlock.Connection.CONNECTED);
            directions.add(tag);
        });
        pTag.put("pipe_connections", directions);
    }

    @Override
    protected void read(CompoundTag pTag) {
        ListTag list = pTag.getList("pipe_connections", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag listItem = list.getCompound(i);
            connectedSides.put(Direction.byName(listItem.getString("direction")),
                    listItem.getBoolean("connection") ? EnergyCableBlock.Connection.CONNECTED : EnergyCableBlock.Connection.NONE);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Cable Config");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_39954_, Inventory p_39955_, Player p_39956_) {
        return new EnergyCableMenu(p_39954_, p_39955_, p_39956_, this);
    }
}
