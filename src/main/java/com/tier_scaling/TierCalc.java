package com.tier_scaling;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.ForgeRegistries;
import java.util.Map;

public class TierCalc {

    public static int calcTierForMob(LivingEntity mob) {
        EntityType<?> mobType = mob.getType();
        ResourceLocation mobId = EntityType.getKey(mobType);
        return Config.mobTiers.getOrDefault(mobId, 1); // Default  to tier 1 if not found
    }

    public static int calcTierForPlayerVictim(LivingEntity player) {
        int totalTier = 0;
        int itemCount = 0;

        for (ItemStack armorPiece : player.getArmorSlots()) {
            String itemId = ForgeRegistries.ITEMS.getKey(armorPiece.getItem()).toString();
            totalTier += Config.itemTiers.getOrDefault(itemId, 1); // Default to tier 1 if not found
            itemCount++;
        }

        return itemCount > 0 ? totalTier / itemCount : 1; // Average tier, default to 1
    }

    public static int calcTierForPlayerAttacker(LivingEntity player) {
        ItemStack weapon = player.getMainHandItem();
        String itemId = ForgeRegistries.ITEMS.getKey(weapon.getItem()).toString();
        return Config.itemTiers.getOrDefault(itemId, 1); // Default to tier 1 if not found
    }

    public static float adjustDamageByTier(float amount, float sourceTier, float targetTier) {
        int sourceLevel = Config.tierLinearization.getOrDefault((int) sourceTier, 1);
        int targetLevel = Config.tierLinearization.getOrDefault((int) targetTier, 1);

        // Example logic using the tier levels
        float adjustedDamage = amount * (sourceLevel / (float) targetLevel);

        return adjustedDamage;  // You will define the logic based on your requirements
    }

}
