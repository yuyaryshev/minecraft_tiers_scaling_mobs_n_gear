package com.lomods.tierscalingmod.mixin;

import com.lomods.tierscalingmod.PlayerDataHelper;
import com.lomods.tierscalingmod.ServerPlayerAcessor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.gameevent.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import static java.util.function.Predicate.isEqual;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.attribute.EntityAttributeInstance;
import net.minecraft.world.entity.attribute.EntityAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {



    @Inject(method = "tick", at = @At("HEAD"))
    private void modifyMaxHealth(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        AttributeInstance maxHealthAttr = entity.getAttributes().getInstance(Attributes.MAX_HEALTH);

        if (maxHealthAttr != null) {
            double newMaxHealth = maxHealthAttr.getBaseValue() + 5.0; // Adds 5 HP
            if (newMaxHealth != maxHealthAttr.getBaseValue()) {
                maxHealthAttr.setBaseValue(newMaxHealth);
            }
        }
    }

    private void yyoverride_LivingEntity_actuallyHurt(LivingEntity entity, float f, DamageSource damageSource, float damage) {
        if (entity.isInvulnerableTo(damageSource)) {
            return;
        }

        // TODO REMOVE THIS!
        if (!entity.isInvulnerableTo(damageSource)) {
//            return;
        }

//        f = entity.getDamageAfterArmorAbsorb(damageSource, f);
//        float g = f = entity.getDamageAfterMagicAbsorb(damageSource, f);
        float g = f = damage;

//        f = Math.max(f - entity.getAbsorptionAmount(), 0.0f);
//        entity.setAbsorptionAmount(entity.getAbsorptionAmount() - (g - f));
//        float h = g - f;
        //if (h > 0.0f && h < 3.4028235E37f && (entity = damageSource.getEntity()) instanceof ServerPlayer) {
//            ServerPlayer serverPlayer = (ServerPlayer)entity;
  //          serverPlayer.awardStat(Stats.DAMAGE_DEALT_ABSORBED, Math.round(h * 10.0f));
    //    }

        if (f == 0.0f) {
            return;
        }
        entity.getCombatTracker().recordDamage(damageSource, f);
        entity.setHealth(entity.getHealth() - f);
        entity.setAbsorptionAmount(entity.getAbsorptionAmount() - f);
        entity.gameEvent(GameEvent.ENTITY_DAMAGE);
//        }
    }

//    @ModifyVariable(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At(value= "HEAD"))
//    private float LivingEntity_actuallyHurt(float f, DamageSource damageSource, float damage) {
//        LivingEntity livingEntity = (LivingEntity)(Object)this;
//        yyoverride_LivingEntity_actuallyHurt(livingEntity, f, damageSource, damage);
//        return 0f;
//    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void yyoverride_LivingEntity_actuallyHurt(DamageSource damageSource, float damage, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;

        yyoverride_LivingEntity_actuallyHurt(livingEntity, 1, damageSource, damage);

        ci.cancel();
    }


    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    private void yyoverride_LivingEntity_hurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
        yyoverride_LivingEntity_actuallyHurt(livingEntity, 1, damageSource, damage);
        cir.setReturnValue(false);
    }

////    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
////    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
////        cir.setReturnValue(false);
////    }
//

//
//    // @ModifyVariable(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At(value= "HEAD"))
//    private float LivingEntity_actuallyHurt33(float f, DamageSource damageSource, float damage) {
//        LivingEntity livingEntity = (LivingEntity)(Object)this;
//
////        Level world = livingEntity.level();
////
////        float newDamage = CollectiveEntityEvents.ON_LIVING_DAMAGE_CALC.invoker().onLivingDamageCalc(world, livingEntity, damageSource, f);
////        if (newDamage != -1 && newDamage != f) {
////            return newDamage;
////        }
//
//        return 0.0f;//f;
//    }
//
//    @ModifyVariable(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At(value= "INVOKE_ASSIGN", target = "Ljava/lang/Math;max(FF)F", ordinal = 0), ordinal = 0, argsOnly = true)
//    private float LivingEntity_actuallyHurt22(float f, DamageSource damageSource, float damage) {
//        LivingEntity livingEntity = (LivingEntity)(Object)this;
//
////        Level world = livingEntity.level();
////
////        float newDamage = CollectiveEntityEvents.ON_LIVING_DAMAGE_CALC.invoker().onLivingDamageCalc(world, livingEntity, damageSource, f);
////        if (newDamage != -1 && newDamage != f) {
////            return newDamage;
////        }
//
//        return 0.0f;//f;
//    }
//
//    @ModifyVariable(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"))
//    private float LivingEntity_hurt(float damage, DamageSource damageSource) {
//        LivingEntity entity = (LivingEntity) (Object) this;
//        return 0.0f;
//    }
//
////    @ModifyVariable(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), argsOnly = true)
//    private float LivingEntity_hurt22(float damage, DamageSource damageSource) {
//        LivingEntity entity = (LivingEntity) (Object) this;
//        return 0;
////        // Check if the entity is a player and in Angelic Mode
////        if (entity instanceof ServerPlayer player) {
////            if (((ServerPlayerAcessor)player).isAngelicMode()) {
////                // Allow void damage to bypass Angelic Mode
//////                if (damageSource.type() == DamageTypes.FELL_OUT_OF_WORLD) {
//////                    return f;
//////                }
////
////
////                // Prevent the player from dropping below 2 HP (1 heart)
////                float currentHealth = player.getHealth();
////                if (currentHealth - damage < 2.0F) {
////                    return currentHealth - 2.0F; // Reduce damage so that player remains at 2 HP
////                }
////            }
////        }
////
////        // Check if the entity is a player and in Angelic Mode
////        if (damageSource.getEntity() instanceof ServerPlayer player) {
////            if (((ServerPlayerAcessor)player).isAngelicMode()) {
////                // If the player's health is below 3 HP (1.5 hearts), limit the outgoing damage to 1
////                if (player.getHealth() < 3.0F) {
////                    return Math.min(damage, 1.0F); // Cap the damage at 1
////                }
////            }
////        }
////        return damage; // Default behavior if not in Angelic Mode or health is sufficient
//    }
//
////    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
////    public void LivingEntity_hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
////        LivingEntity livingEntity = (LivingEntity)(Object)this;
////
//////        Level world = livingEntity.level();
//////
//////        if (!CollectiveEntityEvents.ON_LIVING_ATTACK.invoker().onLivingAttack(world, livingEntity, damageSource, f)) {
//////            ci.setReturnValue(false);
//////        }
////    }
//
////    @ModifyArgs(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
////    private void LivingEntity_actuallyHurt(Args args) {
////        double dbg = 5;
////
//////        int a0 = args.get(0);
//////        double a1 = args.get(1);
//////        boolean a2 = args.get(2);
//////        args.set(0, a0 + 3);
//////        args.set(1, a1 * 2.0D);
//////        args.set(2, !a2);
////    }
//
////    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
////    private void LivingEntity_actuallyHurt(DamageSource damageSource, float damage, CallbackInfo ci, @Local LocalRef<Float> damageRef) {
////        LivingEntity livingEntity = (LivingEntity)(Object)this;
////        //ci.setReturnValue(1);
////        //return 2;
////    }
//
////    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
////    private void LivingEntity_actuallyHurt(DamageSource damageSource, float damage, CallbackInfo ci) {
////        LivingEntity livingEntity = (LivingEntity)(Object)this;
////        //ci.setReturnValue(1);
////        //return 2;
////    }
}
