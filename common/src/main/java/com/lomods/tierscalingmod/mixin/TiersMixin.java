package com.lomods.tierscalingmod.mixin;


//import net.minecraft.world.item.ToolMaterial;
//import net.minecraft.world.item.ToolMaterials;

import com.lomods.tierscalingmod.UpdateHelper;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TieredItem;
import net.minecraft.world.item.Tiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(Tiers.class)
public class TiersMixin {

//    @Inject(method = "<init>",
//            at = @At(value = "FIELD",
//                    target = "Lnet/minecraft/world/item/Tiers;level:I",
//                    opcode = 181, // 181 is the opcode for PUTFIELD (setting a field)
//                    shift = At.Shift.BEFORE)) // Inject before the level is assigned

    @Inject(method = "<init>",
            at = @At(value = "RETURN",
            target = "Lnet/minecraft/world/item/Tiers;level:I"))
    private void injectBeforeLevelAssignment(String enumItemName, int uuuu, int level, int uses, float speed, float damage, int enchantmentValue, Supplier repairIngredient, CallbackInfo ci) {
        UpdateHelper.updateTier((Tier) this);
    }
}
