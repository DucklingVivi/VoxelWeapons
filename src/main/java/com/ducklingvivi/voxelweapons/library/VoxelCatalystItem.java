package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.model.VoxelWithoutLevelRenderer;
import com.ducklingvivi.voxelweapons.client.render.ItemTooltip;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.Style;
import java.awt.*;
import java.util.List;
import java.util.function.Consumer;

public class VoxelCatalystItem extends Item{

    public VoxelTier tier;
    public VoxelCatalystItem(VoxelTier tier) {
        super(new Item.Properties().stacksTo(1));
        this.tier = tier;
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


    @Override
    public @NotNull Component getName(ItemStack pStack) {
        return Component.translatable("item.voxelweapons.voxel_catalyst").append(": ").append(Component.translatable(this.tier.languageKey).withStyle(style -> style.withColor(this.tier.color)));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        String sizestring = "";
        if(pStack.getItem() instanceof VoxelCatalystItem catalystItem){
            AABB bb = catalystItem.tier.boundingBox;
            sizestring = (int)bb.getXsize() + "x" +  (int)bb.getYsize() + "x" + (int) bb.getZsize();
        }
        pTooltipComponents.add(Component.translatable("item.voxelweapons.voxelcatalyst.size").append(": ").append(sizestring));
        pTooltipComponents.add(Component.translatable("item.voxelweapons.voxelcatalyst.info"));
        pTooltipComponents.add(Component.translatable("item.voxelweapons.voxelcatalyst.flavor").withStyle(ChatFormatting.ITALIC).withStyle((style -> style.withColor(TextColor.parseColor("#ff8b3d")))));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
