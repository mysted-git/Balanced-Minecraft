package com.balancedmc.mixins.client.enchanting;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ProtectionEnchantment.class)
public abstract class M_ProtectionEnchantment {

    /**
     * @author HB0P
     * @reason Level 5 protection enchantments
     */

    @Overwrite
    public int getProtectionAmount(int level, DamageSource source) {
        ProtectionEnchantment enchantment = (ProtectionEnchantment)(Object)this;
        if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return 0;
        }
        else if (enchantment.protectionType == ProtectionEnchantment.Type.FIRE && source.isIn(DamageTypeTags.IS_FIRE)) {
            return (int) (level * 1.6);
        }
        else if (enchantment.protectionType == ProtectionEnchantment.Type.EXPLOSION && source.isIn(DamageTypeTags.IS_EXPLOSION)) {
            return (int) (level * 1.6);
        }
        else if (enchantment.protectionType == ProtectionEnchantment.Type.PROJECTILE && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            return (int) (level * 1.6);
        }
        else if (enchantment.protectionType == ProtectionEnchantment.Type.FALL && source.isIn(DamageTypeTags.IS_FALL)) {
            return (int) (level * 2.4);
        }
        else {
            return 0;
        }
    }
}
