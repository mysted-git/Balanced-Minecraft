package com.balancedmc.mixins.transport;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Nether portals are 2x faster
 */
@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity {

    @Inject(
            method = "getMaxNetherPortalTime()I",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Integer> cir) {
        if (cir.getReturnValue() == 80) {
            cir.setReturnValue(40);
        }
    }
}
