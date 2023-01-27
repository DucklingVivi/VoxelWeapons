package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.client.model.VoxelWithoutLevelRenderer;
import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import jdk.jshell.spi.ExecutionControl;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Consumer;


public class VoxelWeaponItem extends Item {

    public VoxelTier tier;
    public static Map<VoxelTier,VoxelWeaponItem> tierMap = new HashMap<>();
    public VoxelWeaponItem(VoxelTier tier) {
        super(new Properties());
        this.tier = tier;
        tierMap.put(tier, this);
    }
    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;
    }
    @Override
    public boolean isEnchantable(ItemStack pStack) {
        return false;
    }
    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }
    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return false;
    }
    @Override
    public boolean isFoil(ItemStack pStack) {
        return false;
    }
    @Override
    public Rarity getRarity(ItemStack pStack) {
        return Rarity.COMMON;
    }
    @Override
    public boolean isRepairable(ItemStack stack) {
        return false;
    }
    @Override
    public boolean canBeDepleted() {
        return false;
    }
    @Override
    public boolean isBarVisible(ItemStack pStack) {
        return false;
    }

    @Override
    public boolean onLeftClickEntity(ItemStack stack, Player player, Entity entity) {
        return super.onLeftClickEntity(stack, player, entity);
    }

    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, VoxelData data) {
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(Item.BASE_ATTACK_DAMAGE_UUID,"voxelweapons.voxelweapon.attack_damage", 2, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(Item.BASE_ATTACK_SPEED_UUID, "voxelweapons.voxelweapon.attack_speed", 1f, AttributeModifier.Operation.ADDITION));
        return builder.build();
    }
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if(!tag.hasUUID("voxelUUID") || !slot.getName().equals("mainhand")) return ImmutableMultimap.of();
        UUID uuid = tag.getUUID("voxelUUID");
        VoxelData data = VoxelSavedData.get().getData(uuid);
        return getAttributeModifiers(slot,data);
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
