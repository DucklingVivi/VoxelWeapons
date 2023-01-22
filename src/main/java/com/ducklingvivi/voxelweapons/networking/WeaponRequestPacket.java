package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;


import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class WeaponRequestPacket {

    private UUID uuid;
    public WeaponRequestPacket(UUID uuid) {
        this.uuid = uuid;
    }

    public WeaponRequestPacket(FriendlyByteBuf buf) {
        uuid = buf.readUUID();
    }

    public void toBytes(FriendlyByteBuf buf){
        buf.writeUUID(uuid);
    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{
            Messages.sendToPlayer(new WeaponPacket(uuid, VoxelSavedData.get().getData(uuid), WeaponPacket.WeaponOperation.REPLACE),  ctx.getSender());
        });
        return true;
    }
}
