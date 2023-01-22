package com.ducklingvivi.voxelweapons.networking;
import com.ducklingvivi.voxelweapons.client.data.VoxelCreatorClientData;
import com.ducklingvivi.voxelweapons.library.voxelUtils;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkEvent;

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
        tag.put("data", data);
        buf.writeNbt(tag);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{
            switch (operation){
                case SYNCORIGIN -> {
                   SyncOrigin();
                }
                case SYNCBBOUND -> {
                    SyncBound();
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
        SYNCBBOUND,
        SYNCALL

    }

    private void SyncAll(){
        SyncOrigin();
        SyncBound();
        VoxelCreatorClientData.INSTANCE.isWireFrameVisible = false;
    }
    private void SyncOrigin(){
        BlockPos originPos = NbtUtils.readBlockPos(data.getCompound("Origin"));
        VoxelCreatorClientData.INSTANCE.setOrigin(originPos);
    }
    private void SyncBound(){
        AABB boundingBox = voxelUtils.readAABB(data.getList("BoundingBox", Tag.TAG_FLOAT));
        VoxelCreatorClientData.INSTANCE.setBoundingBox(boundingBox);
    }
}
