package com.balancedmc.mixins.phantom.carry;

import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Prevent players from dismounting phantoms with shift
 */
@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity {

    @Redirect(
            method = "tickRiding()V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/PlayerEntity;stopRiding()V"
            )
    )
    private void redirect(PlayerEntity player) {
        if (player.getVehicle() instanceof PhantomEntity) {
            player.setSneaking(false);
        }
        else {
            player.stopRiding();
        }
    }
}
