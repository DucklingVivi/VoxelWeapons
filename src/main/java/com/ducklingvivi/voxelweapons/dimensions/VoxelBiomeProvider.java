package com.ducklingvivi.voxelweapons.dimensions;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class VoxelBiomeProvider extends BiomeSource {


    public static final Codec<VoxelBiomeProvider> CODEC = RegistryOps.retrieveRegistryLookup(Registries.BIOME)
            .xmap(VoxelBiomeProvider::new, VoxelBiomeProvider::getBiomeRegistry).codec();

    private static final List<ResourceKey<Biome>> SPAWN = Collections.singletonList(Dimensions.VOXELBIOME);
    private final Holder<Biome> biome;
    private final HolderLookup.RegistryLookup<Biome> biomeRegistry;


    public VoxelBiomeProvider(HolderLookup.RegistryLookup<Biome> biomeRegistry){
        super(getStartBiomes(biomeRegistry));
        this.biomeRegistry = biomeRegistry;
        biome = biomeRegistry.getOrThrow(Dimensions.VOXELBIOME);
    }

    private static List<Holder<Biome>> getStartBiomes(HolderLookup.RegistryLookup<Biome> registry) {
        return SPAWN.stream().map(s -> registry.getOrThrow(ResourceKey.create(Registries.BIOME, s.location()))).collect(Collectors.toList());
    }

    public HolderLookup.RegistryLookup<Biome> getBiomeRegistry(){
        return biomeRegistry;
    }

    @Override
    protected Codec<? extends BiomeSource> codec() {
        return null;
    }

    @Override
    public Holder<Biome> getNoiseBiome(int p_204238_, int p_204239_, int p_204240_, Climate.Sampler p_204241_) {
        return biome;
    }
}
