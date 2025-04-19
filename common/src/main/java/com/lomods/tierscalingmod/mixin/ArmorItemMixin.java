package com.lomods.tierscalingmod.mixin;


//import net.minecraft.world.item.ToolMaterial;
//import net.minecraft.world.item.ToolMaterials;

import com.lomods.tierscalingmod.UpdateHelper;
import net.minecraft.world.item.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorItem.class)
public class ArmorItemMixin {
//     @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/Item;<init>(Lnet/minecraft/world/item/Item$Properties;)V"))
//    @Inject(method = "<init>", at = @At("HEAD"))

//    @Inject(method = "<init>", at = @At("RETURN"))

    @Inject(method = "<init>",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/item/ArmorItem;material:Lnet/minecraft/world/item/ArmorMaterial;",
                    opcode = 181, // 181 is the opcode for PUTFIELD
                    shift = At.Shift.BEFORE)
            )
    private void injectConstructor(ArmorMaterial armorMaterial, ArmorItem.Type armorItemType, Item.Properties itemProperties, CallbackInfo ci) {
        UpdateHelper.updateArmorMaterial(armorMaterial);
    }
}
// Test comment 2