package com.ducklingvivi.voxelweapons.setup;


import com.ducklingvivi.voxelweapons.commands.ModCommands;
import com.ducklingvivi.voxelweapons.dimensions.Dimensions;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.commands.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

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
    }
}
