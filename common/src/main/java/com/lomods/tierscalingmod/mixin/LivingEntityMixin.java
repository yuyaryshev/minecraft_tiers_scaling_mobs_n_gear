package com.lomods.tierscalingmod.mixin;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
//    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
//    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(false);
//    }

//    @ModifyArgs(method = "actuallyHurt()V", at = @At(value = "INVOKE", target = "La/b/c/Something;actuallyHurt(IDZ)V"))
//    private void injected(Args args) {
//        int a0 = args.get(0);
//        double a1 = args.get(1);
//        boolean a2 = args.get(2);
//        args.set(0, a0 + 3);
//        args.set(1, a1 * 2.0D);
//        args.set(2, !a2);
//    }


    @ModifyVariable(method = "actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V", at = @At(value= "INVOKE_ASSIGN", target = "Ljava/lang/Math;max(FF)F", ordinal = 0), ordinal = 0, argsOnly = true)
    private float LivingEntity_actuallyHurt(float f, DamageSource damageSource, float damage) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
//        Level world = livingEntity.level();
//
//        float newDamage = CollectiveEntityEvents.ON_LIVING_DAMAGE_CALC.invoker().onLivingDamageCalc(world, livingEntity, damageSource, f);
//        if (newDamage != -1 && newDamage != f) {
//            return newDamage;
//        }

        return f;
    }

    @Inject(method = "hurt(Lnet/minecraft/world/damagesource/DamageSource;F)Z", at = @At(value = "HEAD"), cancellable = true)
    public void LivingEntity_hurt(DamageSource damageSource, float f, CallbackInfoReturnable<Boolean> ci) {
        LivingEntity livingEntity = (LivingEntity)(Object)this;
//        Level world = livingEntity.level();
//
//        if (!CollectiveEntityEvents.ON_LIVING_ATTACK.invoker().onLivingAttack(world, livingEntity, damageSource, f)) {
//            ci.setReturnValue(false);
//        }
    }

//    @ModifyArgs(method = "actuallyHurt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;actuallyHurt(Lnet/minecraft/world/damagesource/DamageSource;F)V"))
//    private void LivingEntity_actuallyHurt(Args args) {
//        double dbg = 5;
//
////        int a0 = args.get(0);
////        double a1 = args.get(1);
////        boolean a2 = args.get(2);
////        args.set(0, a0 + 3);
////        args.set(1, a1 * 2.0D);
////        args.set(2, !a2);
//    }

//    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
//    private void LivingEntity_actuallyHurt(DamageSource damageSource, float damage, CallbackInfo ci, @Local LocalRef<Float> damageRef) {
//        LivingEntity livingEntity = (LivingEntity)(Object)this;
//        //ci.setReturnValue(1);
//        //return 2;
//    }

//    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
//    private void LivingEntity_actuallyHurt(DamageSource damageSource, float damage, CallbackInfo ci) {
//        LivingEntity livingEntity = (LivingEntity)(Object)this;
//        //ci.setReturnValue(1);
//        //return 2;
//    }
}
