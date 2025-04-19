package com.lomods.tierscalingmod.mixin;


//import net.minecraft.world.item.ToolMaterial;
//import net.minecraft.world.item.ToolMaterials;
import com.lomods.tierscalingmod.UpdateHelper;
import net.minecraft.world.item.*;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(TieredItem.class)
public class TieredItemMixin {
//    @Inject(method = "<init>", at = @At("TAIL"))

    @Inject(method = "<init>",
            at = @At(value = "FIELD",
                    target = "Lnet/minecraft/world/item/TieredItem;tier:Lnet/minecraft/world/item/Tier;",
                    opcode = 181, // 181 is the opcode for PUTFIELD
                    shift = At.Shift.BEFORE
            ))
    private void injectConstructor(Tier tier, Item.Properties properties, CallbackInfo ci) {
        UpdateHelper.updateTier(tier);
//        // Convert the Tier to MutableTier and set it in the TieredItem instance
//        MutableTier mutableTier = MutableTier.toMutableTier(tier);
//
//        // Here, you can cast `this` to `TieredItem` and modify the `tier` field
//        // Since `tier` is private, you may need to use reflection to set it
//        // Reflection code to modify the private field `tier`
//        try {
//            Field tierField = TieredItem.class.getDeclaredField("tier");
//            tierField.setAccessible(true); // Allow modification of the private field
//            tierField.set(this, mutableTier); // Replace the tier with MutableTier
//
//            Field damageField = Tiers.class.getDeclaredField("damage");
//            damageField.setAccessible(true);
//            damageField.set(tier, 78);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace(); // Handle any errors during reflection
//        }
    }
}
