package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.library.data.VoxelSavedData;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.DimensionArgument;
import net.minecraft.server.level.ServerLevel;

public class CommandDimension {

    public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("dimension")
                .requires(cs -> cs.hasPermission(1))
                .then(CommandDeleteLevel.register(dispatcher));
    }



    private static class CommandDeleteLevel implements Command<CommandSourceStack> {

        private static final CommandDeleteLevel CMD = new CommandDeleteLevel();

        public static ArgumentBuilder<CommandSourceStack, ?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
            return Commands.literal("delete")
                    .requires(cs -> cs.hasPermission(1))
                    .then(Commands.argument("dimension", DimensionArgument.dimension())
                            .executes(CMD));
        }

        @Override
        public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
            ServerLevel level = DimensionArgument.getDimension(context,"dimension");
            if(!level.dimension().location().getNamespace().equals(voxelweapons.MODID)) return -1;
            VoxelSavedData.get().DeleteDimension(Integer.valueOf(level.dimension().location().getPath()));
            return 0;
        }

    }
}
