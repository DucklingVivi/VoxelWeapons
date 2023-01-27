package com.ducklingvivi.voxelweapons.recipe;

import com.ducklingvivi.voxelweapons.library.VoxelCatalystItem;
import com.ducklingvivi.voxelweapons.library.VoxelTier;
import com.ducklingvivi.voxelweapons.library.VoxelWeaponItem;
import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.UpgradeRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.NoteBlockEvent;

import java.util.UUID;

public class VoxelUpgradeRecipe extends UpgradeRecipe{

    public VoxelUpgradeRecipe(ResourceLocation pId, Ingredient pBase, Ingredient pAddition, ItemStack pResult) {
        super(pId, pBase, pAddition, pResult);
    }

    @Override
    public boolean matches(Container pInv, Level pLevel) {
        if(!super.matches(pInv,pLevel)) return false;
        ItemStack base = pInv.getItem(0);
        ItemStack addition = pInv.getItem(1);
        if(base.getItem() instanceof VoxelWeaponItem weaponItem && addition.getItem() instanceof VoxelCatalystItem catalystItem){
            return true;
        }
        return false;
    }

    @Override
    public ItemStack assemble(Container pInv) {
        ItemStack base = pInv.getItem(0);
        ItemStack addition = pInv.getItem(1);
        ItemStack result = getResultItem().copy();
        if(base.getItem() instanceof VoxelWeaponItem weaponItem && addition.getItem() instanceof VoxelCatalystItem catalystItem){
            weaponItem.tier = catalystItem.tier;
        }
        CompoundTag tag = base.getTag();
        if(tag != null){
            result.setTag(tag.copy());
        }

        return result;
    }
}
