package com.balancedmc.mixins.client.enchanting;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DamageEnchantment.class)
public abstract class M_DamageEnchantment {

    /**
     * @author HB0P
     * @reason Damage enchantments are no longer mutually exclusive
     */

    @Overwrite
    public boolean canAccept(Enchantment other) {
        return (DamageEnchantment)(Object)this != other;
    }
}
