package com.lomods.tierscalingmod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
//import virtuoel.pehkui.api.ScaleTypes;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class MobUtils {
    private static final String TIER_SCALING_DONE_TAG = "TierDone";

    public static final Function<Entity, ResourceLocation> ENTITY_ID = e -> BuiltInRegistries.ENTITY_TYPE.getKey(e.getType());
    public static final UUID ATTRIBUTE_MOD = UUID.fromString("7c7e5c2d-1eb0-434a-858f-3ab81f52832c");

    public static boolean isMobProcessed(Mob mob) {
        return mob.getTags().contains(TIER_SCALING_DONE_TAG);
    }

    public static void markMobProcessed(Mob mob) {
        mob.addTag(TIER_SCALING_DONE_TAG);
    }

    public static ItemStack getEquip(Mob e, EquipmentSlot slot, float difficulty) {
        if(slot == EquipmentSlot.CHEST) {
            return new ItemStack(Items.IRON_CHESTPLATE);
        }
        return ItemStack.EMPTY;
    }


    public static <T> boolean isInList(T entry, List<? extends String> list, boolean reverse, Function<T, ResourceLocation> mapper) {
        if (reverse)
            return !isInList(entry, list, false, mapper);
        ResourceLocation res = mapper.apply(entry);
        return list.contains(res.getPath()) || list.contains(res.toString());
    }

    public static void reequipMob(Mob mob, float difficulty) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            mob.setDropChance(slot, -100);
            ItemStack existingItem =  mob.getItemBySlot(slot);
            if (!mob.getItemBySlot(slot).isEmpty()) {
                if (existingItem.isEnchanted()) {
                    existingItem.removeTagKey("Enchantments");
                }

                continue;
            }
//              if (slot.getType() == EquipmentSlot.Type.HAND)
//                  continue;
//              EquipmentSlot.MAINHAND

            // Because of bartering they'll throw weapon out immediately
            if (mob instanceof AbstractPiglin && slot == EquipmentSlot.OFFHAND)
                continue;


            ItemStack equip = MobUtils.getEquip(mob, slot, difficulty);
            if (!equip.isEmpty()) {
                mob.setItemSlot(slot, equip);
            }
        }
    }

    public static void modifyAttr(Mob mob, Attribute attr, double targetValue) {
        AttributeInstance inst = mob.getAttribute(attr);
        if (inst == null || inst.getModifier(ATTRIBUTE_MOD) != null)
            return;

        double oldValue = inst.getBaseValue();
        if(oldValue != targetValue) {
            double addedValue =targetValue - oldValue ;
            inst.addPermanentModifier(new AttributeModifier(ATTRIBUTE_MOD, "im_modifier", addedValue, AttributeModifier.Operation.ADDITION));
        }
    }

    private static void applyChangesToMob(Mob mob, float difficulty) {
        if(isMobProcessed(mob)) {
            return;
        }
//        markMobProcessed(mob); // Moved to end of function for debug convenience

        MobUtils.reequipMob(mob, difficulty);
        MobUtils.modifyAttr(mob, Attributes.MAX_HEALTH, 7);
        MobUtils.modifyAttr(mob, Attributes.ATTACK_DAMAGE, 1);

//        MobUtils.modifyAttr(mob, Attributes.KNOCKBACK_RESISTANCE, 1);
//        MobUtils.modifyAttr(mob, Attributes.MOVEMENT_SPEED, 1);
//        if (!flags.modifyAttributes) {
//            if (!Config.CommonConfig.entityBlacklist.hasFlag(mob, EntityModifyFlagConfig.Flags.ATTRIBUTES, Config.CommonConfig.mobAttributeWhitelist)) {
//                if (Config.CommonConfig.healthIncrease != 0 && !Config.CommonConfig.useScalingHealthMod) {
//                    MobUtils.modifyAttr(mob, Attributes.MAX_HEALTH, Config.CommonConfig.healthIncrease * 0.016, Config.CommonConfig.healthMax, difficulty, true);
//                    mob.setHealth(mob.getMaxHealth());
//                }
//                if (Config.CommonConfig.damageIncrease != 0 && !Config.CommonConfig.useScalingHealthMod)
//                    MobUtils.modifyAttr(mob, Attributes.ATTACK_DAMAGE, Config.CommonConfig.damageIncrease * 0.008, Config.CommonConfig.damageMax, difficulty, true);
//                if (Config.CommonConfig.speedIncrease != 0)
//                    MobUtils.modifyAttr(mob, Attributes.MOVEMENT_SPEED, Config.CommonConfig.speedIncrease * 0.0008, Config.CommonConfig.speedMax, difficulty, false);
//                if (Config.CommonConfig.knockbackIncrease != 0)
//                    MobUtils.modifyAttr(mob, Attributes.KNOCKBACK_RESISTANCE, Config.CommonConfig.knockbackIncrease * 0.002, Config.CommonConfig.knockbackMax, difficulty, false);
//                if (Config.CommonConfig.magicResIncrease != 0)
//                    EntityFlags.get(mob).magicRes = Math.min(Config.CommonConfig.magicResIncrease * 0.0016f * difficulty, Config.CommonConfig.magicResMax);
//                if (Config.CommonConfig.projectileIncrease != 0)
//                    EntityFlags.get(mob).projMult = 1 +
//                            (Config.CommonConfig.projectileMax <= 0 ? Config.CommonConfig.projectileIncrease * 0.008f * difficulty : Math.min(Config.CommonConfig.projectileIncrease * 0.008f * difficulty, Config.CommonConfig.projectileMax - 1));
//                if (Config.CommonConfig.explosionIncrease != 0)
//                    EntityFlags.get(mob).explosionMult = 1 +
//                            (Config.CommonConfig.explosionMax <= 0 ? Config.CommonConfig.explosionIncrease * 0.003f * difficulty : Math.min(Config.CommonConfig.explosionIncrease * 0.003f * difficulty, Config.CommonConfig.explosionMax - 1));
//            }
//            flags.modifyAttributes = true;
//        }

        markMobProcessed(mob);
    }

    public static void onMobLoad(Mob mob) {
        if (mob.level().isClientSide)
            return;
        // boolean mobGriefing = mob.level().getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING);
        float difficulty = 1;
        applyChangesToMob(mob, difficulty);
    }

    public static void onEntityLoad(Entity entity, ServerLevel world) {
        if (entity instanceof Mob mob)
            onMobLoad(mob);
    }
}

// YYA Useful code snippets
//         if (mob.getRandom().nextFloat() < (Config.CommonConfig.baseEquipChance + time)) { // Random number

// Code for reference: ImprovedMobs