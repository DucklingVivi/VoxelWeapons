package com.ducklingvivi.voxelweapons.setup;



import com.ducklingvivi.voxelweapons.client.render.ItemTooltip;
import com.ducklingvivi.voxelweapons.commands.ModCommands;
import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.EnderPearlHandler;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelTier;
import com.ducklingvivi.voxelweapons.library.data.VoxelCreatorSavedData;

import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.ducklingvivi.voxelweapons.networking.DimensionCreatorPacket;
import com.ducklingvivi.voxelweapons.networking.DimensionRegistryUpdatePacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.google.common.collect.ImmutableSet;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Position;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import net.minecraft.world.phys.AABB;

import net.minecraftforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;

import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Formattable;
import java.util.Objects;
import java.util.UUID;

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
        MinecraftForge.EVENT_BUS.addListener(ModSetup::onItemCraft);
        MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGH,EnderPearlHandler::onEnderPearlHit);

    }



    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGatherComponentsEvent(RenderTooltipEvent.GatherComponents event)
    {

    }

    private static void onItemCraft(PlayerEvent.ItemCraftedEvent event){
        if(event.getCrafting().is(Registration.VOXEL_CATALYST_STARTER.get())){
            Player entity = event.getEntity();
            Position pos = entity.position();
            if(entity.canTakeItem(Items.ENDER_PEARL.getDefaultInstance())){
                entity.addItem(Items.ENDER_PEARL.getDefaultInstance());
            }else{
                entity.getLevel().addFreshEntity(new ItemEntity(entity.level,pos.x(),pos.y(),pos.z(), Items.ENDER_PEARL.getDefaultInstance()));
            }
        }
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
        if(event.getTo().location().getNamespace().equals(voxelweapons.MODID)){
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
            AABB boundingBox = VoxelCreatorSavedData.get(Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer().getLevel(entity.level.dimension()))).getTier().boundingBox;
            if(!boundingBox.contains(event.getPos().getCenter())){
                event.setCanceled(true);
            }
        }
    }
}
