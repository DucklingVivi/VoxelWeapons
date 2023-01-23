package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.model.VoxelWithoutLevelRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class VoxelCatalystItem extends Item{
    public VoxelCatalystItem() {
        super(new Item.Properties().stacksTo(1));
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
