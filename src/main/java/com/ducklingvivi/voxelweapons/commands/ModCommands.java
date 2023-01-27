package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class ModCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralCommandNode<CommandSourceStack> commands = dispatcher.register(
                Commands.literal(voxelweapons.MODID)
                        .then(CommandDimension.register(dispatcher))
        );
        dispatcher.register(Commands.literal("voxel").redirect(commands));
    }
}
