package com.balancedmc.mixins.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class M_LivingEntity {

    @Shadow public abstract boolean hasStatusEffect(StatusEffect effect);

    @Shadow public abstract StatusEffectInstance getStatusEffect(StatusEffect effect);

    /**
     * Increase the effect of leaping
     */
    @Inject(
            method = "getJumpBoostVelocityModifier()F",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue((float) (cir.getReturnValueF() * 1.6));
    }

    /**
     * Higher levels of dolphin's grace are effective
     */
    @Redirect(
            method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;"
            ),
            slice = @Slice(
                    to = @At(
                            value = "INVOKE",
                            target = "Lnet/minecraft/entity/LivingEntity;setVelocity(DDD)V"
                    )
            )
    )
    private Vec3d redirect(Vec3d vec3d, double x, double y, double z) {
        if (this.hasStatusEffect(StatusEffects.DOLPHINS_GRACE)) {
            x += this.getStatusEffect(StatusEffects.DOLPHINS_GRACE).getAmplifier() * 0.01;
        }
        return vec3d.multiply(x, y, x);
    }
}
