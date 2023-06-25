package com.balancedmc.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.function.Predicate;

public class MobProtectionEnchantment extends Enchantment {

    private final Predicate<LivingEntity> predicate;

    public MobProtectionEnchantment(Predicate<LivingEntity> p) {
        super(Rarity.COMMON, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        this.predicate = p;
    }

    public int getProtectionAmount(int level, DamageSource source) {
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        }
        else if (source.getAttacker() == null) {
            return 0;
        }
        else if (!(source.getAttacker() instanceof LivingEntity entity)) {
            return 0;
        }
        else if (predicate.test(entity)) {
            return level;
        }
        return 0;
    }

    public boolean canAccept(Enchantment other) {
        return !(other instanceof MobProtectionEnchantment);
    }

    public int getMaxLevel() {
        return 4;
    }
}
