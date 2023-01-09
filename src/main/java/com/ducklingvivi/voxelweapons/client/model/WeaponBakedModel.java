package com.ducklingvivi.voxelweapons.client.model;



import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.math.Transformation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.extensions.IForgeBakedModel;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.data.ModelData;

import net.minecraftforge.client.model.QuadTransformers;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.*;
import java.util.function.Function;

public class WeaponBakedModel implements IDynamicBakedModel {

    public final ModelState modelState;
    private final Function<Material, TextureAtlasSprite> spriteGetter;
    private WeaponOverrides weaponOverrides;
    public static final ModelResourceLocation modelResourceLocation
            = new ModelResourceLocation(Registration.VOXELWEAPONITEM.getId(), "inventory");

    public WeaponBakedModel(){
        this.modelState = new ModelState(){};
        this.spriteGetter = material -> material.sprite();
        weaponOverrides = new WeaponOverrides();
    }
    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData extraData, @Nullable RenderType renderType) {
        return Collections.emptyList();
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
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return spriteGetter.apply(ForgeHooksClient.getBlockMaterial(new ResourceLocation(voxelweapons.MODID, "block/egg")));
    }

    @Override
    public ItemOverrides getOverrides() {
        return weaponOverrides;
    }
}
