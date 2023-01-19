package com.ducklingvivi.voxelweapons.setup;


import com.ducklingvivi.voxelweapons.client.model.VoxelDataClient;
import com.ducklingvivi.voxelweapons.client.render.*;
import com.ducklingvivi.voxelweapons.commands.ModCommands;
import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.library.VoxelSavedData;
import com.ducklingvivi.voxelweapons.networking.DimensionBuildPacket;
import com.ducklingvivi.voxelweapons.networking.DimensionCreatorPacket;
import com.ducklingvivi.voxelweapons.networking.DimensionRegistryUpdatePacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.commands.CommandSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;

@Mod.EventBusSubscriber(modid = voxelweapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetupClient {


    public static void init(FMLClientSetupEvent event) {

        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::tickEvent);
        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::renderEvent);
        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::LeftClickEmptyEvent);

        event.enqueueWork(() -> {
            VoxelControllerRenderer.register();
        });

    }

    private static void renderEvent(RenderLevelStageEvent event) {
        if (event.getStage() == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            if (Minecraft.getInstance().level.dimension().location().getNamespace().equals(voxelweapons.MODID)) {
                boolean flag = false;

                Tesselator tesselator = Tesselator.getInstance();
                PoseStack poseStack = event.getPoseStack();
                Camera cam = event.getCamera();

                for (ItemStack stack : Minecraft.getInstance().player.getHandSlots()) {
                    if (stack.is(Items.ENDER_EYE)) {
                        flag = true;
                    }
                }
                if (flag || VoxelCreatorClientData.INSTANCE.isOriginVisible) {
                    OriginRenderer.render(tesselator, poseStack, cam);
                }

                if (VoxelCreatorClientData.INSTANCE.isWireFrameVisible) {
                    AABB aabb = VoxelCreatorClientData.INSTANCE.getBoundingBox();
                    LineBoxRenderer.render(tesselator, poseStack, cam, aabb);

                }

//                AABB aabb1 = aabb.inflate(10);
//                ForceFieldRenderer.render(tesselator,poseStack,cam,aabb1);
            }
        }
    }

    private static void LeftClickEmptyEvent(PlayerInteractEvent.LeftClickEmpty event) {
        if (event.getSide() == LogicalSide.CLIENT) {
            VoxelControllerRenderer.MenuItem item = VoxelCreatorClientData.INSTANCE.currentMenuItem;
            if (item != null) {
                switch (item) {
                    case WIREFRAME -> {
                        VoxelCreatorClientData.INSTANCE.isWireFrameVisible = !VoxelCreatorClientData.INSTANCE.isWireFrameVisible;
                    }
                    case ORIGIN -> {
                        VoxelCreatorClientData.INSTANCE.isOriginVisible = !VoxelCreatorClientData.INSTANCE.isOriginVisible;
                    }
                    case FINISH -> {
                        if (VoxelCreatorClientData.INSTANCE.exitTimer <= 0) {
                            VoxelCreatorClientData.INSTANCE.exitTimer = VoxelCreatorClientData.BUILDTIMERTICKTIME;
                        } else {
                            Messages.sendToServer(new DimensionBuildPacket());
                        }
                    }
                }
            }
        }
    }

    private static void tickEvent(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            VoxelDataClient.tickAll();
            VoxelCreatorClientData.INSTANCE.exitTimer = Math.max(0,VoxelCreatorClientData.INSTANCE.exitTimer-1);
        }
    }
}
