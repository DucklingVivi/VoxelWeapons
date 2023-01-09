package com.ducklingvivi.voxelweapons.library;

import net.minecraft.nbt.CompoundTag;


import java.util.ArrayList;
import java.util.List;

public class VoxelData {

    public VoxelData(){

    }
    private List<Voxel> voxels;


    public List<Voxel> getVoxels(){
        return voxels;
    }

    public void setVoxels(List<Voxel> voxels){
        this.voxels = voxels;
    }

    public void copyFrom(VoxelData source){
        this.voxels = source.voxels;
    }

    public void saveNBTData(CompoundTag compound){
        CompoundTag listCompound = new CompoundTag();
        listCompound.putInt("size",voxels.size());
        for (Integer i = 0; i < voxels.size(); i++) {
            listCompound.put(i.toString(),voxels.get(i).toCompound());
        }
        compound.put("voxels",listCompound);
    }
    public void loadNBTData(CompoundTag compound){
        List<Voxel> blockStates = new ArrayList<>();
        CompoundTag listCompound = compound.getCompound("voxels");
        int size = listCompound.getInt("size");
        for (Integer i = 0; i < size; i++) {
           blockStates.add(Voxel.fromCompound(listCompound.getCompound(i.toString())));
        }
        voxels = blockStates;
    }
}
