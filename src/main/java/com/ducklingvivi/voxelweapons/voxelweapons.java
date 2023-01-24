package com.ducklingvivi.voxelweapons;

import com.ducklingvivi.voxelweapons.client.model.WeaponBakedModel;
import com.ducklingvivi.voxelweapons.client.render.ItemTooltip;
import com.ducklingvivi.voxelweapons.library.VoxelCatalystItem;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.setup.ModSetup;
import com.ducklingvivi.voxelweapons.setup.ModSetupClient;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.mojang.datafixers.util.Either;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

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
        ItemTooltip.registerFactory();

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

    @SubscribeEvent
    public static void setTooltip(ItemTooltipEvent event){
        if(event.getItemStack().is(Registration.VOXELCATALYSTITEM.get())){
            event.getItemStack();
        }
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
        public static void onModelBakeEvent(ModelEvent.ModifyBakingResult event){
            voxelweapons.LOGGER.info("Entered Bakery");
            ModelResourceLocation itemModelResourceLocation = new ModelResourceLocation(Registration.VOXELWEAPONITEM.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation1 = new ModelResourceLocation(Registration.VOXELCATALYSTITEM.getId(), "inventory");
            WeaponBakedModel customModel = new WeaponBakedModel();
            event.getModels().replace(itemModelResourceLocation, customModel);
            event.getModels().replace(itemModelResourceLocation1, customModel);
        }

    }
}
