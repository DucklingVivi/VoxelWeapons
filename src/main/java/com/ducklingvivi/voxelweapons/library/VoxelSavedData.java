package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.event.WindowStateListener;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class VoxelSavedData extends SavedData {
    private final Map<UUID, VoxelData> Data;

    private final Map<UUID, Integer> DimensionMap;


    @Nonnull
    public static VoxelSavedData get(){
        DimensionDataStorage storage = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
        return storage.computeIfAbsent(VoxelSavedData::new,VoxelSavedData::new,"voxeldata");
    }
    public VoxelSavedData(){
        Data = new HashMap<>();
        DimensionMap = new HashMap<>();
    }

    public VoxelSavedData(CompoundTag tag) {
        Data = new HashMap<>();
        DimensionMap = new HashMap<>();
        if(!tag.contains("voxelData") || !tag.contains("dimensionMap")) return;
        ListTag listtag = tag.getList("voxelData", Tag.TAG_COMPOUND);
        for (Tag t : listtag){
            CompoundTag listitem = (CompoundTag) t;

            UUID uuid = listitem.getUUID("uuid");
            VoxelData data = VoxelData.fromNBT(ServerLifecycleHooks.getCurrentServer().overworld(),listitem.getCompound("data"));
            Data.put(uuid,data);
        }
        ListTag listtag2 = tag.getList("dimensionMap", Tag.TAG_COMPOUND);
        for (Tag t: listtag2) {
            CompoundTag listitem = (CompoundTag) t;
            Integer integer = listitem.getInt("integer");
            UUID uuid = listitem.getUUID("uuid");
            DimensionMap.put(uuid,integer);
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
        ListTag listtag2 = new ListTag();
        DimensionMap.forEach(((uuid,integer) -> {
            CompoundTag listitem = new CompoundTag();
            listitem.putInt("integer", integer);
            listitem.putUUID("uuid", uuid);
            listtag2.add(listitem);
        }));
        tag.put("dimensionMap",listtag2);
        return tag;
    }


    public ServerLevel CreateDimension(UUID uuid){
        boolean flag = false;
        Integer x = 0;
        while(!flag){
            if(DimensionMap.containsValue(x)){
                x++;
            }else{
                flag = true;
            }
        }
        DimensionMap.put(uuid, x);
        setDirty(true);
        return DimensionUtils.createWorld(ServerLifecycleHooks.getCurrentServer(),x.toString());
    }
    public void DeleteDimension(UUID uuid){
        if(DimensionMap.containsKey(uuid)){
            Integer dimensionid = DimensionMap.remove(uuid);
            DimensionUtils.deleteWorld(ServerLifecycleHooks.getCurrentServer(),dimensionid.toString());
            setDirty(true);
        }
    }
    public void DeleteDimension(Integer integer){
        if(DimensionMap.containsValue(integer)){
            for (Map.Entry<UUID,Integer> entry  :DimensionMap.entrySet()) {
                if(entry.getValue().equals(integer)){
                    DeleteDimension(entry.getKey());
                    return;
                }
            }
        }
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
