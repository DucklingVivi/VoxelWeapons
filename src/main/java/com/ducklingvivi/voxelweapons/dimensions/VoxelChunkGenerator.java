package com.ducklingvivi.voxelweapons.dimensions;

import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ChunkGeneratorStructureState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.Structure.StructureSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;


public class VoxelChunkGenerator extends ChunkGenerator {

    public static final Codec<VoxelChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    RegistryOps.retrieveRegistryLookup(Registries.BIOME).forGetter(VoxelChunkGenerator::getBiomeRegistry)
            ).apply(instance, VoxelChunkGenerator::new));

    public VoxelChunkGenerator(HolderLookup.RegistryLookup<Biome> registry){
        super(new VoxelBiomeProvider(registry));
    }


    public HolderLookup.RegistryLookup<Biome> getBiomeRegistry() {
        return ((VoxelBiomeProvider)biomeSource).getBiomeRegistry();
    }

    @Override
    protected Codec<? extends ChunkGenerator> codec() {
        return CODEC;
    }


    @Override
    public void applyCarvers(WorldGenRegion p_223043_, long p_223044_, RandomState p_223045_, BiomeManager p_223046_, StructureManager p_223047_, ChunkAccess p_223048_, GenerationStep.Carving p_223049_) {

    }

    @Override
    public void buildSurface(WorldGenRegion pLevel, StructureManager pStructureManager, RandomState pRandom, ChunkAccess pChunk) {
        BlockState floor = Registration.VOXELFLOORBLOCK.get().defaultBlockState();
        BlockState barrier = Blocks.BARRIER.defaultBlockState();
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        pChunk.getPos().getWorldPosition();
        int x,y,z;
        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                pChunk.setBlockState(pos.set(x,0,z),floor,false);
                pChunk.setBlockState(pos.set(x,255,z),barrier,false);
                BlockPos pos1 = pChunk.getPos().getWorldPosition().offset(x,0,z);
                if(Math.abs(pos1.getX()) > 50 || Math.abs(pos1.getZ()) > 50){
                    for (y = 0; y < 255; y++) {
                        pChunk.setBlockState(pos.set(x,y,z),barrier,false);
                    }
                }
            }
        }


    }


    @Override
    public void spawnOriginalMobs(WorldGenRegion p_62167_) {

    }


    @Override
    public int getGenDepth() {
        return 0;
    }

    @Override
    public CompletableFuture<ChunkAccess> fillFromNoise(Executor p_223209_, Blender p_223210_, RandomState p_223211_, StructureManager p_223212_, ChunkAccess chunkAccess) {
        return CompletableFuture.completedFuture(chunkAccess);
    }

    @Override
    public int getSeaLevel() {
        return 64;
    }

    @Override
    public int getMinY() {
        return 0;
    }

    @Override
    public int getBaseHeight(int p_223032_, int p_223033_, Heightmap.Types p_223034_, LevelHeightAccessor levelHeightAccessor, RandomState p_223036_) {
        return levelHeightAccessor.getMinBuildHeight();
    }

    @Override
    public NoiseColumn getBaseColumn(int p_223028_, int p_223029_, LevelHeightAccessor levelHeightAccessor, RandomState p_223031_) {
        return new NoiseColumn(levelHeightAccessor.getMinBuildHeight(), new BlockState[0]);
    }


    @Override
    public void addDebugScreenInfo(List<String> p_223175_, RandomState p_223176_, BlockPos p_223177_) {

    }
}
