package com.balancedmc.mixins.dragon;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Illusioners give different status effects<br>
 * They can give these effects regardless of local difficulty
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

    @Redirect(
            method = "canStart()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/LocalDifficulty;isHarderThan(F)Z"
            )
    )
    private boolean redirect(LocalDifficulty instance, float difficulty) {
        return true;
    }
}
