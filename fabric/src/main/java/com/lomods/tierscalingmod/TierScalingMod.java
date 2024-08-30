package com.lomods.tierscalingmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.world.entity.player.Player;

public class TierScalingMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Log initialization
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();

        // Register the TierScaling command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TierScalingCommand.register(dispatcher);
        });

        // Register a tick event to check player game mode
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Iterate over all players
            for (Player player : server.getPlayerList().getPlayers()) {
                CommonClass.onPlayerTick(player);
            }
        });
    }
}