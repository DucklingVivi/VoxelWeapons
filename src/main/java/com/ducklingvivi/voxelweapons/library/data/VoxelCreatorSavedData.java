package com.ducklingvivi.voxelweapons.library.data;

import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelTier;
import com.ducklingvivi.voxelweapons.library.voxelUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.io.File;

public class VoxelCreatorSavedData extends SavedData {



    private ItemStack itemStack;
    private ResourceKey<Level> levelOrigin;
    private BlockPos origin;
    private VoxelTier tier;
    private BlockPos levelOriginPos;

    @Nonnull
    public static VoxelCreatorSavedData get(ServerLevel level){
        DimensionDataStorage storage = level.getDataStorage();
        return storage.computeIfAbsent(VoxelCreatorSavedData::new,VoxelCreatorSavedData::new,"voxelCreatorData");
    }

    public VoxelCreatorSavedData(){
        origin = new BlockPos(0,0,0);

        levelOrigin = Level.OVERWORLD;
        levelOriginPos = null;
        itemStack = ItemStack.EMPTY;
        tier = VoxelTier.STARTER;
    }

    public VoxelCreatorSavedData(CompoundTag tag) {
        origin = NbtUtils.readBlockPos(tag.getCompound("Origin"));
        tier = VoxelTier.valueOf(tag.getString("Tier"));
        if(tag.contains("LevelOrigin")){levelOrigin = ResourceKey.create(Registries.DIMENSION,new ResourceLocation(tag.getString("LevelOrigin")));
        }else{levelOrigin = Level.OVERWORLD;}
        if(tag.contains("LevelOriginPos")){
            levelOriginPos = BlockPos.of(tag.getLong("LevelOriginPos"));
        }else{
            levelOriginPos = null;
        }
        itemStack = ItemStack.of(tag.getCompound("ItemStack"));
    }
    @Override
    public @NotNull CompoundTag save(CompoundTag pCompoundTag) {
        pCompoundTag.put("Origin", NbtUtils.writeBlockPos(origin));
        pCompoundTag.putString("Tier", tier.getSerializedName());
        pCompoundTag.putString("LevelOrigin", levelOrigin.location().toString());
        if(levelOriginPos!= null){
            pCompoundTag.putLong("LevelOriginPos", levelOriginPos.asLong());
        }
        pCompoundTag.put("ItemStack",itemStack.save(new CompoundTag()));
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

    public VoxelTier getTier() {
        return tier;
    }
    public void setTier(VoxelTier tier){
        this.tier = tier;
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

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
        setDirty();
    }

    public ItemStack getItemStack(){
        return itemStack;
    }

}
