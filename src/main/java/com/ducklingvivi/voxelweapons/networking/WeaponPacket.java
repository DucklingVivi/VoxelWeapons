package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.voxelUtils;
import com.ducklingvivi.voxelweapons.voxelweapons;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;
import org.checkerframework.checker.nullness.qual.NonNull;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class WeaponPacket {


    private UUID uuid;
    private VoxelData data;

    public WeaponPacket(UUID uuid, VoxelData data) {
        this.uuid = uuid;
        this.data = data;
    }

    public WeaponPacket(FriendlyByteBuf buf) {
        CompoundTag nbt = buf.readNbt();
        if(nbt == null) nbt = new CompoundTag();
        CompoundTag tempdata = nbt.getCompound("data");
        data = VoxelData.fromNBT(voxelUtils.getLevel(),tempdata);
        uuid = nbt.getUUID("uuid");
    }

    public void toBytes(FriendlyByteBuf buf){
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", uuid);
        CompoundTag tag2 = data.writeNBT();
        tag.put("data", tag2);
        buf.writeNbt(tag);
        voxelweapons.LOGGER.info(String.valueOf(tag.sizeInBytes()));
    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{
            voxelweapons.LOGGER.info("Recieved packet");
        });
        return true;
    }
}
