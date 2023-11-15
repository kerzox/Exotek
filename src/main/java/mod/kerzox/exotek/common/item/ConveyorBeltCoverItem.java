package mod.kerzox.exotek.common.item;

import mod.kerzox.exotek.common.blockentities.transport.item.covers.IConveyorCover;

public class ConveyorBeltCoverItem extends ExotekItem {

    private IConveyorCover implementation;

    public ConveyorBeltCoverItem(Properties p_41383_, IConveyorCover implementation) {
        super(p_41383_);
        this.implementation = implementation;
    }

    /**
     * This creates a new cover object
     * @return
     */
    public IConveyorCover getCover() {
        IConveyorCover cover = implementation.create(this);
        cover.setItem(this);
        return cover;
    }
}
