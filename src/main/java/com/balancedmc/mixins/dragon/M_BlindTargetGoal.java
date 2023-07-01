package com.balancedmc.mixins.dragon;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Illusioners give different status effects
 */
@Mixin(targets = "net.minecraft.entity.mob.IllusionerEntity$BlindTargetGoal")
public abstract class M_BlindTargetGoal {

    @Redirect(
            method = "castSpell()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/effect/StatusEffects;BLINDNESS:Lnet/minecraft/entity/effect/StatusEffect;"
            )
    )
    private StatusEffect redirect() {
        return new StatusEffect[]{
                StatusEffects.SLOWNESS,
                StatusEffects.WEAKNESS,
                StatusEffects.POISON
        }[(int) (Math.random() * 3)];
    }
}
