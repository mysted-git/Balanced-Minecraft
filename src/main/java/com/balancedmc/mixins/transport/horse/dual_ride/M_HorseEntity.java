package com.balancedmc.mixins.transport.horse.dual_ride;

import net.minecraft.entity.passive.HorseEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(HorseEntity.class)
public abstract class M_HorseEntity {

    @Redirect(
            method = "interactMob(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/Hand;)Lnet/minecraft/util/ActionResult;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/passive/HorseEntity;hasPassengers()Z"
            )
    )
    private boolean redirect(HorseEntity horse) {
        return horse.getPassengerList().size() >= 2;
    }
}
