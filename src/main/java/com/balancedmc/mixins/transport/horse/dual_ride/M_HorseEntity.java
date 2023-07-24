package com.balancedmc.mixins.transport.horse.dual_ride;

import com.balancedmc.Main;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.HorseEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
