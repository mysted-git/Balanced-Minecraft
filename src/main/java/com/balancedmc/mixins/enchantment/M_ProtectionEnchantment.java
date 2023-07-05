package com.balancedmc.mixins.enchantment;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public abstract class M_ProtectionEnchantment {

    /**
     * Reduce environmental protection amounts (for level 5 enchants)
     */

    @Inject(
            method = "getProtectionAmount(ILnet/minecraft/entity/damage/DamageSource;)I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue((int) (cir.getReturnValue() * 0.8));
    }
}
