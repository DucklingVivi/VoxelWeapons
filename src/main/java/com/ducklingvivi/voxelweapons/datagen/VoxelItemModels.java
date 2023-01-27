package com.ducklingvivi.voxelweapons.datagen;

import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.extensions.IForgeItem;


public class VoxelItemModels extends ItemModelProvider {

    public VoxelItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), voxelweapons.MODID, existingFileHelper);
    }
    @Override
    protected void registerModels() {
        ItemModelBuilder builder00 =
                getBuilder(Registration.VOXEL_WEAPON_STARTER.getId().getPath());
        ItemModelBuilder builder01 =
                getBuilder(Registration.VOXEL_WEAPON_OVERWORLD.getId().getPath());
        ItemModelBuilder builder02 =
                getBuilder(Registration.VOXEL_WEAPON_NETHER.getId().getPath());
        ItemModelBuilder builder03 =
                getBuilder(Registration.VOXEL_WEAPON_END.getId().getPath());
        ItemModelBuilder builder04 =
                getBuilder(Registration.VOXEL_WEAPON_BOSS.getId().getPath());

        ItemModelBuilder builder10 =
                getBuilder(Registration.VOXEL_CATALYST_STARTER.getId().getPath());
        ItemModelBuilder builder11 =
                getBuilder(Registration.VOXEL_CATALYST_OVERWORLD.getId().getPath());
        ItemModelBuilder builder12 =
                getBuilder(Registration.VOXEL_CATALYST_NETHER.getId().getPath());
        ItemModelBuilder builder13 =
                getBuilder(Registration.VOXEL_CATALYST_END.getId().getPath());
        ItemModelBuilder builder14 =
                getBuilder(Registration.VOXEL_CATALYST_BOSS.getId().getPath());

    }

}
