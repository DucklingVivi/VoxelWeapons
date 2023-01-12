package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelHandler;
import com.ducklingvivi.voxelweapons.voxelweapons;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.NetworkEvent;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WeaponRequestPacket {


    public WeaponRequestPacket() {
    }

    public WeaponRequestPacket(FriendlyByteBuf buf) {
    }

    public void toBytes(FriendlyByteBuf buf){

    }
    public boolean handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() ->{



            voxelweapons.LOGGER.info("Server meep");
            ctx.getSender().getHandSlots().forEach(itemStack -> {
                VoxelData data = new VoxelData();
                List<Voxel> voxels = new ArrayList<Voxel>();
                for (int i = -2; i < 3; i++) {
                    for (int j = -2; j < 3; j++) {
                        for (int k = 0; k < 6; k++) {

                            BlockPos pos = new BlockPos(0+i,100+k,0+j);
                            BlockState blockState = ctx.getSender().server.getLevel(Dimensions.VOXELDIMENSION).getBlockState(pos);
                            itemStack.getCapability(VoxelHandler.CAPABILITY);
                            voxels.add(new Voxel(i,k,j,blockState));
                        }
                    }
                }
                VoxelHandler voxelBase;
                LazyOptional<VoxelHandler> capability = itemStack.getCapability(VoxelHandler.CAPABILITY);

                data.setVoxels(voxels);

                if(capability.isPresent()){
                    voxelBase = capability.orElseThrow(AssertionError::new);
                    voxelBase.setVoxelData(data);
                }

                LazyOptional<VoxelHandler> capability2 = itemStack.getCapability(VoxelHandler.CAPABILITY);


                CompoundTag nbt = itemStack.getOrCreateTag();
                int dirtyCounter = nbt.getInt("dirtyCounter");
                nbt.putInt("dirtyCounter", dirtyCounter + 1);
                itemStack.setTag(nbt);

//
//                CompoundTag tag2 = new CompoundTag();
//                data.saveNBTData(tag2);
//
//                tag.put("VoxelData",tag2);
//
            });
        });
        return true;
    }
}
