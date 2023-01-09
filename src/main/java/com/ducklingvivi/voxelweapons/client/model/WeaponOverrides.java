package com.ducklingvivi.voxelweapons.client.model;


import com.ducklingvivi.voxelweapons.library.Messages;
import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class WeaponOverrides extends ItemOverrides {
    public WeaponOverrides(){
        super();
    }


    @Nullable
    @Override
    public BakedModel resolve(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int value) {

        //TODO SWITCH TO CAPABILITIES
        VoxelData data = new VoxelData();
        if(stack.hasTag()) {
            CompoundTag tag = stack.getTag();
            if (tag.contains("VoxelData")) {
                data.loadNBTData(tag.getCompound("VoxelData"));
            }
        }
        return new WeaponBakedModelFinalized(originalModel, data);
    }
}
