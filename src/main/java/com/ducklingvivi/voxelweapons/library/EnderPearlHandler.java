package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.dimensions.VoxelChunkGenerator;
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
                if(entity instanceof ItemEntity itemEntity && itemEntity.getItem().getItem() instanceof VoxelWeaponItem weaponItem){
                    CompoundTag tag = itemEntity.getItem().getOrCreateTag();
                    if(tag.contains("voxelUUID")){
                        UUID uuid = tag.getUUID("voxelUUID");
                        voxelweapons.LOGGER.info(uuid.toString());
                        ServerLevel tolevel = VoxelSavedData.get().CreateDimensionFromData(uuid, weaponItem.tier);
                        VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(tolevel);
                        savedData.setLevelOriginPos(itemEntity.blockPosition());
                        savedData.setLevelOrigin(level.dimension());
                        savedData.setItemStack(itemEntity.getItem());
                        tolevel.getDataStorage().save();

                        AABB boundingbox = weaponItem.tier.boundingBox;
                        BlockPos pos = new BlockPos(boundingbox.maxX+4.5f,1,boundingbox.getCenter().z);
                        player.teleportTo(tolevel, pos.getX()+0.5f, pos.getY(), pos.getZ()+0.5f, 90f, 0 );
                        itemEntity.discard();
                        event.setCanceled(true);
                        thrownEnderpearl.discard();
                        return;
                    }
                } else if (entity instanceof ItemEntity itemEntity && itemEntity.getItem().is(Registration.VOXEL_CATALYST_PREDICATE)) {
                    VoxelCatalystItem item = (VoxelCatalystItem) itemEntity.getItem().getItem();
                    UUID uuid = UUID.randomUUID();
                    VoxelTier tier = item.tier;

                    AABB boundingbox = tier.boundingBox;
                    BlockPos pos = new BlockPos(boundingbox.maxX+4.5f,1,boundingbox.getCenter().z);
                    VoxelChunkGenerator.Settings settings = new VoxelChunkGenerator.Settings(new VoxelChunkGenerator.FloorSettings((int)boundingbox.minX,(int)boundingbox.maxX,(int)boundingbox.minZ,(int)boundingbox.maxZ),pos.getX(),pos.getZ());
                    ServerLevel newLevel = VoxelSavedData.get().CreateDimension(uuid,settings);
                    VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(newLevel);
                    savedData.setOrigin(new BlockPos(0,0,0));
                    savedData.setLevelOrigin(player.getLevel().dimension());
                    savedData.setLevelOriginPos(player.blockPosition());
                    savedData.setTier(tier);
                    newLevel.getDataStorage().save();


                    itemEntity.discard();
                    thrownEnderpearl.discard();
                    player.teleportTo(newLevel, pos.getCenter().x,pos.getY(),pos.getCenter().z,90,0f);
                    return;
                }
            }

        }
    }
}
