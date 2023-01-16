package com.ducklingvivi.voxelweapons.mixin;

import com.ducklingvivi.voxelweapons.library.IItemStackMixinInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public class ItemStackMixin implements IItemStackMixinInterface {
    private BlockPos _blockPos = null;
    @Override
    public BlockPos getItemStackBlockPos() {
         return _blockPos;
    }
    @Override
    public void setItemStackBlockPos(BlockPos value) {
        _blockPos = value;
    }
}


