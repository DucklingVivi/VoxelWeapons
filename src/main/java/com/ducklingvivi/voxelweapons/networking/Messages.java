package com.ducklingvivi.voxelweapons.networking;

import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class Messages {
    public static SimpleChannel INSTANCE;

    private static int packetId = 0;
    private static int id() {
        return packetId++;
    }


    public static void register(){
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(voxelweapons.MODID, "messages"))
                .networkProtocolVersion(()-> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();
        INSTANCE = net;

        net.messageBuilder(WeaponPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(WeaponPacket::new)
                .encoder(WeaponPacket::toBytes)
                .consumerNetworkThread(WeaponPacket::handle)
                .add();

        net.messageBuilder(WeaponRequestPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(WeaponRequestPacket::new)
                .encoder(WeaponRequestPacket::toBytes)
                .consumerNetworkThread(WeaponRequestPacket::handle)
                .add();


        net.messageBuilder(DimensionRegistryUpdatePacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DimensionRegistryUpdatePacket::new)
                .encoder(DimensionRegistryUpdatePacket::toBytes)
                .consumerNetworkThread(DimensionRegistryUpdatePacket::handle)
                .add();

        net.messageBuilder(DimensionCreatorPacket.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(DimensionCreatorPacket::new)
                .encoder(DimensionCreatorPacket::toBytes)
                .consumerMainThread(DimensionCreatorPacket::handle)
                .add();
    }

    public static <MSG> void sendToServer(MSG message) {
        INSTANCE.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static <MSG> void sendToAllPlayers(MSG message) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), message);
    }
    public static <MSG> void sendToAllPlayersInLevel(MSG message, Level level) {
        INSTANCE.send(PacketDistributor.DIMENSION.with(level::dimension), message);
    }

}
