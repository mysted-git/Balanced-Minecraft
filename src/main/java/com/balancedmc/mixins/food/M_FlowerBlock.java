package com.balancedmc.mixins.food;

import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.effect.StatusEffect;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Increase duration of beneficial suspicious stew effects
 */
@Mixin(FlowerBlock.class)
public abstract class M_FlowerBlock {

    @Shadow @Final private int effectInStewDuration;

    @Shadow @Final private StatusEffect effectInStew;

    @Inject(
            method = "getEffectInStewDuration()I",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.effectInStew.isBeneficial() ? effectInStewDuration * 5 : effectInStewDuration);
    }
}
