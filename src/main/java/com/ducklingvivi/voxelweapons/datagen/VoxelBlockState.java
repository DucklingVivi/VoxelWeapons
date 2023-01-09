package com.ducklingvivi.voxelweapons.datagen;

import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import com.ducklingvivi.voxelweapons.setup.Registration;



public class VoxelBlockState extends BlockStateProvider {
    public VoxelBlockState(DataGenerator gen, ExistingFileHelper helper) {
        super(gen.getPackOutput(), voxelweapons.MODID, helper);
    }
    @Override
    protected void registerStatesAndModels() {


    }

    private void registerWeaponModel(){

    }
}
