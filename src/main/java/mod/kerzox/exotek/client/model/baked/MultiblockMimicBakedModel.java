package mod.kerzox.exotek.client.model.baked;

import mod.kerzox.exotek.client.render.RenderingUtil;
import mod.kerzox.exotek.common.block.MultiblockBlock;
import mod.kerzox.exotek.common.blockentities.multiblock.MultiblockInvisibleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.MultiPartBakedModel;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.registries.ForgeRegistries;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiblockMimicBakedModel implements IDynamicBakedModel {

    private final Map<BlockState, BakedModel> cachedCamo = new HashMap<>();
    private BakedModel model;

    public MultiblockMimicBakedModel(BakedModel bakedModel) {
        this.model = bakedModel;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
//        List<BakedQuad> quads = new ArrayList<>(new ArrayList<>(this.getCamoModelFromData(extraData).getQuads(state, side, rand)));
        List<BakedQuad> quads = getCamoModelFromData(extraData, state, rand, side);
        if (quads.isEmpty()) this.model.getQuads(state, side, rand);
        return quads;
    }

    private List<BakedQuad> getCamoModelFromData(ModelData data, @Nullable BlockState state, @NotNull RandomSource rand, @Nullable Direction side) {
        BlockState mimic = data.get(MultiblockInvisibleEntity.MIMIC);
        if (mimic != null && !(mimic.getBlock() instanceof MultiblockBlock)) {
            if (!Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic).getQuads(state, side, rand).isEmpty()) {
                TextureAtlasSprite sprite = Minecraft.getInstance().getBlockRenderer().getBlockModel(mimic).getQuads(state, side, rand).get(0).getSprite();
                return RenderingUtil.bakedQuadList(0, 0, 0, 16f / 16, 16f / 16, 16f / 16,
                        0, 0, 16, 16, sprite
                        ,
                        0x7A7A7A);
            }
        }
        return new ArrayList<>();
    }


    @Override
    public boolean useAmbientOcclusion() {
        return model.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return model.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return model.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return model.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return model.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        return model.getOverrides();
    }


}
