package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.render.VoxelCreatorClientData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class VoxelFloorControllerBlockEntity extends BlockEntity {
    public VoxelFloorControllerBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(Registration.VOXELFLOORCONTROLLERBLOCKENTITY.get(), pPos, pBlockState);
    }


    public void tickClient(){
        if(this.worldPosition.distToCenterSqr(Minecraft.getInstance().player.position())<2){
            VoxelCreatorClientData.INSTANCE.isControllerVisible = true;
        }

    }
    public void tickServer(){

    }

}
