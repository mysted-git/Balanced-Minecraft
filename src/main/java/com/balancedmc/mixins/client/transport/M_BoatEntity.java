package com.balancedmc.mixins.client.transport;

import net.minecraft.entity.vehicle.BoatEntity;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoatEntity.class)
public abstract class M_BoatEntity {

    /**
     * @author HB0P
     * @reason Slippery blocks now have no effect on boats
     */

    @Inject(method = "getNearbySlipperiness()F", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Float> cir) {
        float slipperiness = cir.getReturnValue();

        if (slipperiness > 0.6) {
            slipperiness = 0.6F;
        }

        cir.setReturnValue(slipperiness);
    }

    /**
     * @author HB0P
     * @reason Boats travel faster
     */

    @Shadow
    private float velocityDecay;

    @Shadow
    private boolean pressingForward;

    @Redirect(method = "updateVelocity()V", at = @At(value = "FIELD", target = "Lnet/minecraft/entity/vehicle/BoatEntity;velocityDecay:F", opcode = Opcodes.GETFIELD))
    private float redirect(BoatEntity entity) {
        return pressingForward ? (1 + velocityDecay) / 2 : velocityDecay;
    }
}
