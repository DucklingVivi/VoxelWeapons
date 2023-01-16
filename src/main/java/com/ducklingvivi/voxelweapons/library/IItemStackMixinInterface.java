package com.ducklingvivi.voxelweapons.library;

import net.minecraft.core.BlockPos;

public interface IItemStackMixinInterface {
    BlockPos getItemStackBlockPos();
    void setItemStackBlockPos(BlockPos value);
}
