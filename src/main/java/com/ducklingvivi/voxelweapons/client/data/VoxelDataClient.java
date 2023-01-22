package com.ducklingvivi.voxelweapons.client.data;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.networking.WeaponRequestPacket;
import net.minecraft.client.renderer.block.model.BakedQuad;

import java.util.*;

public class VoxelDataClient {

    public static final int REMOVE_TICK_TIME = 500;
    public static final int REQUEST_TICK_TIME = 100;
    public static Map<UUID, VoxelData> voxelDataMap = new HashMap<>();
    public static Map<UUID, VoxelRenderData> voxelRenderDataMap = new HashMap<>();
    public static Map<UUID, Integer> voxelRemoveMap = new HashMap<>();
    public static  Map<UUID, Integer> voxelRequestMap = new HashMap<>();


    public static void addRenderData(UUID uuid, VoxelRenderData data){
        voxelRenderDataMap.put(uuid, data);
    }
    public static void removeRenderData(UUID uuid){
        voxelRenderDataMap.remove(uuid);
    }
    public static Optional<VoxelRenderData> getRenderData(UUID uuid){
        VoxelRenderData data = voxelRenderDataMap.get(uuid);
        if(data != null){
            return Optional.of(data);
        }
        return Optional.empty();
    }
    public static void addData(UUID uuid, VoxelData data){
        voxelDataMap.put(uuid,data);
        voxelRemoveMap.put(uuid,REMOVE_TICK_TIME);
    }
    public static void removeData(UUID uuid){
        voxelDataMap.remove(uuid);
        voxelRemoveMap.remove(uuid);
        voxelRenderDataMap.remove(uuid);
    }

    public static Optional<VoxelData> getData(UUID uuid) {
        voxelRemoveMap.put(uuid, REMOVE_TICK_TIME);
        if(voxelDataMap.containsKey(uuid)){
            return Optional.of(voxelDataMap.get(uuid));
        }
        if(voxelRequestMap.containsKey(uuid)){
            return Optional.empty();
        }
        voxelRequestMap.put(uuid, REQUEST_TICK_TIME);
        Messages.sendToServer(new WeaponRequestPacket(uuid));
        return Optional.empty();
    }

    public static void tickAll(){
        for (UUID uuid: new HashSet<>(voxelRemoveMap.keySet())) {
            tickremove(uuid);
        }
        for (UUID uuid: new HashSet<>(voxelRequestMap.keySet())) {
            tickrequest(uuid);
        }

    }
    private static void tickremove(UUID uuid){
        voxelRemoveMap.put(uuid, voxelRemoveMap.getOrDefault(uuid,REMOVE_TICK_TIME)-1);
        if(voxelRemoveMap.get(uuid) < 0){
            voxelRenderDataMap.remove(uuid);
            voxelDataMap.remove(uuid);
            voxelRemoveMap.remove(uuid);
        }
    }
    private static void tickrequest(UUID uuid){
        voxelRequestMap.put(uuid, voxelRequestMap.getOrDefault(uuid,REQUEST_TICK_TIME)-1);
        if(voxelRequestMap.get(uuid) < 0){
            voxelRequestMap.remove(uuid);
        }
    }

    public static void flush(){
        voxelRenderDataMap.clear();
        voxelDataMap.clear();
        voxelRequestMap.clear();
        voxelRemoveMap.clear();
    }

}
