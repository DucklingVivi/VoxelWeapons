package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.setup.Registration;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class VoxelFloorControllerBlockEntity extends BlockEntity {
    public VoxelFloorControllerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.VOXELFLOORCONTROLLERBLOCKENTITY.get(), pPos, pBlockState);
    }


    public void tickClient(){

    }

    @Override
    public AABB getRenderBoundingBox() {
        AABB originalAABB = super.getRenderBoundingBox();
        return originalAABB.inflate(2);
    }

    public void tickServer(){

    }

}
