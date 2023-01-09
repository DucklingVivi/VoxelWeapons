package com.ducklingvivi.voxelweapons.library;

import com.ducklingvivi.voxelweapons.voxelweapons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class Messages {
    private static final String PROTOCOL_VERSION = "1";
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

        net.messageBuilder(WeaponRequestPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(WeaponRequestPacket::new)
                .encoder(WeaponRequestPacket::toBytes)
                .consumerMainThread(WeaponRequestPacket::handle)
                .add();

        net.messageBuilder(WeaponRequestPacket.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(WeaponRequestPacket::new)
                .encoder(WeaponRequestPacket::toBytes)
                .consumerMainThread(WeaponRequestPacket::handle)
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

}
