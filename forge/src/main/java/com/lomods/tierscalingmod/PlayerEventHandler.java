package com.lomods.tierscalingmod;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TierScalingMod.MOD_ID)
public class PlayerEventHandler {


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
//
//        Pair<UUID, UUID> entityPair = Pair.of(sourceEntity.getUUID(), targetEntity.getUUID());
//
//        if (PROCESSED_ENTITY_PAIRS_THIS_TICK.contains(entityPair)) return;
//
//        if (targetEntity.getCommandSenderWorld().isClientSide) return;
//
//        int sourceTier;
//        int targetTier;
//
//        if (sourceEntity instanceof LivingEntity && targetEntity instanceof LivingEntity) {
//            if (sourceEntity instanceof Player) {
//                sourceTier = TierCalc.calcTierForPlayerAttacker((LivingEntity) sourceEntity);
//            } else {
//                sourceTier = TierCalc.calcTierForMob((LivingEntity) sourceEntity);
//            }
//
//            if (targetEntity instanceof Player) {
//                targetTier = TierCalc.calcTierForPlayerVictim((LivingEntity) targetEntity);
//            } else {
//                targetTier = TierCalc.calcTierForMob((LivingEntity) targetEntity);
//            }
//
//            float originalDamage = event.getAmount();
//            float adjustedDamage = TierCalc.adjustDamageByTier(originalDamage, sourceTier, targetTier);
//            if(Math.abs(event.getAmount() - originalDamage) >0.05) {
//                PROCESSED_ENTITY_PAIRS_THIS_TICK.add(entityPair);
//                event.setCanceled(true);
//                if(adjustedDamage > 0) {
//                    targetEntity.hurt(event.getSource(), adjustedDamage);
//                }
//            }
//        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        CommonClass.onPlayerTick(event.player);
    }
}
