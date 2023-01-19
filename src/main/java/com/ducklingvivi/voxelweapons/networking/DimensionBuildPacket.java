package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelSavedData;


import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;


import java.util.UUID;
import java.util.function.Supplier;

public class DimensionBuildPacket {


    public DimensionBuildPacket() {
    }

    public DimensionBuildPacket(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf){
    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{
            if(ctx.getSender().level.dimension().location().getNamespace().equals(voxelweapons.MODID)){
                VoxelData.BuildWeapon(ctx.getSender().getLevel(),ctx.getSender());
            }
        });
        return true;
    }
}
