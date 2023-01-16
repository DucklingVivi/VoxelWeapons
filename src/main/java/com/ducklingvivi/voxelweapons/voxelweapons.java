package com.ducklingvivi.voxelweapons;

import com.ducklingvivi.voxelweapons.client.model.VoxelDataClient;
import com.ducklingvivi.voxelweapons.client.model.WeaponBakedModel;
import com.ducklingvivi.voxelweapons.library.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.networking.DimensionCreatorPacket;
import com.ducklingvivi.voxelweapons.networking.Messages;
import com.ducklingvivi.voxelweapons.setup.ModSetup;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ServerboundUseItemOnPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.server.ServerLifecycleHooks;
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

        Messages.register();

        Registration.init();


        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.addListener(this::tickEvent);
        MinecraftForge.EVENT_BUS.addListener(this::onUseItem);
        MinecraftForge.EVENT_BUS.addListener(this::onLogin);
        // Register the item to a creative tab

    }



    private void commonSetup(FMLCommonSetupEvent event) {
        // Some common setup code

    }

    private void tickEvent(TickEvent.ClientTickEvent event){
        if(event.phase == TickEvent.Phase.END){
            VoxelDataClient.tickAll();
        }
    }

    private void onLogin(PlayerEvent.PlayerLoggedInEvent event){

        if(event.getEntity().level.dimension().location().getNamespace().equals(voxelweapons.MODID)){
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            ServerLevel level = server.getLevel(event.getEntity().level.dimension());
            ServerPlayer player = server.getPlayerList().getPlayer(event.getEntity().getUUID());
            assert level != null;
            CompoundTag tag = VoxelCreatorSavedData.get(level).save(new CompoundTag());

            Messages.sendToPlayer(new DimensionCreatorPacket(DimensionCreatorPacket.DimensionCreatorOperation.SYNCALL, tag), player);
        }
    }
    private void onUseItem(PlayerInteractEvent.RightClickBlock event){
        if(event.getSide() == LogicalSide.SERVER){
            if(event.getItemStack().is(Items.ENDER_EYE)){
                if(event.getLevel().dimension().location().getNamespace().equals(voxelweapons.MODID)){
                    MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
                    ServerLevel level =server.getLevel(event.getLevel().dimension());
                    ServerPlayer player = server.getPlayerList().getPlayer(event.getEntity().getUUID());

                    assert level != null;
                    VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(level);
                    savedData.setOrigin(event.getPos());

                    CompoundTag tag = savedData.save(new CompoundTag());
                    Messages.sendToPlayer(new DimensionCreatorPacket(DimensionCreatorPacket.DimensionCreatorOperation.SYNCORIGIN, tag), player);
                };
            }
        }
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
