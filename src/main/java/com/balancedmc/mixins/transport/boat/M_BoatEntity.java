package com.balancedmc.mixins.transport.boat;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BoatEntity.class)
public abstract class M_BoatEntity extends Entity {

    public M_BoatEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    /*
    /**
     * Slippery blocks now have no effect on boats
     *
    @Inject(method = "getNearbySlipperiness()F", at = @At("RETURN"), cancellable = true)
    private void injected(CallbackInfoReturnable<Float> cir) {
        float slipperiness = cir.getReturnValue();

        if (slipperiness > 0.6) {
            slipperiness = 0.6F;
        }

        cir.setReturnValue(slipperiness);
    }*/

    @Shadow
    private float velocityDecay;

    @Shadow
    private boolean pressingForward;

    /**
     * Boats travel twice as fast<br>
     * This applies until they travel over 16m/s, when they travel at normal speed
     */
    @Redirect(
            method = "updateVelocity()V",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/vehicle/BoatEntity;velocityDecay:F",
                    opcode = Opcodes.GETFIELD
            )
    )
    private float redirect(BoatEntity entity) {
        if (this.getVelocity().length() > 0.8) return this.velocityDecay;
        return pressingForward ? (1 + velocityDecay) / 2 : velocityDecay;
    }

}
