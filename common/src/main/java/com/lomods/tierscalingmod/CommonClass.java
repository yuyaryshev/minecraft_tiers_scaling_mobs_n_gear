package com.lomods.tierscalingmod;

import com.lomods.tierscalingmod.platform.Services;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// This class is part of the common project meaning it is shared between all supported loaders. Code written here can only
// import and access the vanilla codebase, libraries used by vanilla, and optionally third party libraries that provide
// common compatible binaries. This means common code can not directly use loader specific concepts such as Forge events
// however it will be compatible with all supported mod loaders.
public class CommonClass {
    public static final Map<UUID, Boolean> playerToggleMap = new HashMap<>();

    // The loader specific projects are able to import and use any code from the common project. This allows you to
    // write the majority of your code here and load it from your loader specific projects. This exam ple has some
    // code that gets invoked by the entry point of the loader specific projects.
    public static void init() {

        Constants.LOG.info("Hello from Common init on {}! we are currently in a {} environment!", Services.PLATFORM.getPlatformName(), Services.PLATFORM.getEnvironmentName());
        Constants.LOG.info("The ID for diamonds is {}", BuiltInRegistries.ITEM.getKey(Items.DIAMOND));

        // It is common for all supported loaders to provide a similar feature that can not be used directly in the
        // common code. A popular way to get around this is using Java's built-in service loader feature to create
        // your own abstraction layer. You can learn more about this in our provided services class. In this exam ple
        // we have an interface in the common code and use a loader specific implementation to delegate our call to
        // the platform specific approach.
        if (Services.PLATFORM.isModLoaded("tierscalingmod")) {

            Constants.LOG.info("Hello to tierscalingmod");
        }
    }

    // Converts tier level to LinearTier equivalent
    public static float linearizeTier(int tier) {
        // TODO тут нужно сделать линеаризацию tier'ов.
        //
        // В чем суть поясню на примере:
        // Допустим есть tier'ы, например: 1 wood, 2 stone, 3 copper, 4 iron, 5 diamond, 6 netherite
        //
        //
        // И вот допустим мы хотим чтобы
        // - stone от wood отличался чуть чуть
        // - а вот diamond от iron отличался в два раза
        //
        // Тогда это будет выглядеть вот так:
        // 1 wood => 10, 2 stone => 15, 3 copper => 25, 4 iron => 40, 5 diamond => 80, 6 netherite => 120
        //
        // То есть вот wood это 10, а stone это 15 - разница всего 5 - не большая разница.
        // То есть вот iron 40, diamond 80 - разница 40 - это уже большая разница
        //
        // Так вот функция линеаризации должна перейти от значения tier ко второму значению - линеаризованному tier.
        //

        return tier;
    }

    public static float getMobTier(LivingEntity livingEntity) {
        // TODO всякие свойства которые вероятно пригодятся для определения tier
        // livingEntity.getArmorSlots();
        // livingEntity.getArmorValue();
        // livingEntity.getActiveEffectsMap();
        // livingEntity.getAttribute();
        // livingEntity.getItemInHand();
        // livingEntity.getMobType();
        // livingEntity.getSpeed();
        // livingEntity.getUseItem();
        // livingEntity.getTags();
        // livingEntity.getName();
        // livingEntity.getDisplayName();
        // livingEntity.getActiveEffects();
        // livingEntity.getItemBySlot();
        // livingEntity.getEffect();
        // livingEntity.getAbsorptionAmount()
        // livingEntity.getMaxHealth();
        // livingEntity.level();
        return 1f;
    }

    public static float getItemTier(TieredItem item) {

        // TODO всякие свойства которые вероятно пригодятся для определения tier
        // item.getEnchantmentValue();
        // item.getClass();
        // item.getName();
        // item.getMaxDamage();
        // item.getRarity();
        // item.getDescriptionId();

        // TODO Тут нужна более сложная логика, поскольку существуют предметы из модов, которые не подпадают под ванильные tier'ы
        return item.getTier().getLevel();
    }

    public static float getDamageRespectingTiers(float attackerLinearTier, float victimLinearTier, float damage, float armor, float toughness) {
        // TODO тут нужно описать формулу расчета дамага с учетом разницы в tier'ах
        return 0f;
    }

    public static void onPlayerTick(Player player) {
        // Only run on the server side
        if (!player.level().isClientSide && player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer) player;

            // Check if the player is in the toggle map and if the behavior is enabled
            if (playerToggleMap.getOrDefault(serverPlayer.getUUID(), true)) {
                // Check if the player is not in survival mode
                if (serverPlayer.gameMode.getGameModeForPlayer() != GameType.SURVIVAL) {
                    // Set the player back to survival mode
                    //serverPlayer.setGameMode(GameType.SURVIVAL);
                }
            }
        }
    }
}