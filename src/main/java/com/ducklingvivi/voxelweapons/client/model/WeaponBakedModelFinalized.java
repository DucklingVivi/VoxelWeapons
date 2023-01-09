package com.ducklingvivi.voxelweapons.client.model;



import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.AtlasSet;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;

import net.minecraftforge.client.model.QuadTransformers;

import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;


public class WeaponBakedModelFinalized implements IDynamicBakedModel {

    private BakedModel parentModel;
    private VoxelData data;

    public WeaponBakedModelFinalized(BakedModel parentModel, VoxelData data){
        this.parentModel = parentModel;
        this.data = data;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();

        Direction facing = state == null ? Direction.SOUTH : state.getValue(BlockStateProperties.FACING);


        Transformation translate = transformBlock(facing);

        IQuadTransformer transformer = QuadTransformers.applying(translate);
        BlockState blockState = Blocks.BONE_BLOCK.defaultBlockState();
        if(data != null){
            if(data.getVoxels() != null) {
                if (data.getVoxels().size() > 0) {
                    blockState = data.getVoxels().get(0).blockState;
                }
            }
        }


        IForgeBakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(blockState);

        List<BakedQuad> modelQuads = model.getQuads(state, side, rand, ModelData.builder().build(), null);

        TextureAtlasSprite sprite = null;


        //new Material(new ResourceLocation(data.resourceMap.get(0)))


        for (BakedQuad quad : modelQuads) {
            quads.add(transformer.process(quad));
        }

        return quads;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return null;
    }

    private Transformation transformBlock(Direction facing){
        Transformation translate = new Transformation(new Matrix4f().translate(0.5f,0.5f,0.5f));
        translate = translate.compose(new Transformation(new Matrix4f().scale(.2f,.2f,.2f)));
        translate = translate.compose(new Transformation(new Matrix4f().translate(-0.5f,-0.5f,-0.5f)));
        return translate;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return parentModel.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return parentModel.getParticleIcon();
    }

    @Override
    public ItemOverrides getOverrides() {
        throw new UnsupportedOperationException("The finalised model does not have an override list.");
    }
}
