package com.balancedmc.mixins.phantom;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.mob.PhantomEntity;
import net.minecraft.network.packet.s2c.play.EntityPassengersSetS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Remove dismount message when riding a phantom
 */
@Mixin(ClientPlayNetworkHandler.class)
public abstract class M_ClientPlayNetworkHandler {

    @Inject(
            method = "onEntityPassengersSet(Lnet/minecraft/network/packet/s2c/play/EntityPassengersSetS2CPacket;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/text/MutableText;"
            ),
            cancellable = true
    )
    private void inject(EntityPassengersSetS2CPacket packet, CallbackInfo ci) {
        if (((ClientPlayNetworkHandler)(Object)this).getWorld().getEntityById(packet.getId()) instanceof PhantomEntity) {
            ci.cancel();
        }
    }
}
