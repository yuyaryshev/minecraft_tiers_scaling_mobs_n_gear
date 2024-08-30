package com.lomods.tierscalingmod;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod(TierScalingMod.MOD_ID)
public class TierScalingMod {

    public static final String MOD_ID = "tierscalingmod";
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Map<UUID, Boolean> playerToggleMap = new HashMap<>();

    public TierScalingMod() {
        LOGGER.info("tierscalingmod started!");
        CommonClass.init();

        // Register the event handler
        MinecraftForge.EVENT_BUS.register(new PlayerEventHandler());
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        TierScalingCommand.register(dispatcher);
    }
}
