package com.balancedmc.mixins.phantom;

import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Change rendering of entities carried by phantoms
 */
@Mixin(LivingEntityRenderer.class)
public abstract class M_LivingEntityRenderer {

    /**
     * Render upside down
     */
    @Inject(
            method = "shouldFlipUpsideDown(Lnet/minecraft/entity/LivingEntity;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void injected(LivingEntity entity, CallbackInfoReturnable<Boolean> cir) {
        if (entity.getVehicle() instanceof PhantomEntity) {
            cir.setReturnValue(true);
        }
    }

    /**
     * Do not render sitting
     */
    @Redirect(
            method = "render(Lnet/minecraft/entity/LivingEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;hasVehicle()Z"
            )
    )
    private boolean redirect(LivingEntity entity) {
        if (entity.getVehicle() instanceof PhantomEntity) {
            return false;
        }
        return entity.hasVehicle();
    }
}
