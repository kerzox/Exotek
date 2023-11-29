package mod.kerzox.exotek.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.common.network.FluidTankClick;
import mod.kerzox.exotek.common.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.List;
import java.util.Optional;

public class TankComponent extends TexturedWidgetComponent {

    private TextureAtlasSprite sprite;
    private float percentage;
    private int color;
    private IFluidHandler handler;
    private boolean flash = false;
    private int index = 0;

    private int u1, v1, u2, v2;

    public TankComponent(ICustomScreen screen, ResourceLocation texture, IFluidHandler tank, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Component component) {
        super(screen, x, y, width, height, u1, v1, texture, component);
        setTextureOffset(u1, v1);
        this.handler = tank;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public TankComponent(ICustomScreen screen, ResourceLocation texture, IFluidHandler handler, int index, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
        super(screen, x, y, width, height, u1, v1, texture, Component.translatable(handler.getFluidInTank(index).getTranslationKey()));
        setTextureOffset(u1, v1);
        this.handler = handler;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.index = index;
        this.v2 = v2;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        PacketHandler.sendToServer(new FluidTankClick(index));
    }

    @Override
    public void tick() {
        updateState();
    }

    public void updateState() {
        if (handler != null) {
            FluidStack fluidStack = handler.getFluidInTank(index);
            if (!fluidStack.isEmpty()) {
                setSpriteFromFluidStack(fluidStack);

            }
            update(fluidStack.getAmount(), handler.getTankCapacity(index));
        }
    }

    public IFluidHandler getHandler() {
        return handler;
    }

    public void setSpriteFromFluidStack(FluidStack fluidStack) {
        IClientFluidTypeExtensions clientStuff = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        sprite = Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(clientStuff.getStillTexture());
        color = clientStuff.getTintColor();
        setTextureOffset(this.u2, this.v2);
    }

    public void update(int amount, int capacity) {
        percentage = ((float) amount / capacity);
    }

    @Override
    public void drawComponent(GuiGraphics graphics, int pMouseX, int pMouseY, float pPartialTick) {
        graphics.pose().pushPose();
        if (sprite != null) {
            RenderSystem.setShaderColor(RenderingUtil.convertColor(color)[0],
                    RenderingUtil.convertColor(color)[1],
                    RenderingUtil.convertColor(color)[2],
                    1f);
            RenderingUtil.drawSpriteGrid(graphics, this.getCorrectX() + 1, this.getCorrectY() + 1,
                    sprite.contents().width(), sprite.contents().height(),
                    sprite,
                    (this.width + 1) / 16, (this.height + 1) / 16);
            RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
            RenderSystem.setShader(GameRenderer::getPositionTexShader);


            float size = this.height * percentage;
            this.setTextureOffset(this.u1, this.v1);
            this.draw(graphics, this.getCorrectX(), this.getCorrectY(), this.width, (int) (this.height - size));
            this.setTextureOffset(this.u2, this.v2);
        }
        this.draw(graphics, this.getCorrectX(), this.getCorrectY(), this.width, this.height);
        graphics.pose().popPose();
    }

    public void onHover(GuiGraphics graphics, int pMouseX, int pMouseY, float partialTicks) {
        if (!getHandler().getFluidInTank(0).isEmpty()) {
            graphics.renderTooltip(Minecraft.getInstance().font,
                    List.of(this.getHandler().getFluidInTank(index).isEmpty() ? Component.literal("Empty") : Component.translatable(this.getHandler().getFluidInTank(index).getTranslationKey()), Component.literal("Fluid Amount:" +
                            (!this.getHandler().getFluidInTank(index).isEmpty() ? String.format("%, .0f",
                            Double.parseDouble(String.valueOf(this.getHandler().getFluidInTank(index).getAmount()))) + " mB" : 0))), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);

        }
    }


}
