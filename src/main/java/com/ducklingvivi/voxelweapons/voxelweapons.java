package com.ducklingvivi.voxelweapons;

import com.ducklingvivi.voxelweapons.client.model.WeaponBakedModel;
import com.ducklingvivi.voxelweapons.client.render.ItemTooltip;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.setup.ModSetup;
import com.ducklingvivi.voxelweapons.setup.ModSetupClient;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelEvent;
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
        if(event.getItemStack().is(Registration.VOXEL_CATALYST_PREDICATE)){
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
            ModelResourceLocation itemModelResourceLocation00 = new ModelResourceLocation(Registration.VOXEL_WEAPON_STARTER.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation01 = new ModelResourceLocation(Registration.VOXEL_WEAPON_OVERWORLD.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation02 = new ModelResourceLocation(Registration.VOXEL_WEAPON_NETHER.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation03 = new ModelResourceLocation(Registration.VOXEL_WEAPON_END.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation04 = new ModelResourceLocation(Registration.VOXEL_WEAPON_BOSS.getId(), "inventory");

            ModelResourceLocation itemModelResourceLocation10 = new ModelResourceLocation(Registration.VOXEL_CATALYST_STARTER.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation11 = new ModelResourceLocation(Registration.VOXEL_CATALYST_OVERWORLD.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation12 = new ModelResourceLocation(Registration.VOXEL_CATALYST_NETHER.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation13 = new ModelResourceLocation(Registration.VOXEL_CATALYST_END.getId(), "inventory");
            ModelResourceLocation itemModelResourceLocation14 = new ModelResourceLocation(Registration.VOXEL_CATALYST_BOSS.getId(), "inventory");


            WeaponBakedModel customModel = new WeaponBakedModel();
            event.getModels().replace(itemModelResourceLocation00, customModel);
            event.getModels().replace(itemModelResourceLocation01, customModel);
            event.getModels().replace(itemModelResourceLocation02, customModel);
            event.getModels().replace(itemModelResourceLocation03, customModel);
            event.getModels().replace(itemModelResourceLocation04, customModel);
            event.getModels().replace(itemModelResourceLocation10, customModel);
            event.getModels().replace(itemModelResourceLocation11, customModel);
            event.getModels().replace(itemModelResourceLocation12, customModel);
            event.getModels().replace(itemModelResourceLocation13, customModel);
            event.getModels().replace(itemModelResourceLocation14, customModel);


        }

    }
}
