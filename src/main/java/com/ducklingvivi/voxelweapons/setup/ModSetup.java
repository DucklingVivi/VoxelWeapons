package com.ducklingvivi.voxelweapons.setup;



import com.ducklingvivi.voxelweapons.commands.ModCommands;
import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.VoxelCreatorSavedData;

import com.ducklingvivi.voxelweapons.networking.DimensionCreatorPacket;
import com.ducklingvivi.voxelweapons.networking.DimensionRegistryUpdatePacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.google.common.collect.ImmutableSet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.item.Items;

import net.minecraft.world.phys.AABB;

import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = voxelweapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    @SubscribeEvent
    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }

    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Dimensions.register();
        });

        MinecraftForge.EVENT_BUS.addListener(ModSetup::onUseItem);
        MinecraftForge.EVENT_BUS.addListener(ModSetup::onLogin);
        MinecraftForge.EVENT_BUS.addListener(ModSetup::onDimensionChangeServer);
        MinecraftForge.EVENT_BUS.addListener(ModSetup::onPlaceBlock);


    }



    private static void onLogin(PlayerEvent.PlayerLoggedInEvent event){
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        ServerPlayer player = server.getPlayerList().getPlayer(event.getEntity().getUUID());
        Messages.sendToPlayer(new DimensionRegistryUpdatePacket(server.levelKeys(), ImmutableSet.of()), player);

        if(event.getEntity().level.dimension().location().getNamespace().equals(voxelweapons.MODID)){

            ServerLevel level = server.getLevel(event.getEntity().level.dimension());

            assert level != null;
            CompoundTag tag = VoxelCreatorSavedData.get(level).save(new CompoundTag());

            Messages.sendToPlayer(new DimensionCreatorPacket(DimensionCreatorPacket.DimensionCreatorOperation.SYNCALL, tag), player);
        }
    }

    private static void onDimensionChangeServer(PlayerEvent.PlayerChangedDimensionEvent event){
        if(event.getTo().location().getNamespace() == voxelweapons.MODID){
            MinecraftServer server =  ServerLifecycleHooks.getCurrentServer();
            ServerLevel level = server.getLevel(event.getTo());
            CompoundTag tag = VoxelCreatorSavedData.get(level).save(new CompoundTag());
            ServerPlayer player = server.getPlayerList().getPlayer(event.getEntity().getUUID());
            Messages.sendToPlayer(new DimensionCreatorPacket(DimensionCreatorPacket.DimensionCreatorOperation.SYNCALL, tag), player);
        }
    }

    private static void onUseItem(PlayerInteractEvent.RightClickBlock event){
        if(event.getSide() == LogicalSide.SERVER){
            if(event.getItemStack().is(Items.ENDER_EYE)){
                if(event.getLevel().dimension().location().getNamespace().equals(voxelweapons.MODID)){
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    ServerLevel level =server.getLevel(event.getLevel().dimension());

                    assert level != null;
                    VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(level);
                    savedData.setOrigin(event.getPos());
                    CompoundTag tag = savedData.save(new CompoundTag());
                    Messages.sendToAllPlayersInLevel(new DimensionCreatorPacket(DimensionCreatorPacket.DimensionCreatorOperation.SYNCORIGIN, tag), level);

                };
            }
        }
    }
    private static void onPlaceBlock(BlockEvent.EntityPlaceEvent event){
        Entity entity = event.getEntity();
        if(entity !=null && entity.level.dimension().location().getNamespace().equals(voxelweapons.MODID)){
            AABB boundingBox = VoxelCreatorSavedData.get(Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(entity.level.dimension()))).getBoundingBox();
            if(!boundingBox.contains(event.getPos().getCenter())){
                event.setCanceled(true);
            }
        }
    }
}
