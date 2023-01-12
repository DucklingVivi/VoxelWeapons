package com.ducklingvivi.voxelweapons.dimensions;

import com.ducklingvivi.voxelweapons.networking.DimensionRegistryUpdatePacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.mojang.serialization.Lifecycle;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RegistryLayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.progress.ChunkProgressListener;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;

public class DimensionUtils {


    public static boolean tryDeleteDimension(MinecraftServer server, ResourceKey<Level> toDelete) {
        if (server == null) return false;
        @SuppressWarnings("deprecation")

        var item = server.getAllLevels();
        ServerLevel level = server.getLevel(toDelete);
        if (level == null) return false;

        Path storageFolder = DimensionType.getStorageFolder(toDelete, server.getWorldPath(LevelResource.ROOT));
        File dir = storageFolder.toFile();
        if (!dir.exists() || !dir.isDirectory()) {
            voxelweapons.LOGGER.warn("DimensionUtils.tryDeleteDimension: Failed to get the directory for dimension {}", toDelete.location());
            return false;
        }
        try {
            unregisterLevel(server, toDelete);
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            voxelweapons.LOGGER.warn("DimensionUtils.tryDeleteDimension: Failed to delete the directory for dimension {}", toDelete.location());
        }

        return true;
    }


    public static ResourceKey<Level> getLevelKey(ResourceLocation id) {
        return ResourceKey.create(Registries.DIMENSION, id);
    }

    public static void deleteWorld(MinecraftServer minecraftServer, String name) {
        ResourceLocation id = new ResourceLocation(voxelweapons.MODID, name);
        ResourceKey<Level> key = getLevelKey(id);
        tryDeleteDimension(minecraftServer, key);
    }

    public static ServerLevel createWorld(MinecraftServer minecraftServer, String name) {

        ResourceLocation id = new ResourceLocation(voxelweapons.MODID, name);

        ResourceKey<Level> key = getLevelKey(id);

        RegistryAccess registryAccess = minecraftServer.registryAccess();

        Holder<DimensionType> type = registryAccess.registryOrThrow(Registries.DIMENSION_TYPE).getHolderOrThrow(Dimensions.VOXELDIMENSIONTYPE);

        ServerLevel result = getOrCreateLevel(minecraftServer, key, (server, registryKey) -> {
            ChunkGenerator generator = new VoxelChunkGenerator(registryAccess.registryOrThrow(Registries.BIOME).asLookup());
            return new LevelStem(type, generator);
        });


        return result;
    }


    public static ServerLevel getOrCreateLevel(final MinecraftServer server, final ResourceKey<Level> levelkey, final BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory) {
        @SuppressWarnings("deprecation") final Map<ResourceKey<Level>, ServerLevel> map = server.forgeGetWorldMap();

        final ServerLevel existingLevel = map.get(levelkey);
        if (existingLevel != null) {
            return existingLevel;
        }

        return createAndRegisterLevel(server, map, levelkey, dimensionFactory);
    }


    private static ServerLevel createAndRegisterLevel(final MinecraftServer server, final Map<ResourceKey<Level>, ServerLevel> map, final ResourceKey<Level> worldKey, final BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory) {

        final ResourceKey<LevelStem> dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, worldKey.location());
        final LevelStem dimension = dimensionFactory.apply(server, dimensionKey);
        final ChunkProgressListener chunkProgressListener = server.progressListenerFactory.create(11);
        final Executor executor = server.executor;
        final LevelStorageAccess anvilConverter = server.storageSource;
        final WorldData worldData = server.getWorldData();
        final DerivedLevelData derivedLevelData = new DerivedLevelData(worldData, worldData.overworldData());

        Registry<LevelStem> dimensionRegistry = server.registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM);


        voxelweapons.LOGGER.info(dimensionRegistry.keySet().toString());
       if (dimensionRegistry instanceof MappedRegistry<LevelStem> writableRegistry) {
            //THIS FEELS VILE
            writableRegistry.unfreeze();
       }
       Registry.register(dimensionRegistry, dimensionKey, dimension);

        final ServerLevel newWorld = new ServerLevel(
                server,
                executor,
                anvilConverter,
                derivedLevelData,
                worldKey,
                dimension,
                chunkProgressListener,
                false,
                0L,
                ImmutableList.of(),
                false
        );




        map.put(worldKey, newWorld);

        server.markWorldsDirty();

        MinecraftForge.EVENT_BUS.post(new LevelEvent.Load(newWorld));

        Messages.sendToAllPlayers(new DimensionRegistryUpdatePacket(ImmutableSet.of(worldKey), ImmutableSet.of()));

        return newWorld;
    }
    @SuppressWarnings("unchecked deprecated")
    private static void unregisterLevel(MinecraftServer server, ResourceKey<Level> levelKey) {
        ServerLevel level = server.getLevel(levelKey);
        assert level != null;
        if (!level.players().isEmpty()) {

            for (ServerPlayer player : Lists.newArrayList(level.players())) {
                ResourceKey<Level> respawnKey = player.getRespawnDimension();
                final ServerLevel destLevel = server.getLevel(respawnKey);
                BlockPos pos = player.getRespawnPosition();
                if (pos == null) {
                    assert destLevel != null;
                    pos = destLevel.getSharedSpawnPos();
                }
                player.teleportTo(destLevel, pos.getX(), pos.getY(), pos.getZ(), player.getRespawnAngle(), 0f);
            }
        }



        LayeredRegistryAccess<RegistryLayer> registries = server.registries();
        RegistryAccess.ImmutableRegistryAccess composite = (RegistryAccess.ImmutableRegistryAccess)registries.composite;

        Map<? extends ResourceKey<?>,? extends Registry<?>> map = composite.registries;

        Map<ResourceKey<?>,Registry<?>> hashMap = new HashMap<>(map);

        ResourceKey<?> key = ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("root")),new ResourceLocation("dimension"));
        final MappedRegistry<LevelStem> oldRegistry = (MappedRegistry<LevelStem>)hashMap.get(key);
        Lifecycle oldLifecycle = oldRegistry.registryLifecycle;
        final MappedRegistry<LevelStem> newRegistry = new MappedRegistry<>(Registries.LEVEL_STEM, oldLifecycle, false);
        for (var entry : oldRegistry.entrySet()) {

            final ResourceKey<LevelStem> oldKey = entry.getKey();
            final ResourceKey<Level> oldLevelKey = ResourceKey.create(Registries.DIMENSION, oldKey.location());
            final LevelStem dimension = entry.getValue();
            if(dimension != null && oldLevelKey != levelKey){
                Registry.register(newRegistry, oldKey, dimension);
            }
        }
        hashMap.replace(key, newRegistry);

        composite.registries = (Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>>) hashMap;


        Registry<LevelStem> dimensionRegistry = server.registries().compositeAccess().registryOrThrow(Registries.LEVEL_STEM);


        server.forgeGetWorldMap().remove(levelKey);



        MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.level.LevelEvent.Unload(level));


        server.markWorldsDirty();

        Messages.sendToAllPlayers(new DimensionRegistryUpdatePacket(ImmutableSet.of(), ImmutableSet.of(levelKey)));
    }
//TODO FIX THIS INSTANTLY
    public static List<String> GetDimensionStrings(){
        List<String> retList = new ArrayList<>();
        Set<ResourceKey<Level>> levels;
        Set<ResourceKey<Level>> level1 = DistExecutor.unsafeCallWhenOn(Dist.CLIENT,() -> DimensionUtils::getLevelSetClient);
        Set<ResourceKey<Level>> level2 = DistExecutor.unsafeCallWhenOn(Dist.DEDICATED_SERVER,() -> DimensionUtils::getLevelSetServer);
        levels = level1 != null ? level1 :level2;
        for (ResourceKey<Level> level: levels) {
            if(level.location().getNamespace()==voxelweapons.MODID){
                retList.add(level.location().getPath());
            }
        }
        return retList;
    }
    private static Set<ResourceKey<Level>> getLevelSetClient(){
        return Minecraft.getInstance().player.connection.levels();
    }
    private static Set<ResourceKey<Level>> getLevelSetServer(){
        return ServerLifecycleHooks.getCurrentServer().levelKeys();
    }
}
