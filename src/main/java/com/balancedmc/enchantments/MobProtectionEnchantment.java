package com.balancedmc.enchantments;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;

import java.util.List;

public class MobProtectionEnchantment extends Enchantment {

    private final EntityGroup group;
    private final EntityType[] entities;

    public MobProtectionEnchantment(EntityGroup group, EntityType[] entities) {
        super(Rarity.COMMON, EnchantmentTarget.ARMOR, new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET});
        this.group = group;
        this.entities = entities;
    }

    public MobProtectionEnchantment(EntityGroup group) {
        this(group, new EntityType[0]);
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
        else if (entity.getGroup() == group) {
            return level;
        }
        else if (List.of(entities).contains(entity.getType())) {
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
