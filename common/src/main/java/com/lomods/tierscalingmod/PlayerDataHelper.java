package com.lomods.tierscalingmod;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerDataHelper {
    private static final EntityDataAccessor<Boolean> ANGELIC_MODE = SynchedEntityData.defineId(ServerPlayer.class, EntityDataSerializers.BOOLEAN);
    // new EntityDataAccessor<>(67277683, EntityDataSerializers.BOOLEAN);

    public static boolean getAngelicMode(ServerPlayer player) {
        return player.getEntityData().get(ANGELIC_MODE);
    }

    public static void setAngelicMode(ServerPlayer player, boolean value) {
        boolean previousValue = getAngelicMode(player);
        if (previousValue != value) {
            player.getEntityData().set(ANGELIC_MODE, value);
            announceAngelicModeChange(player, value);
        }
    }

    public static void toggleAngelicMode(ServerPlayer player) {
        setAngelicMode(player, !getAngelicMode(player));
    }

    private static void announceAngelicModeChange(ServerPlayer player, boolean isAngelicMode) {
        // Create a message to announce the change
        String mode = isAngelicMode ? "enabled" : "disabled";
        String message = player.getName().getString() + " has " + mode + " Angelic Mode!";

        // Send the message to all players in the server
        MinecraftServer server = player.getServer();
        if (server != null) {
            server.getPlayerList().broadcastSystemMessage(Component.literal(message), true );
        }
    }
}
