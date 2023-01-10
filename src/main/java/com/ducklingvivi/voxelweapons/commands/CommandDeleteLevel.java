package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.ServerLifecycleHooks;

public class CommandDeleteLevel implements Command<CommandSourceStack> {

    private static final CommandDeleteLevel CMD = new CommandDeleteLevel();
    public static ArgumentBuilder<CommandSourceStack,?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("delete")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.word())
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = context.getArgument("name", String.class);
        DimensionUtils.deleteWorld(ServerLifecycleHooks.getCurrentServer(), name.toLowerCase());
        return 0;
    }
}