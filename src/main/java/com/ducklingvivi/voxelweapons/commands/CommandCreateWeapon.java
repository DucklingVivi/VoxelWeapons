package com.ducklingvivi.voxelweapons.commands;

import com.ducklingvivi.voxelweapons.dimensions.DimensionUtils;
import com.ducklingvivi.voxelweapons.library.VoxelData;
import com.ducklingvivi.voxelweapons.library.VoxelSavedData;
import com.ducklingvivi.voxelweapons.setup.Registration;
import com.ducklingvivi.voxelweapons.voxelweapons;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
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
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.joml.Vector3f;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CommandCreateWeapon implements Command<CommandSourceStack> {

    private static final CommandCreateWeapon CMD = new CommandCreateWeapon();

    public static ArgumentBuilder<CommandSourceStack,?> register(CommandDispatcher<CommandSourceStack> dispatcher) {
        return Commands.literal("weapon")
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
