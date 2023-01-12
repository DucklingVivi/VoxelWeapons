package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.setup.Registration;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VoxelItemCapabilityProvider implements ICapabilitySerializable<CompoundTag> {
    @Override
    public CompoundTag serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {

    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        getCapability(cap);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        if (Registration.VOXELITEMCAPABILITY == cap) return (LazyOptional<T>)(lazyInitialisionSupplier);
        return LazyOptional.empty();
    }

    private VoxelHandler getCachedInventory() {
        if (itemStackHandlerFlowerBag == null) {
            itemStackHandlerFlowerBag = new Voxel;
        }
        return itemStackHandlerFlowerBag;
    }
    private VoxelHandler itemStackHandlerFlowerBag;
    private final LazyOptional<IItemHandler> lazyInitialisionSupplier = LazyOptional.of(this::getCachedInventory);
}
