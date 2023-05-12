package com.balancedmc.mixins.phantom.carry;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Prevent players from dismounting phantoms with shift
 */
@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity {

    @Inject(
            method = "shouldDismount()Z",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        if (((PlayerEntity)(Object)this).getVehicle() instanceof PhantomEntity) {
            cir.setReturnValue(false);
        }
    }
}
