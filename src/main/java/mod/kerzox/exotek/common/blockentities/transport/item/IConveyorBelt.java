package mod.kerzox.exotek.common.blockentities.transport.item;

import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;
import mod.kerzox.exotek.common.entity.ConveyorBeltItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public interface IConveyorBelt<T extends AbstractConveyorBelt<?>> {

    T getBelt();
    ConveyorBeltInventory getInventory();
    boolean onConveyorBeltItemStackCollision(ConveyorBeltItemStack itemStack, IConveyorBelt<?> belt, Level level, Vec3 position);
    void onConveyorBeltItemStackPassed(ConveyorBeltItemStack itemStack, Vec3 itemStackVectorPos, IConveyorBelt<?> belt);
    IConveyorBelt<?> getNextBelt();
    boolean hasBeltInFront();
    Direction getBeltDirection();
    IConveyorCover getCoverByClickedPosition(Vec3 clickedPos);

    /**
     * 2 = NORTH
     * 3 = EAST
     * 4 = SOUTH
     * 5 = WEST
     * 0, 1 = CENTER
     * @return a Conveyor belt cover
     */
    List<IConveyorCover> getCovers();

    /**
     * 2 = NORTH
     * 3 = EAST
     * 4 = SOUTH
     * 5 = WEST
     * 0, 1 = CENTER
     * @return
     */
    boolean addCover(int index, IConveyorCover conveyorCover);
}
