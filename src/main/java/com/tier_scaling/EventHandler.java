// EventHandler.java - Add this file to your mod
package com.tier_scaling;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = TiersScalingMod.MODID)
public class EventHandler {
    private static final Set<Pair<UUID, UUID>> PROCESSED_ENTITY_PAIRS_THIS_TICK = new HashSet<>();

    @SubscribeEvent
    public static void onEntityHurt(LivingAttackEvent event) {
        DamageSource source = event.getSource();
        Entity sourceEntity = source.getEntity();

        if(sourceEntity == null) {
            return;
        }

        LivingEntity targetEntity = event.getEntity();
        if(targetEntity == null) {
            return;
        }

        Pair<UUID, UUID> entityPair = Pair.of(sourceEntity.getUUID(), targetEntity.getUUID());

        if (PROCESSED_ENTITY_PAIRS_THIS_TICK.contains(entityPair)) return;

        if (targetEntity.getCommandSenderWorld().isClientSide) return;

        int sourceTier;
        int targetTier;

        if (sourceEntity instanceof LivingEntity && targetEntity instanceof LivingEntity) {
            if (sourceEntity instanceof Player) {
                sourceTier = TierCalc.calcTierForPlayerAttacker((LivingEntity) sourceEntity);
            } else {
                sourceTier = TierCalc.calcTierForMob((LivingEntity) sourceEntity);
            }

            if (targetEntity instanceof Player) {
                targetTier = TierCalc.calcTierForPlayerVictim((LivingEntity) targetEntity);
            } else {
                targetTier = TierCalc.calcTierForMob((LivingEntity) targetEntity);
            }

            float originalDamage = event.getAmount();
            float adjustedDamage = TierCalc.adjustDamageByTier(originalDamage, sourceTier, targetTier);
            if(Math.abs(event.getAmount() - originalDamage) >0.05) {
                PROCESSED_ENTITY_PAIRS_THIS_TICK.add(entityPair);
                event.setCanceled(true);
                if(adjustedDamage > 0) {
                    targetEntity.hurt(event.getSource(), adjustedDamage);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        // Clear the set of processed entities at the end of each tick
        PROCESSED_ENTITY_PAIRS_THIS_TICK.clear();
    }
}
