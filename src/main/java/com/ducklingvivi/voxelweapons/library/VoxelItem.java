package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.model.VoxelWithoutLevelRenderer;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.networking.WeaponPacket;
import com.ducklingvivi.voxelweapons.networking.WeaponRequestPacket;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StreamTagVisitor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.client.IItemDecorator;

import java.util.UUID;
import java.util.function.Consumer;


public class VoxelItem extends Item {


    public VoxelItem() {
        super(new Properties().stacksTo(1));
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return VoxelWithoutLevelRenderer.getInstance();
            }
        });
    }



}
