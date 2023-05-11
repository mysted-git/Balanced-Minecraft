package com.balancedmc.mixins.potions;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class M_LivingEntity {

    /**
     * @author HB0P
     * @reason Increase the effect of leaping
     */

    @Inject(method = "getJumpBoostVelocityModifier()D", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Double> cir) {
        cir.setReturnValue(cir.getReturnValueD() * 1.6);
    }
}
