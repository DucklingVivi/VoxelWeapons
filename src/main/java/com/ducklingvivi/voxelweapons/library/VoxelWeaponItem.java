package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.model.VoxelWithoutLevelRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;


public class VoxelWeaponItem extends Item {

    public VoxelTier tier;
    public static Map<VoxelTier,VoxelWeaponItem> tierMap = new HashMap<>();
    public VoxelWeaponItem(VoxelTier tier) {
        super(new Properties().stacksTo(1));
        this.tier = tier;
        tierMap.put(tier, this);
    }


    public @NotNull Component getName(ItemStack pStack) {
        return Component.translatable("item.voxelweapons.voxel_weapon");
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
