package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.library.data.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;

import java.io.Serial;
import java.util.List;
import java.util.UUID;

public class EnderPearlHandler {

    public static void onEnderPearlHit(ProjectileImpactEvent event){
        if(event.getProjectile() instanceof ThrownEnderpearl thrownEnderpearl && thrownEnderpearl.getOwner() !=null && thrownEnderpearl.getOwner() instanceof ServerPlayer player){
            Level level = thrownEnderpearl.getLevel();
            if(level.isClientSide()) return;
            List<Entity> entityList = level.getEntities(event.getEntity(), AABB.ofSize(thrownEnderpearl.position(),3,3,3));
            for(Entity entity : entityList){
                if(entity instanceof ItemEntity itemEntity && itemEntity.getItem().is(Registration.VOXELWEAPONITEM.get())){
                    CompoundTag tag = itemEntity.getItem().getOrCreateTag();
                    if(tag.contains("voxelUUID")){
                        UUID uuid = tag.getUUID("voxelUUID");
                        voxelweapons.LOGGER.info(uuid.toString());
                        ServerLevel tolevel = VoxelSavedData.get().CreateDimensionFromData(uuid);
                        VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(tolevel);
                        savedData.setLevelOriginPos(itemEntity.blockPosition());
                        savedData.setLevelOrigin(level.dimension());
                        savedData.setItemStack(itemEntity.getItem());
                        tolevel.getDataStorage().save();
                        BlockPos pos = savedData.getSpawnPoint();
                        player.teleportTo(tolevel, pos.getX()+0.5f, pos.getY(), pos.getZ()+0.5f, 90f, 0 );
                        itemEntity.discard();
                        event.setCanceled(true);
                        thrownEnderpearl.discard();
                        return;
                    }
                }
            }

        }
    }
}
