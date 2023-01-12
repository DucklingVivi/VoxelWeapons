package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.networking.WeaponRequestPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;


public class VoxelItem extends Item {


    private final String BASE_NBT_TAG = "base";
    private final String CAPABILITY_NBT_TAG = "cap";

    public VoxelItem() {
        super(new Properties().stacksTo(1));
    }


    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {

        boolean value = super.onEntitySwing(stack, entity);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Messages.sendToServer(new WeaponRequestPacket()));
        return value;
    }

    private static VoxelHandler getItemStackHandlerVoxel(ItemStack itemStack) {
        VoxelHandler voxelHandler = itemStack.getCapability(VoxelHandler.CAPABILITY).orElseThrow(AssertionError::new);
        return voxelHandler;

    }


    @Override
    public @Nullable CompoundTag getShareTag(ItemStack stack) {
        CompoundTag baseTag = stack.getTag();
        VoxelHandler itemStackHandlerVoxel = getItemStackHandlerVoxel(stack);
        CompoundTag capabilityTag = itemStackHandlerVoxel.serializeNBT();
        CompoundTag combinedTag = new CompoundTag();
        if (baseTag != null) {
            combinedTag.put(BASE_NBT_TAG, baseTag);
        }
        if(capabilityTag != null) {
            combinedTag.put(CAPABILITY_NBT_TAG, capabilityTag);
        }
        return combinedTag;
    }



    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundTag nbt) {
        if (nbt == null) {
            stack.setTag(null);
            return;
        }

        CompoundTag baseTag = nbt.getCompound(BASE_NBT_TAG);              // empty if not found
        CompoundTag capabilityTag = nbt.getCompound(CAPABILITY_NBT_TAG); // empty if not found
        stack.setTag(baseTag);
        VoxelHandler itemStackHandlerVoxel = getItemStackHandlerVoxel(stack);
        itemStackHandlerVoxel.deserializeNBT(capabilityTag);



    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new VoxelItemCapabilityProvider();
    }
}
