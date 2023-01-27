package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.dimensions.VoxelChunkGenerator;
import com.ducklingvivi.voxelweapons.library.VoxelTier;
import com.ducklingvivi.voxelweapons.library.data.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;

import java.util.UUID;

public class CommandWeapon {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("weapon")
                .requires(cs -> cs.hasPermission(1))
                .then(CommandCreateWeapon.register(dispatcher));
    }

    private static class CommandCreateWeapon implements Command<CommandSourceStack> {
        private static final CommandCreateWeapon CMD = new CommandCreateWeapon();
        public static ArgumentBuilder<CommandSourceStack,?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
            return Commands.literal("create")
                    .requires(cs -> cs.hasPermission(1))
                    .executes(CMD);

        }
        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {

            AABB boundingbox = VoxelTier.STARTER.boundingBox;
            BlockPos pos = new BlockPos(boundingbox.maxX+4.5f,1,boundingbox.getCenter().z);
            VoxelChunkGenerator.Settings settings = new VoxelChunkGenerator.Settings(new VoxelChunkGenerator.FloorSettings((int)boundingbox.minX,(int)boundingbox.maxX,(int)boundingbox.minZ,(int)boundingbox.maxZ),pos.getX(),pos.getZ());
            UUID uuid = UUID.randomUUID();
            ServerLevel level = VoxelSavedData.get().CreateDimension(uuid,settings);
            ServerPlayer player = context.getSource().getPlayer();
            VoxelCreatorSavedData savedData = VoxelCreatorSavedData.get(level);
            savedData.setOrigin(new BlockPos(0,0,0));
            savedData.setLevelOrigin(context.getSource().getLevel().dimension());
            savedData.setTier(VoxelTier.STARTER);
            savedData.setLevelOriginPos(player.blockPosition());
            level.getDataStorage().save();


            
            player.teleportTo(level, pos.getCenter().x,pos.getY(),pos.getCenter().z,90,0f);

            return 0;
        }
    }

}

