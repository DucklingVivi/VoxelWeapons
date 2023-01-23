package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.model.VoxelWithoutLevelRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.Style;
import java.awt.*;
import java.util.List;
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

    @Override
    public @NotNull Component getName(ItemStack pStack) {
        return Component.literal("Voxel Catalyst");
    }

    @Override
    public @NotNull Rarity getRarity(ItemStack pStack) {
        return Rarity.UNCOMMON;
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {

        pTooltipComponents.add(Component.literal("Throw an ").withStyle(ChatFormatting.GRAY).append(Component.literal("[Ender Pearl]").withStyle(ChatFormatting.DARK_AQUA)).append(Component.literal(" while dropped on ground to construct a new weapon").withStyle(ChatFormatting.GRAY)));
        pTooltipComponents.add(Component.empty());
        pTooltipComponents.add(Component.literal("A gateway to infinite creation").withStyle(ChatFormatting.ITALIC).withStyle((style -> style.withColor(TextColor.parseColor("#ff8b3d")))));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }
}
