package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelSavedData;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.networking.WeaponRequestPacket;

import java.util.*;

public class VoxelDataClient {

    public static final int REMOVE_TICK_TIME = 500;
    public static final int REQUEST_TICK_TIME = 100;
    public static Map<UUID, VoxelData> voxelDataMap = new HashMap<>();
    public static Map<UUID, Integer> voxelRemoveMap = new HashMap<>();
    public static  Map<UUID, Integer> voxelRequestMap = new HashMap<>();


    public static void addData(UUID uuid, VoxelData data){
        voxelDataMap.put(uuid,data);
        voxelRemoveMap.put(uuid,REMOVE_TICK_TIME);
    }
    public static void removeData(UUID uuid){
        voxelDataMap.remove(uuid);
        voxelRemoveMap.remove(uuid);
    }

    public static VoxelData getData(UUID uuid) {
        voxelRemoveMap.put(uuid, REMOVE_TICK_TIME);
        if(voxelDataMap.containsKey(uuid)){
            return voxelDataMap.get(uuid);
        }
        if(voxelRequestMap.containsKey(uuid)){
            return new VoxelData();
        }
        voxelRequestMap.put(uuid, REQUEST_TICK_TIME);
        Messages.sendToServer(new WeaponRequestPacket(uuid));
        return new VoxelData();
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
        voxelDataMap.clear();
        voxelRequestMap.clear();
        voxelRemoveMap.clear();
    }

}
