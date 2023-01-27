package com.ducklingvivi.voxelweapons.library.data;

import com.ducklingvivi.voxelweapons.client.render.RenderTypes;
import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.dimensions.VoxelChunkGenerator;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelTier;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class VoxelSavedData extends SavedData {
    private final Map<UUID, VoxelData> Data;

    private final Map<UUID, Integer> DimensionMap;


    @Nonnull
    public static VoxelSavedData get(){
        DimensionDataStorage storage = ServerLifecycleHooks.getCurrentServer().overworld().getDataStorage();
        return storage.computeIfAbsent(VoxelSavedData::new,VoxelSavedData::new,"voxeldata");
    }
    public VoxelSavedData(){
        Data = new HashMap<>();
        DimensionMap = new HashMap<>();
    }

    public VoxelSavedData(CompoundTag tag) {
        Data = new HashMap<>();
        DimensionMap = new HashMap<>();
        if(!tag.contains("voxelData") || !tag.contains("dimensionMap")) return;
        ListTag listtag = tag.getList("voxelData", Tag.TAG_COMPOUND);
        for (Tag t : listtag){
            CompoundTag listitem = (CompoundTag) t;

            UUID uuid = listitem.getUUID("uuid");
            VoxelData data = VoxelData.fromNBT(ServerLifecycleHooks.getCurrentServer().overworld(),listitem.getCompound("data"));
            Data.put(uuid,data);
        }
        ListTag listtag2 = tag.getList("dimensionMap", Tag.TAG_COMPOUND);
        for (Tag t: listtag2) {
            CompoundTag listitem = (CompoundTag) t;
            Integer integer = listitem.getInt("integer");
            UUID uuid = listitem.getUUID("uuid");
            DimensionMap.put(uuid,integer);
        }
    }



    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        ListTag listtag = new ListTag();
        Data.forEach((uuid,voxeldata)-> {
            CompoundTag listitem = new CompoundTag();
            listitem.putUUID("uuid", uuid);
            listitem.put("data", voxeldata.writeNBT());

            listtag.add(listitem);
        });
        tag.put("voxelData",listtag);
        ListTag listtag2 = new ListTag();
        DimensionMap.forEach(((uuid,integer) -> {
            CompoundTag listitem = new CompoundTag();
            listitem.putInt("integer", integer);
            listitem.putUUID("uuid", uuid);
            listtag2.add(listitem);
        }));
        tag.put("dimensionMap",listtag2);
        return tag;
    }



    public ServerLevel CreateDimension(UUID uuid, VoxelChunkGenerator.Settings settings){
        boolean flag = false;
        Integer x = 0;
        while(!flag) {
            if (DimensionMap.containsValue(x)) {
                x++;
            } else {
                flag = true;
            }
        }
        DimensionMap.put(uuid, x);
        setDirty(true);

        return DimensionUtils.createWorld(ServerLifecycleHooks.getCurrentServer(),x.toString(),settings);
    }
    public void DeleteDimension(UUID uuid){
        if(DimensionMap.containsKey(uuid)){
            Integer dimensionid = DimensionMap.remove(uuid);
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            server.getLevel(DimensionUtils.getLevelKey(new ResourceLocation(voxelweapons.MODID, dimensionid.toString()))).getDataStorage().save();
            DimensionUtils.deleteWorld(server,dimensionid.toString());
            setDirty(true);
        }
    }
    public void DeleteDimension(Integer integer){
        if(DimensionMap.containsValue(integer)){
            for (Map.Entry<UUID,Integer> entry  :DimensionMap.entrySet()) {
                if(entry.getValue().equals(integer)){
                    DeleteDimension(entry.getKey());
                    return;
                }
            }
        }
    }


    public UUID getDimensionUUID(Integer integer){
        if(DimensionMap.containsValue(integer)){
            for (Map.Entry<UUID,Integer> entry  :DimensionMap.entrySet()) {
                if(entry.getValue().equals(integer)){
                    return entry.getKey();
                }
            }
        }
    return null;
    }
    public VoxelData getData(UUID uuid){
        return Data.getOrDefault(uuid,new VoxelData());
    }
    public void removeData(UUID uuid){
        Data.remove(uuid);
        setDirty();
    }
    public boolean addData(UUID uuid, VoxelData data){
        Data.put(uuid, data);
        setDirty();

        return true;
    }

    public ServerLevel CreateDimensionFromData(UUID uuid, VoxelTier tier) {
        VoxelData data = getData(uuid);

        //TODO GET THIS DATA FROM THE VOXERDATA ITSELF
        AABB boundingbox = tier.boundingBox;
        BlockPos pos = new BlockPos(boundingbox.maxX+4.5f,1,boundingbox.getCenter().z);
        BlockPos origin = data.offset;
        VoxelChunkGenerator.Settings settings = new VoxelChunkGenerator.Settings(new VoxelChunkGenerator.FloorSettings((int)boundingbox.minX,(int)boundingbox.maxX,(int)boundingbox.minZ,(int)boundingbox.maxZ),pos.getX(),pos.getZ());
        ServerLevel level = CreateDimension(uuid,settings);
        for (Map.Entry<BlockPos, StructureTemplate.StructureBlockInfo> entry: data.devGetBlocks().entrySet()) {
            BlockPos pos1 = entry.getKey();
            pos1 = pos1.offset(origin);
            StructureTemplate.StructureBlockInfo info = entry.getValue();
            level.setBlockAndUpdate(pos1,info.state);
            if(info.nbt != null){
                BlockEntity toput = BlockEntity.loadStatic(pos1,info.state, info.nbt);
                assert toput != null;
                level.setBlockEntity(toput);
            }

        }
        VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(level);
        savedData.setOrigin(data.offset);
        savedData.setTier(tier);
        return level;
    }
}
