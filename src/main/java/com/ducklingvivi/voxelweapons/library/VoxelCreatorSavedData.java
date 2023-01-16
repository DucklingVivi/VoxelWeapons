package com.ducklingvivi.voxelweapons.library;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.UUID;

public class VoxelCreatorSavedData extends SavedData {

    private BlockPos origin = BlockPos.ZERO;

    @Nonnull
    public static VoxelCreatorSavedData get(ServerLevel level){
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(VoxelCreatorSavedData::new,VoxelCreatorSavedData::new,"voxelCreatorData");
    }

    public VoxelCreatorSavedData(){

    }

    public VoxelCreatorSavedData(CompoundTag tag) {
        origin = NbtUtils.readBlockPos(tag.getCompound("Origin"));
    }
    @Override
    public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.put("Origin", NbtUtils.writeBlockPos(origin));
        return pCompoundTag;
    }


    public void setOrigin(BlockPos origin) {
        this.origin = origin;
        setDirty(true);
    }
    public BlockPos getOrigin(){
        return this.origin;
    }
}
