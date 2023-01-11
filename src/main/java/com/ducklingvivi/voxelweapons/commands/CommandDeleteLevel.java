package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.concurrent.CompletableFuture;

public class CommandDeleteLevel implements Command<CommandSourceStack> {

    private static final CommandDeleteLevel CMD = new CommandDeleteLevel();
    private static final SuggestionProvider<CommandSourceStack> SUGGESTION_PROVIDER = new CommandDeleteLevel.CommandDimensionProvider();
    public static ArgumentBuilder<CommandSourceStack,?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("delete")
                .requires(cs -> cs.hasPermission(1))
                .then(Commands.argument("name", StringArgumentType.word())
                        .suggests(SUGGESTION_PROVIDER)
                        .executes(CMD));
    }

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        String name = context.getArgument("name", String.class);
        DimensionUtils.deleteWorld(ServerLifecycleHooks.getCurrentServer(), name.toLowerCase());
        return 0;
    }

    private static class CommandDimensionProvider implements SuggestionProvider<CommandSourceStack>{


        @Override
        public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) throws CommandSyntaxException {
            for (String str : DimensionUtils.GetDimensionStrings()) {
                builder.suggest(str);
            }
            return builder.buildFuture();
        }
    }
}