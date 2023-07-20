package com.balancedmc.mixins.trident;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.RiptideEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(RiptideEnchantment.class)
public abstract class M_RiptideEnchantment extends Enchantment {

    protected M_RiptideEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    /**
     * @author HB0P
     * @reason Riptide is compatible with loyalty and channeling
     */
    @Overwrite
    @Override
    public boolean canAccept(Enchantment other) {
        return super.canAccept(other);
    }
}
