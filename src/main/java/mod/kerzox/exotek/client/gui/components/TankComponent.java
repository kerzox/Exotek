package mod.kerzox.exotek.client.gui.components;

import com.mojang.blaze3d.systems.RenderSystem;
import mod.kerzox.exotek.client.gui.menu.DefaultMenu;
import mod.kerzox.exotek.client.gui.screen.ICustomScreen;
import mod.kerzox.exotek.client.render.RenderingUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;

import java.util.List;
import java.util.Optional;

public class TankComponent extends TexturedWidgetComponent {

    private TextureAtlasSprite sprite;
    private float percentage;
    private int color;
    private IFluidTank tank;
    private boolean flash = false;

    private int u1, v1, u2, v2;

    public TankComponent(ICustomScreen screen, ResourceLocation texture, IFluidTank tank, int x, int y, int width, int height, int u1, int v1, int u2, int v2, Component component) {
        super(screen, x, y, width, height, u1, v1, texture, component);
        setTextureOffset(u1, v1);
        this.tank = tank;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    public TankComponent(ICustomScreen screen, ResourceLocation texture, IFluidTank tank, int x, int y, int width, int height, int u1, int v1, int u2, int v2) {
        super(screen, x, y, width, height, u1, v1, texture, Component.translatable(tank.getFluid().getTranslationKey()));
        setTextureOffset(u1, v1);
        this.tank = tank;
        this.u1 = u1;
        this.v1 = v1;
        this.u2 = u2;
        this.v2 = v2;
    }

    @Override
    public void tick() {
        updateState();
    }

    public void updateState() {
        if (tank != null) {
            FluidStack fluidStack = tank.getFluid();
            if (!fluidStack.isEmpty()) {
                setSpriteFromFluidStack(fluidStack);

            }
            update(fluidStack.getAmount(), tank.getCapacity());
        }
    }

    public IFluidTank getTank() {
        return tank;
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
        if (!getTank().getFluid().isEmpty()) {
            graphics.renderTooltip(Minecraft.getInstance().font,
                    List.of(Component.translatable(this.getTank().getFluid().getTranslationKey()), Component.literal("Fluid Amount:" +
                            (!this.getTank().getFluid().isEmpty() ? String.format("%, .0f",
                            Double.parseDouble(String.valueOf(this.getTank().getFluid().getAmount()))) + " mB" : 0))), Optional.empty(), ItemStack.EMPTY, pMouseX, pMouseY);

        }
    }


}
