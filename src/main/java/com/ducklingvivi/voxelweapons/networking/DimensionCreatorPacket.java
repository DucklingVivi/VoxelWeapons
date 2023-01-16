package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.client.model.VoxelDataClient;
import com.ducklingvivi.voxelweapons.commands.CommandCreateWeapon;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.voxelUtils;
import com.ducklingvivi.voxelweapons.voxelweapons;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.antlr.v4.codegen.model.Sync;


import java.util.UUID;
import java.util.function.Supplier;

public class DimensionCreatorPacket {


    private CompoundTag data;
    private DimensionCreatorOperation operation;

    public DimensionCreatorPacket(DimensionCreatorOperation operation, CompoundTag data) {
        this.data = data;
        this.operation = operation;
    }

    public DimensionCreatorPacket(FriendlyByteBuf buf) {
        CompoundTag nbt = buf.readAnySizeNbt();
        if(nbt == null) nbt = new CompoundTag();
        operation = DimensionCreatorOperation.valueOf(nbt.getString("operation"));
        data = nbt.getCompound("data");
        //uuid = nbt.getUUID("uuid");

    }

    public void toBytes(FriendlyByteBuf buf){
        CompoundTag tag = new CompoundTag();
        //tag.putUUID("uuid", uuid);
        tag.putString("operation",operation.toString());
        buf.writeNbt(tag);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{
            switch (operation){
                case SYNCORIGIN -> {
                   SyncOrigin();
                }
                case SYNCALL -> {
                    SyncAll();
                }
                default -> {
                    //DO NOTHING
                }
            }
        });
        return true;
    }

    public enum DimensionCreatorOperation{

        SYNCORIGIN,
        SYNCALL

    }

    private void SyncAll(){
        SyncOrigin();
    }
    private void SyncOrigin(){
        BlockPos originPos = NbtUtils.readBlockPos(data.getCompound("ORIGIN"));
        voxelweapons.LOGGER.info("Origin set to {}",originPos.toShortString());
    }
}
