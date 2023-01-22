package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.library.VoxelData;


import com.ducklingvivi.voxelweapons.library.data.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.ibm.icu.text.MessagePattern;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelData;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;


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
            if(ctx.getSender() != null && ctx.getSender().level.dimension().location().getNamespace().equals(voxelweapons.MODID)){

                ServerPlayer player = ctx.getSender();
                MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                ServerLevel currentLevel = player.getLevel();
                ServerLevel exitLevel = server.getLevel(VoxelCreatorSavedData.get(currentLevel).getLevelOrigin());
                BlockPos pos = VoxelCreatorSavedData.get(currentLevel).getLevelOriginPos();
                if(exitLevel==null){
                    exitLevel = server.getLevel(player.getRespawnDimension());
                    pos = player.getRespawnPosition();
                    if(exitLevel == null){
                        exitLevel = server.overworld();
                        pos = exitLevel.getSharedSpawnPos();
                    }
                }


                if(ctx.getSender().level.players().size() == 1){
                    Integer levelindex = Integer.valueOf(currentLevel.dimension().location().getPath());

                    ItemStack item = VoxelData.BuildWeapon(currentLevel);
                    player.teleportTo(exitLevel, pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,0,0);
                    VoxelSavedData.get().DeleteDimension(levelindex);

                    if(!player.addItem(item)){
                        ItemEntity entity = new ItemEntity(exitLevel, pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,item);
                        exitLevel.addFreshEntity(entity);

                    }
                }else{
                    player.teleportTo(exitLevel, pos.getX()+0.5,pos.getY(),pos.getZ()+0.5,0,0);
                }
            }
        });
        return true;
    }
}
