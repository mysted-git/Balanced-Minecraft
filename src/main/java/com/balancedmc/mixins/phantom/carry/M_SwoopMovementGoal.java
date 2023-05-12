package com.balancedmc.mixins.phantom.carry;

import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Phantoms pick up players instead of attacking
 */
@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$SwoopMovementGoal")
public abstract class M_SwoopMovementGoal {

    @Redirect(
            method = "tick()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/PhantomEntity;tryAttack(Lnet/minecraft/entity/Entity;)Z"
            )
    )
    private boolean redirect(PhantomEntity phantom, Entity entity) {
        if (!entity.hasVehicle()) {
            phantom.tryAttack(entity);
            entity.startRiding(phantom);
        }
        return false;
    }
}
