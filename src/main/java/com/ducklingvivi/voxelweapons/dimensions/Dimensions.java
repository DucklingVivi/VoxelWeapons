package com.ducklingvivi.voxelweapons.dimensions;


import com.ducklingvivi.voxelweapons.library.Voxel;
import com.ducklingvivi.voxelweapons.library.VoxelItem;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;


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
