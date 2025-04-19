package com.lomods.tierscalingmod;

import net.minecraft.Util;
import net.minecraft.world.item.*;

import java.lang.reflect.Field;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Set;

public class UpdateHelper {
    private static final Set<Object> knownThings = new HashSet<>();

    // Generic method to edit a field using reflection
    public static void editField(Object targetObject, String fieldName, Object newValue) {
        try {
            // Get the class of the target object
            Class<?> targetClass = targetObject.getClass();

            // Get the declared field from the class
            Field field = targetClass.getDeclaredField(fieldName);

            // Make the field accessible if it's private
            field.setAccessible(true);

            // Set the new value for the field
            field.set(targetObject, newValue);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace(); // Handle exceptions during reflection
        }
    }

    public static void updateTier(Tier m) {
        if(knownThings.contains(m)) {
            return;
        }
        knownThings.add(m);

        editField(m,"damage", 28);
    }

    public static void updateArmorMaterial(ArmorMaterial m) {
        if(knownThings.contains(m)) {
            return;
        }
        knownThings.add(m);

        editField(m,"toughness", 11);
        editField(m,"protectionFunctionForType", (EnumMap) Util.make(new EnumMap(ArmorItem.Type.class), (kv) -> {
                kv.put(ArmorItem.Type.HELMET, 64);
                kv.put(ArmorItem.Type.BOOTS, 63);
                kv.put(ArmorItem.Type.LEGGINGS, 66);
                kv.put(ArmorItem.Type.CHESTPLATE, 68);
       }));
    }
}
