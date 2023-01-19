package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.client.model.VoxelDataClient;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.voxelUtils;
import com.ducklingvivi.voxelweapons.voxelweapons;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;


import java.util.UUID;
import java.util.function.Supplier;

public class WeaponPacket {


    private UUID uuid;
    private VoxelData data;
    private WeaponOperation operation;

    public WeaponPacket(UUID uuid, VoxelData data, WeaponOperation operation) {
        this.uuid = uuid;
        this.data = data;
        this.operation = operation;
    }

    public WeaponPacket(FriendlyByteBuf buf) {
        CompoundTag nbt = buf.readAnySizeNbt();
        if(nbt == null) nbt = new CompoundTag();
        operation = WeaponOperation.valueOf(nbt.getString("operation"));
        CompoundTag tempdata = nbt.getCompound("data");
        data = VoxelData.fromNBT(voxelUtils.getLevel(),tempdata);
        uuid = nbt.getUUID("uuid");

    }

    public void toBytes(FriendlyByteBuf buf){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        CompoundTag tag2 = data.writeNBT();
        tag.put("data", tag2);
        tag.putString("operation",operation.toString());
        buf.writeNbt(tag);

        voxelweapons.LOGGER.info("Sent weaponpacket with {} bytes", buf.writerIndex());

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{
            if(operation == WeaponOperation.REPLACE){
                VoxelDataClient.addData(uuid,data);
            }
            if(operation == WeaponOperation.DELETE){
                VoxelDataClient.removeData(uuid);
            }
        });
        return true;
    }

    public enum WeaponOperation{
        ADD,
        DELETE,
        REPLACE,

    }
}
