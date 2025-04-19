package com.lomods.tierscalingmod;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.commands.arguments.GameProfileArgument;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.lomods.tierscalingmod.mixin.ServerPlayerMixin;
import java.util.Collection;

public class AngelicModeCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("angelicmode")
                .requires(source -> source.hasPermission(2)) // Require OP permission level 2
                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                        .executes(context -> {
                            try{
                                ServerPlayer player = getPlayerFromContext(context);
                                if (player != null) {
                                    toggleAngelicMode(context.getSource(), player);
                                }
                            } catch (Exception e) {
                                logAndSendError(context.getSource(), e);
                            }
                            return 1;
                        })
                        .then(Commands.argument("mode", BoolArgumentType.bool()) // Optional boolean argument
                                .executes(context -> {
                                    try{
                                        ServerPlayer player = getPlayerFromContext(context);
                                        if (player != null) {
                                            boolean mode = BoolArgumentType.getBool(context, "mode");
                                            setAngelicMode(context.getSource(), player, mode);
                                        }
                                    } catch (Exception e) {
                                        logAndSendError(context.getSource(), e);
                                    }
                                    return 1;
                                })
                        )
                )
        );
    }

    // Log the exception and send an error message to the player/console
    private static void logAndSendError(CommandSourceStack source, Exception e) {
        e.printStackTrace(); // This will log the full stack trace to the server console
        source.sendFailure(Component.literal("An error occurred: " + e.getMessage()));
    }

    // Extracted common logic to retrieve ServerPlayer from context
    private static ServerPlayer getPlayerFromContext(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Collection<GameProfile> gameProfiles = GameProfileArgument.getGameProfiles(context, "player");
        GameProfile gameProfile = gameProfiles.iterator().next();
        ServerPlayer player = context.getSource().getServer().getPlayerList().getPlayer(gameProfile.getId());

        if (player == null) {
            context.getSource().sendFailure(Component.literal("Player not found!"));
        }

        return player;
    }

    // Toggle the angelic_mode field by accessing the ServerPlayerMixin
    private static void toggleAngelicMode(CommandSourceStack source, ServerPlayer player) {
        ServerPlayerAcessor playerMixin = (ServerPlayerAcessor)(Object) player; // Cast to the mixin class
        boolean currentMode = playerMixin.isAngelicMode();
        playerMixin.setAngelicMode(!currentMode); // Toggle the mode

        String status = playerMixin.isAngelicMode() ? "enabled" : "disabled";
        source.sendSuccess(() -> Component.literal("Angelic Mode " + status + " for " + player.getName().getString()), true);
    }

    // Set the angelic_mode field directly by accessing the ServerPlayerMixin
    private static void setAngelicMode(CommandSourceStack source, ServerPlayer player, boolean mode) {
        ServerPlayerAcessor playerMixin = (ServerPlayerAcessor) (Object)player; // Cast to the mixin class
        playerMixin.setAngelicMode(mode); // Set the mode directly

        String status = mode ? "enabled" : "disabled";
        source.sendSuccess(() -> Component.literal("Angelic Mode " + status + " for " + player.getName().getString()), true);
    }


    private static void toggleAngelicMode_OLD(CommandSourceStack source, ServerPlayer player) {
        PlayerDataHelper.toggleAngelicMode(player);
        source.sendSuccess(() -> Component.literal("Angelic Mode toggled for " + player.getName().getString()), true);
    }

    private static void setAngelicMode_OLD(CommandSourceStack source, ServerPlayer player, boolean mode) {
        PlayerDataHelper.setAngelicMode(player, mode);
        String status = mode ? "enabled" : "disabled";
        source.sendSuccess(() -> Component.literal("Angelic Mode " + status + " for " + player.getName().getString()), true);
    }
}
