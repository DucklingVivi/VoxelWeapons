package com.ducklingvivi.voxelweapons.library;



import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.level.block.state.BlockState;


public class Voxel {
    public int x;
    public int y;
    public int z;
    public BlockState blockState;
    public Voxel(int x, int y, int z, BlockState blockState) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.blockState = blockState;
    }

    public CompoundTag toCompound() {

        CompoundTag tag = new CompoundTag();
        tag.putInt("x",x);
        tag.putInt("y",y);
        tag.putInt("z",z);
        tag.put("blockstate", NbtUtils.writeBlockState(blockState));
        return tag;
    }

    public static Voxel fromCompound(CompoundTag tag) {
        return new Voxel(tag.getInt("x"), tag.getInt("y"), tag.getInt("z"), NbtUtils.readBlockState(
                voxelUtils.getLevel().holderLookup(Registries.BLOCK),
                tag.getCompound("blockstate")));
    }
}

