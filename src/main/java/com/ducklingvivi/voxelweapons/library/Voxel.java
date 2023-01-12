package com.ducklingvivi.voxelweapons.library;



import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;




public interface VoxelCapability {

    public CompoundTag toCompound();

    public Voxel fromCompound(CompoundTag tag);
}

public class Voxel implements VoxelCapability{

    public int x;
    public int y;
    public int z;
    public BlockState blockState;
    Voxel(int x, int y, int z, BlockState blockState) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockState = blockState;
    }

    @Override
    public CompoundTag toCompound() {

        CompoundTag tag = new CompoundTag();
        tag.putInt("x",x);
        tag.putInt("y",y);
        tag.putInt("z",z);
        tag.put("blockstate", NbtUtils.writeBlockState(blockState));
        return tag;
    }
    @Override
    public Voxel fromCompound(CompoundTag tag) {
        return new Voxel(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"), NbtUtils.readBlockState(
                voxelUtils.getLevel().holderLookup(Registries.BLOCK),
                tag.getCompound("blockstate")));
    }
}

