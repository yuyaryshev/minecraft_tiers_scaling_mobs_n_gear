package com.lomods.tierscalingmod.mixin;

import com.lomods.tierscalingmod.Constants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

//    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
//    private void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
//        cir.setReturnValue(false);
//    }

//    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
//    private float LivingEntity_actuallyHurt(DamageSource damageSource, float damage, CallbackInfoReturnable<Integer> cir) {
//        LivingEntity livingEntity = (LivingEntity)(Object)this;
//        cir.setReturnValue(1);
//        return 2;
//    }
}
