package com.balancedmc.mixins.phantom.carry;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Phantoms cannot attack when<br>
 * They are carrying a player<br>
 * The player is being carried
 */
@Mixin(targets = "net.minecraft.entity.mob.PhantomEntity$FindTargetGoal")
public abstract class M_FindTargetGoal {

    @Redirect(
            method = "canStart()Z",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/mob/PhantomEntity;setTarget(Lnet/minecraft/entity/LivingEntity;)V"
            )
    )
    private void redirect(PhantomEntity phantom, LivingEntity entity) {
        if (!phantom.hasPassengers() && !entity.hasVehicle()) {
            phantom.setTarget(entity);
        }
    }
}
