package com.lomods.tierscalingmod.mixin;

import net.minecraft.world.damagesource.CombatRules;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CombatRules.class)
public class CombatRulesMixin {
    @Inject(method = "getDamageAfterAbsorb", at = @At("HEAD"), cancellable = true)
    private static void onGetDamageAfterAbsorb(float damage, float armor, float toughness, CallbackInfoReturnable<Float> cir) {
        float r = 1f;
        cir.setReturnValue(r);
    }
}