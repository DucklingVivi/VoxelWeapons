package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;


import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.network.NetworkEvent;


import java.util.ArrayList;
import java.util.List;
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
            VoxelData data = new VoxelData();
            data.offset = new BlockPos(0,100,0);
            for (int i = -3; i < 4; i++) {
                for (int j = -3; j < 4; j++) {
                    for (int k = 0; k < 7; k++) {
                        data.devAddBlock(new BlockPos(i,100+k,j), ctx.getSender().level);
                    }
                }
            }
            Messages.sendToPlayer(new WeaponPacket(uuid, data),  ctx.getSender());
            ctx.getSender().getHandSlots().forEach((itemStack) -> {
                if(itemStack.getItem() == Registration.VOXELWEAPONITEM.get()){
                    CompoundTag tag = itemStack.getOrCreateTag();
                    tag.put("voxelData",data.writeNBT());
                    itemStack.setTag(tag);
                }
            });
        });
        return true;
    }
}
