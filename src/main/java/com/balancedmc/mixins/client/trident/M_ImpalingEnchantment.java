package com.balancedmc.mixins.client.trident;

import net.minecraft.enchantment.ImpalingEnchantment;
import net.minecraft.entity.EntityGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ImpalingEnchantment.class)
public abstract class M_ImpalingEnchantment {

    /**
     * @author HB0P
     * @reason Impaling deals extra damage to non-aquatic mobs
     */

    @Overwrite
    public float getAttackDamage(int level, EntityGroup group) {
        return level * (group == EntityGroup.AQUATIC ? 2.5F : 1F);
    }
}
