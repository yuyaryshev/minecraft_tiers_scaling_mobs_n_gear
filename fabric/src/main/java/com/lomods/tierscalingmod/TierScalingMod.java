package com.lomods.tierscalingmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.fabricmc.fabric.api.event.registry.RegistryEntryAddedCallback;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.core.Registry;
import net.minecraft.world.item.Item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;


import net.fabricmc.api.ModInitializer;

public class TierScalingMod implements ModInitializer {

    @Override
    public void onInitialize() {
        // Log initialization
        Constants.LOG.info("Hello Fabric world!");
        CommonClass.init();

        // Register the TierScaling command
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            TierScalingCommand.register(dispatcher);
            AngelicModeCommand.register(dispatcher);
        });

        ServerEntityEvents.ENTITY_LOAD.register(MobUtils::onEntityLoad);

        // Register a tick event to check player game mode
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            // Iterate over all players
            for (Player player : server.getPlayerList().getPlayers()) {
                CommonClass.onPlayerTick(player);
            }
        });
    }
}