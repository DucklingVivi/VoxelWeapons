package com.ducklingvivi.voxelweapons;

import com.ducklingvivi.voxelweapons.client.model.VoxelDataClient;
import com.ducklingvivi.voxelweapons.client.model.WeaponBakedModel;
import com.ducklingvivi.voxelweapons.client.render.VoxelCreatorClientData;
import com.ducklingvivi.voxelweapons.library.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.networking.DimensionCreatorPacket;
import com.ducklingvivi.voxelweapons.networking.DimensionRegistryUpdatePacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.setup.ModSetup;
import com.ducklingvivi.voxelweapons.setup.ModSetupClient;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.StructureRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.slf4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(voxelweapons.MODID)
public class voxelweapons {
    // Define mod id in a common place for everything to reference
    public static final String MODID = "voxelweapons";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path

    public voxelweapons() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ModSetup::init);
        modEventBus.addListener(ModSetupClient::init);
        Messages.register();

        Registration.init();


        // Register ourselves for server and other game events we are interested in

        // Register the item to a creative tab

    }



    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code

    }



    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());

        }


        @SubscribeEvent
        public static void testing(EntityRenderersEvent.RegisterRenderers event){

        }
        @SubscribeEvent
        public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event){
            voxelweapons.LOGGER.info("Entered Bakery");
            ModelResourceLocation itemModelResourceLocation = WeaponBakedModel.modelResourceLocation;
            WeaponBakedModel customModel = new WeaponBakedModel();
            event.getModels().replace(itemModelResourceLocation, customModel);
        }

    }
}
