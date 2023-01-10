package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class RoomRequestPacket {

    public RoomRequestPacket() {
    }

    public RoomRequestPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {

        });
        return true;
    }
}

