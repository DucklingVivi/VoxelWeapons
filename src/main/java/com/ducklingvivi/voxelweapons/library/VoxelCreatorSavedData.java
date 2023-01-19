package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class VoxelCreatorSavedData extends SavedData {

    private ResourceKey<Level> levelOrigin;
    private BlockPos origin;

    private AABB boundingBox;
    private BlockPos levelOriginPos;

    @Nonnull
    public static VoxelCreatorSavedData get(ServerLevel level){
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(VoxelCreatorSavedData::new,VoxelCreatorSavedData::new,"voxelCreatorData");
    }

    public VoxelCreatorSavedData(){
        origin = new BlockPos(0,0,0);
        boundingBox = new AABB(origin);
        levelOrigin = Level.OVERWORLD;
        levelOriginPos = null;
    }

    public VoxelCreatorSavedData(CompoundTag tag) {
        origin = NbtUtils.readBlockPos(tag.getCompound("Origin"));
        boundingBox = voxelUtils.readAABB(tag.getList("BoundingBox",Tag.TAG_FLOAT));
        if(tag.contains("LevelOrigin")){levelOrigin = ResourceKey.create(Registries.DIMENSION,new ResourceLocation(tag.getString("LevelOrigin")));
        }else{levelOrigin = Level.OVERWORLD;}
        if(tag.contains("LevelOriginPos")){
            levelOriginPos = BlockPos.of(tag.getLong("LevelOriginPos"));
        }else{
            levelOriginPos = null;
        }
    }
    @Override
    public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.put("Origin", NbtUtils.writeBlockPos(origin));
        pCompoundTag.put("BoundingBox", voxelUtils.writeAABB(boundingBox));
        pCompoundTag.putString("LevelOrigin", levelOrigin.location().toString());
        if(levelOriginPos!= null){
            pCompoundTag.putLong("LevelOriginPos", levelOriginPos.asLong());
        }
        return pCompoundTag;
    }


    @Override
    public void save(File pFile) {
        super.save(pFile);
    }

    public void setOrigin(BlockPos origin) {
        this.origin = origin;
        setDirty(true);

    }
    public BlockPos getOrigin(){
        return this.origin;
    }

    public AABB getBoundingBox() {
        return boundingBox;
    }
    public void setBoundingBox(AABB boundingBox){
        this.boundingBox = boundingBox;
        setDirty(true);
    }

    public void setLevelOrigin(ResourceKey<Level> levelResourceKey){
        this.levelOrigin = levelResourceKey;
        setDirty();
    }
    public ResourceKey<Level> getLevelOrigin(){
        return this.levelOrigin;
    }

    public void setLevelOriginPos(BlockPos pos ) {
        this.levelOriginPos = pos;
        setDirty();
    }
    public BlockPos getLevelOriginPos() {
        return levelOriginPos;
    }
}
