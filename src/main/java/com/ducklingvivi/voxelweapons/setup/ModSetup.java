package com.ducklingvivi.voxelweapons.setup;


import com.ducklingvivi.voxelweapons.commands.ModCommands;
import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.library.VoxelInterface;
import com.ducklingvivi.voxelweapons.voxelweapons;

import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = voxelweapons.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ModSetup {

    @SubscribeEvent
    public void onRegisterCapabilitiesEvent(RegisterCapabilitiesEvent event) {
       event.register(VoxelCapability.class);
    }

    public static void onRegisterCommandEvent(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher());
    }
    public static void init(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Dimensions.register();
        });
    }
    public static void clientInit(FMLClientSetupEvent event) {

    }
}
