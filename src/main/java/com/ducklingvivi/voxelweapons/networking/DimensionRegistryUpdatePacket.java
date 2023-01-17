package com.ducklingvivi.voxelweapons.networking;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class DimensionRegistryUpdatePacket {

    private Set<ResourceKey<Level>> newDims;
    private Set<ResourceKey<Level>> removedDims;



    public DimensionRegistryUpdatePacket(Set<ResourceKey<Level>> newDims,Set<ResourceKey<Level>> removedDims) {
        this.newDims = newDims;
        this.removedDims = removedDims;
    }

    public DimensionRegistryUpdatePacket(FriendlyByteBuf buf) {
        newDims = new HashSet<>();
        removedDims = new HashSet<>();

        final int newDimensionCount = buf.readVarInt();
        for (int i = 0; i < newDimensionCount; i++) {
            final ResourceLocation worldID = buf.readResourceLocation();
            newDims.add(ResourceKey.create(Registries.DIMENSION, worldID));
        }

        final int removedDimensionCount = buf.readVarInt();
        for (int i = 0; i < removedDimensionCount; i++) {
            final ResourceLocation worldID = buf.readResourceLocation();
            removedDims.add(ResourceKey.create(Registries.DIMENSION, worldID));
        }

    }
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeVarInt(this.newDims.size());
        for (ResourceKey<Level> key : this.newDims) {
            buf.writeResourceLocation(key.location());
        }

        buf.writeVarInt(this.removedDims.size());
        for (ResourceKey<Level> key : this.removedDims) {
            buf.writeResourceLocation(key.location());
        }
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if(player != null){
                Set<ResourceKey<Level>> commandAutofillLevels = player.connection.levels();

                commandAutofillLevels.addAll(this.newDims);
                for (ResourceKey<Level> resourceKey: this.removedDims) {
                    commandAutofillLevels.remove(resourceKey);
                }
            }
        });
        return true;
    }

}
