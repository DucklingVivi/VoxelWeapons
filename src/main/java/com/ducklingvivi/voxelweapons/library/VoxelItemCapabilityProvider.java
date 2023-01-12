package com.ducklingvivi.voxelweapons.library;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VoxelItemCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    @Override
    public CompoundTag serializeNBT() {
        VoxelHandler handler = getCachedVoxelHandler();
        return handler.serializeNBT();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        VoxelHandler handler = getCachedVoxelHandler();
        handler.deserializeNBT(nbt);
    }


    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (VoxelHandler.CAPABILITY == cap) return (LazyOptional<T>)(lazyInitialisionSupplier);
        return LazyOptional.empty();
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return getCapability(cap, null);
    }

    private VoxelHandler getCachedVoxelHandler() {
        if (itemStackHandlerVoxel == null) {
            itemStackHandlerVoxel = new VoxelHandler();
        }
        return itemStackHandlerVoxel;
    }
    private VoxelHandler itemStackHandlerVoxel;
    private final LazyOptional<VoxelHandler> lazyInitialisionSupplier = LazyOptional.of(this::getCachedVoxelHandler);
}
