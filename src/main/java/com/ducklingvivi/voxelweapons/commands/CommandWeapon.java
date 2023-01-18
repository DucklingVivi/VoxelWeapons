package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.dimensions.VoxelChunkGenerator;
import com.ducklingvivi.voxelweapons.library.VoxelCreatorSavedData;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelFloorBorderBlock;
import com.ducklingvivi.voxelweapons.library.VoxelSavedData;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.UUID;

public class CommandWeapon {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("weapon")
                .requires(cs -> cs.hasPermission(1))
                .then(CommandGenerateWeapon.register(dispatcher))
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

            AABB boundingbox = new AABB(new BlockPos(-10,1,-10));
            boundingbox = boundingbox.minmax(new AABB(new BlockPos(10,40,10)));
            BlockPos pos = new BlockPos(boundingbox.maxX+4.5f,1,boundingbox.getCenter().z);
            VoxelChunkGenerator.Settings settings = new VoxelChunkGenerator.Settings(new VoxelChunkGenerator.FloorSettings((int)boundingbox.minX,(int)boundingbox.maxX,(int)boundingbox.minZ,(int)boundingbox.maxZ),pos.getX(),pos.getZ());
            UUID uuid = UUID.randomUUID();
            ServerLevel level = VoxelSavedData.get().CreateDimension(uuid,settings);
            ServerPlayer player = context.getSource().getPlayer();

            VoxelCreatorSavedData.get(level).setBoundingBox(boundingbox);
            level.getDataStorage().save();
            
            
            
            player.teleportTo(level, pos.getCenter().x,pos.getY(),pos.getCenter().z,90,0f);

            return 0;
        }
    }

    private static class CommandGenerateWeapon implements Command<CommandSourceStack> {

        private static final CommandGenerateWeapon CMD = new CommandGenerateWeapon();

        public static ArgumentBuilder<CommandSourceStack,?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
            return Commands.literal("generate")
                    .requires(cs -> cs.hasPermission(1))
                    .then(Commands.argument("pos0",BlockPosArgument.blockPos())
                            .then(Commands.argument("pos1",BlockPosArgument.blockPos())
                                    .then(Commands.argument("origin",BlockPosArgument.blockPos())
                                            .executes(CMD))));

        }


        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            BlockPos start = BlockPosArgument.getLoadedBlockPos(context,"pos0");
            BlockPos end = BlockPosArgument.getLoadedBlockPos(context,"pos1");
            BlockPos origin = BlockPosArgument.getLoadedBlockPos(context,"origin");

            UUID uuid = UUID.randomUUID();
            VoxelData data = new VoxelData();


            data.offset = origin;

            data.devAddRange(start,end, context.getSource().getLevel());

            VoxelSavedData.get().addData(uuid,data);

            ItemStack item = Registration.VOXELWEAPONITEM.get().getDefaultInstance();
            CompoundTag tag =  item.getOrCreateTag();
            tag.putUUID("voxelUUID", uuid);
            item.setTag(tag);
            context.getSource().getPlayer().addItem(item);
            return 0;
        }

    }

}

