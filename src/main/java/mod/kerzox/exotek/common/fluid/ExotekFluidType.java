package mod.kerzox.exotek.common.fluid;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;

import java.util.function.Consumer;

public class ExotekFluidType extends FluidType {

    private final ResourceLocation stillTexture;
    private final ResourceLocation flowingTexture;
    private ResourceLocation overlayTexture;
    private ResourceLocation viewOverlayTexture;
    private boolean gas;
    private final int colour;

    public ExotekFluidType(Properties properties,
                           ResourceLocation stillTexture,
                           ResourceLocation flowingTexture,
                           ResourceLocation overlayTexture,
                           ResourceLocation viewOverlayTexture,
                           int colour) {
        super(properties);
        this.stillTexture = stillTexture;
        this.flowingTexture = flowingTexture;
        this.overlayTexture = overlayTexture;
        this.viewOverlayTexture = viewOverlayTexture;
        this.colour = colour;
    }

    public ExotekFluidType(Properties properties, ResourceLocation stillTexture, ResourceLocation flowingTexture, int colour, boolean gas) {
        this(properties, stillTexture, flowingTexture, null, null, colour);
        this.gas = gas;
    }

    /**
     * use this if you just want a tinted water fluid ;)
     * @param tint
     * @return fluid type
     */

    public static ExotekFluidType createColoured(Properties properties, int tint, boolean gas) {
        return new ExotekFluidType(properties,
                new ResourceLocation("block/water_still"),
                new ResourceLocation("block/water_flowing"),
                tint, gas);
    }

    public static ExotekFluidType createColoured(int tint, boolean gas) {
        return new ExotekFluidType(Properties.create(),
                new ResourceLocation("block/water_still"),
                new ResourceLocation("block/water_flowing"),
                tint, gas);
    }


    public boolean isGaseous() {
        return gas;
    }

    @Override
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            @Override
            public ResourceLocation getStillTexture()
            {
                return stillTexture;
            }

            @Override
            public ResourceLocation getFlowingTexture()
            {
                return flowingTexture;
            }

            @Override
            public ResourceLocation getOverlayTexture()
            {
                return overlayTexture;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(Minecraft mc)
            {
                return viewOverlayTexture;
            }

            @Override
            public int getTintColor()
            {
                return colour;
            }

            public boolean isGaseous() {
                return gas;
            }
        });
    }
}
