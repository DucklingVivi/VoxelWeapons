package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ColorResolver;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.LevelLightEngine;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.Nullable;

public class VoxelTintAndBlockGetter implements BlockAndTintGetter {

    ClientLevel level;
    VoxelData data;
    public VoxelTintAndBlockGetter(ClientLevel level , VoxelData data){
        this.level = level;
        this.data = data;
    }
    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return level.getShade(p_45522_,p_45523_);
    }

    @Override
    public LevelLightEngine getLightEngine() {
        return level.getLightEngine();
    }

    @Override
    public int getBlockTint(BlockPos p_45520_, ColorResolver p_45521_) {
        return level.getBlockTint(p_45520_,p_45521_);
    }

    @Nullable
    @Override
    public BlockEntity getBlockEntity(BlockPos p_45570_) {
        return data.presentTileEntities.get(p_45570_);
    }

    @Override
    public BlockState getBlockState(BlockPos p_45571_) {
        if(data.devGetBlocks().containsKey(p_45571_)){
            return data.devGetBlocks().get(p_45571_).state;
        }
        return Blocks.AIR.defaultBlockState();

    }

    @Override
    public FluidState getFluidState(BlockPos p_45569_) {
        return data.devGetFluids().getOrDefault(p_45569_, Fluids.EMPTY.defaultFluidState());
    }

    @Override
    public int getHeight() {
        return level.getHeight();
    }

    @Override
    public int getMinBuildHeight() {
        return level.getMinBuildHeight();
    }
}
