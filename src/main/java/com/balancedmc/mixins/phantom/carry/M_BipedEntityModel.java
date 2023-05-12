package com.balancedmc.mixins.phantom.carry;

import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Render player's arms downwards when carried by a phantom
 */
@Mixin(BipedEntityModel.class)
public abstract class M_BipedEntityModel {

    @Shadow @Final
    public ModelPart rightArm;

    @Shadow @Final
    public ModelPart leftArm;

    @Inject(
            method = "positionRightArm",
            at = @At("TAIL")
    )
    private <T extends LivingEntity> void injectedRight(T entity, CallbackInfo ci) {
        if (entity.getVehicle() instanceof PhantomEntity) {
            this.rightArm.pitch = 179;
        }
    }

    @Inject(
            method = "positionLeftArm",
            at = @At("TAIL")
    )
    private <T extends LivingEntity> void injectedLeft(T entity, CallbackInfo ci) {
        if (entity.getVehicle() instanceof PhantomEntity) {
            this.leftArm.pitch = 179;
        }
    }
}
