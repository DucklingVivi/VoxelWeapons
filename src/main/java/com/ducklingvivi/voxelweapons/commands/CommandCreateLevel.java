package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.CompletableFuture;

public class CommandCreateLevel implements Command<CommandSourceStack> {

    private static final CommandCreateLevel CMD = new CommandCreateLevel();

    public static ArgumentBuilder<CommandSourceStack,?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("create")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.word()).executes(CMD));

    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = context.getArgument("name", String.class);
        DimensionUtils.createWorld(ServerLifecycleHooks.getCurrentServer(), name.toLowerCase());
        return 0;
    }

}
