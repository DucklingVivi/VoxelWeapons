package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.LevelLightEngine;

public class VoxelLightEngine extends LevelLightEngine {
    VoxelData data;


    public VoxelLightEngine(LightChunkGetter p_75805_, boolean p_75806_, boolean p_75807_) {
        super(p_75805_, p_75806_, p_75807_);
        this.data = new VoxelData();
    }

    @Override
    public void checkBlock(BlockPos p_9357_) {
        super.checkBlock(p_9357_);
    }
    public void setData(VoxelData data){
        this.data = data;
    }
}


