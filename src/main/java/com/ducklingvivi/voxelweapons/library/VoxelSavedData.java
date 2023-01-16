package com.ducklingvivi.voxelweapons.library;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.event.WindowStateListener;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class VoxelSavedData extends SavedData {
    private final Map<UUID, VoxelData> Data;


    @Nonnull
    public static VoxelSavedData get(){
        DimensionDataStorage storage = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
        return storage.computeIfAbsent(VoxelSavedData::new,VoxelSavedData::new,"voxeldata");
    }
    public VoxelSavedData(){
        Data = new HashMap<>();
    }

    public VoxelSavedData(CompoundTag tag) {
        Data = new HashMap<>();
        ListTag listtag = tag.getList("voxelData", Tag.TAG_COMPOUND);
        for (Tag t : listtag){
            CompoundTag listitem = (CompoundTag) t;

            UUID uuid = listitem.getUUID("uuid");
            VoxelData data = VoxelData.fromNBT(ServerLifecycleHooks.getCurrentServer().overworld(),listitem.getCompound("data"));
            Data.put(uuid,data);
        }
    }
    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        ListTag listtag = new ListTag();
        Data.forEach((uuid,voxeldata)-> {
            CompoundTag listitem = new CompoundTag();
            listitem.putUUID("uuid", uuid);
            listitem.put("data", voxeldata.writeNBT());

            listtag.add(listitem);
        });

        tag.put("voxelData",listtag);
        return tag;
    }



    public VoxelData getData(UUID uuid){
        return Data.getOrDefault(uuid,new VoxelData());
    }
    public void removeData(UUID uuid){
        Data.remove(uuid);
        setDirty();
    }
    public boolean addData(UUID uuid, VoxelData data){
        if(!Data.containsKey(uuid)){
            Data.put(uuid, data);
            setDirty();
            return true;
        }
        return false;
    }
}
