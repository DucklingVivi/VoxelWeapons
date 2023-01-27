package com.ducklingvivi.voxelweapons.datagen;

import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.logging.Logger;

@Mod.EventBusSubscriber(modid = voxelweapons.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeClient(),new VoxelBlockState(generator, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(),new VoxelItemModels(generator, event.getExistingFileHelper()));
        generator.addProvider(true, new VoxelRecipeProvider(generator.getPackOutput()));
    }

}
