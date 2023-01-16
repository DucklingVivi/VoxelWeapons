package com.ducklingvivi.voxelweapons.dimensions;


import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;


public class Dimensions {



    public static final ResourceKey<Level> VOXELDIMENSION = ResourceKey.create(Registries.DIMENSION, new ResourceLocation(voxelweapons.MODID, "voxel"));
    public static final ResourceKey<DimensionType> VOXELDIMENSIONTYPE = ResourceKey.create(Registries.DIMENSION_TYPE, new ResourceLocation(voxelweapons.MODID, "voxel_type"));
    public static final ResourceKey<Biome> VOXELBIOME = ResourceKey.create(Registries.BIOME, new ResourceLocation(voxelweapons.MODID, "voxelbiome"));


    public static void register(){



        voxelweapons.LOGGER.info("Registration");
        Registry.register(BuiltInRegistries.CHUNK_GENERATOR, new ResourceLocation(voxelweapons.MODID, "voxel_chunkgen"), VoxelChunkGenerator.CODEC);
        Registry.register(BuiltInRegistries.BIOME_SOURCE, new ResourceLocation(voxelweapons.MODID, "biomes"), VoxelBiomeProvider.CODEC);

    }




}
