package com.ducklingvivi.voxelweapons.client.render;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;

import javax.lang.model.util.Elements;

public class VoxelCreatorClientData {

    public static VoxelCreatorClientData INSTANCE = new VoxelCreatorClientData();
    public boolean isControllerVisible;
    private BlockPos origin;
    private AABB boundingBox;
    public VoxelCreatorClientData(){
        origin = BlockPos.ZERO;
        boundingBox = new AABB(origin);
        isControllerVisible=false;
    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
    }

    public BlockPos getOrigin() {
        return origin;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(AABB boundingBox) {
        this.boundingBox = boundingBox;
    }
}
