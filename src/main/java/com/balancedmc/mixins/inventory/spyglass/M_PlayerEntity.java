package com.balancedmc.mixins.inventory.spyglass;

import com.balancedmc.MainClient;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class M_PlayerEntity {

    @Inject(
            method = "isUsingSpyglass",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injected(CallbackInfoReturnable<Boolean> isUsingSpyglass) {
        if (MainClient.instance.isUsingSpyglass) {
            isUsingSpyglass.setReturnValue(true);
        }
    }
}
