package com.ducklingvivi.voxelweapons.library;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;


public class VoxelHandler implements VoxelBase {

    public static Capability<VoxelHandler> CAPABILITY = CapabilityManager.get(new CapabilityToken<>(){});
    private VoxelData voxeldata = new VoxelData();

    public CompoundTag serializeNBT() {
        return voxeldata.toCompound();
    }

    public void deserializeNBT(CompoundTag nbt) {
        voxeldata.fromCompound(nbt);
    }

    @Override
    public VoxelData getVoxelData() {
        return voxeldata;
    }

    @Override
    public void setVoxelData(VoxelData data) {
        voxeldata = data;
    }
}
