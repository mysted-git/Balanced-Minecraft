package com.balancedmc.mixins.enchantment;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DamageEnchantment.class)
public abstract class M_DamageEnchantment extends Enchantment {

    protected M_DamageEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    /**
     * @author HB0P
     * @reason Damage enchantments are no longer mutually exclusive
     */

    @Overwrite
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }
}
