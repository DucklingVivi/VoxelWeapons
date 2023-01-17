package com.ducklingvivi.voxelweapons.client.model;

import com.ducklingvivi.voxelweapons.library.IItemStackMixinInterface;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.CompositeModel;
import org.jetbrains.annotations.Nullable;

public class VoxelItemOverride extends ItemOverrides {
    @Nullable
    @Override
    public BakedModel resolve(BakedModel pModel, ItemStack pStack, @Nullable ClientLevel pLevel, @Nullable LivingEntity pEntity, int pSeed) {
//        Entity target;
//        Entity entityRep = pStack.getEntityRepresentation();
//        if(entityRep != null) {
//            target = entityRep;
//        }else{
//            target = pEntity;
//        }
//        if (target != null){
//
//        }
        //TODO NbtIo.parseCompressed();
        CompoundTag tag = pStack.getOrCreateTag();
        if(tag.contains("voxelUUID")){
            //List<BakedQuad> bakedQuadList = VoxelDataClient.getData();
            VoxelData data = VoxelDataClient.getData(tag.getUUID("voxelUUID"));
            return new WeaponBakedModelFinalized(pModel, data);
        }
        return pModel;
    }
}
