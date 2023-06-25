package com.balancedmc.mixins.enchantment;

import net.minecraft.enchantment.DamageEnchantment;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DamageEnchantment.class)
public abstract class M_DamageEnchantment extends Enchantment {

    protected M_DamageEnchantment(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Shadow @Final private static final int[] BASE_POWERS = new int[]{1, 5, 5, 5, 5};
    @Shadow @Final private static final int[] POWERS_PER_LEVEL = new int[]{11, 8, 8, 8, 8};
    @Shadow @Final private static final int[] MIN_MAX_POWER_DIFFERENCES = new int[]{20, 20, 20, 20, 20};
    @Shadow @Final public int typeIndex;

    @Inject(
            method = "getAttackDamage(ILnet/minecraft/entity/EntityGroup;)F",
            at = @At("TAIL"),
            cancellable = true
    )
    private void injected(int level, EntityGroup group, CallbackInfoReturnable<Float> cir) {
        if (this.typeIndex == 3 && group == EntityGroup.AQUATIC) {
            cir.setReturnValue(level * 2.5f);
        }
        if (this.typeIndex == 4 && group == EntityGroup.ILLAGER) {
            cir.setReturnValue(level * 2.5f);
        }
    }
}
