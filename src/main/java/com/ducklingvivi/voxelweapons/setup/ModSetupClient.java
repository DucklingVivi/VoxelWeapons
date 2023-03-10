package com.ducklingvivi.voxelweapons.setup;


import com.ducklingvivi.voxelweapons.client.data.VoxelCreatorClientData;
import com.ducklingvivi.voxelweapons.client.data.VoxelDataClient;
import com.ducklingvivi.voxelweapons.client.render.LineBoxRenderer;
import com.ducklingvivi.voxelweapons.client.render.OriginRenderer;
import com.ducklingvivi.voxelweapons.client.render.VoxelControllerRenderer;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelWeaponItem;
import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.ducklingvivi.voxelweapons.networking.DimensionBuildPacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = voxelweapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetupClient {


    public static void init(FMLClientSetupEvent event) {

        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::tickEvent);
        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::renderEvent);
        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::LeftClickEmptyEvent);
        MinecraftForge.EVENT_BUS.addListener(ModSetupClient::onApplyTooltips);
        event.enqueueWork(VoxelControllerRenderer::register);

    }


    public static void onApplyTooltips(ItemTooltipEvent event){
        ItemStack item = event.getItemStack();
//        if(item.is(Registration.VOXELCATALYSTITEM.get())){
//
//        }
        if(item.getItem() instanceof VoxelWeaponItem voxelWeaponItem){
            CompoundTag tag = item.getOrCreateTag();
            if(!tag.hasUUID("voxelUUID")) return;
            final UUID uuid = tag.getUUID("voxelUUID");
            final VoxelData data = VoxelSavedData.get().getData(uuid);
            event.getToolTip().set(1, Component.translatable("item.voxelweapons.tier").append(": ").append(Component.translatable(voxelWeaponItem.tier.languageKey).withStyle(style -> style.withColor(voxelWeaponItem.tier.color))));
        }
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
                    AABB aabb = VoxelCreatorClientData.INSTANCE.getTier().boundingBox;
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
