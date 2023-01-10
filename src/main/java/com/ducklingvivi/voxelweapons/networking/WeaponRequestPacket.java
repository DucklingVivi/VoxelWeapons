package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.voxelweapons;


import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        Level level = DimensionUtils.createWorld(ctx.getSender().getServer(),"fucker");
        ctx.enqueueWork(() ->{

            voxelweapons.LOGGER.info(level.toString());
            BlockPos position2 = new BlockPos(0,100,0);
            BlockState blockState2 = Blocks.BRICKS.defaultBlockState();
            ctx.getSender().server.getLevel(Dimensions.VOXELDIMENSION).setBlock(position2, blockState2, 3);

            voxelweapons.LOGGER.info("Server meep");
            ctx.getSender().getHandSlots().forEach(itemStack -> {
                VoxelData data = new VoxelData();
                List<Voxel> voxels = new ArrayList<Voxel>();
                BlockPos pos = new BlockPos(0,100,0);
                BlockState blockState = ctx.getSender().server.getLevel(Dimensions.VOXELDIMENSION).getBlockState(pos);
                voxels.add(new Voxel(0,0,0,blockState));
                data.setVoxels(voxels);


                CompoundTag tag = itemStack.getOrCreateTag();
                CompoundTag tag2 = new CompoundTag();
                data.saveNBTData(tag2);

                tag.put("VoxelData",tag2);
                itemStack.setTag(tag);
            });
        });
        return true;
    }
}
