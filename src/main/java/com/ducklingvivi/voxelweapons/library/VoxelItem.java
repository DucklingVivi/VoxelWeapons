package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.networking.WeaponRequestPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.CapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.DistExecutor;
import org.jetbrains.annotations.Nullable;


public class VoxelItem extends Item {


    public VoxelItem() {
        super(new Properties().stacksTo(1));
    }


    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        boolean value = super.onEntitySwing(stack, entity);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> Messages.sendToServer(new WeaponRequestPacket()));
        return value;
    }

    @Override
    public @Nullable ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
        return new VoxelItemCapabilityProvider();
    }
}
