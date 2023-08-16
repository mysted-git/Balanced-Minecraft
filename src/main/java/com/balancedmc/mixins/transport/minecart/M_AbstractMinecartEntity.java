package com.balancedmc.mixins.transport.minecart;

import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Unpowered powered rails give a greater deceleration
 */
@Mixin(AbstractMinecartEntity.class)
public abstract class M_AbstractMinecartEntity {

    @Redirect(
            method = "moveOnRail(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/math/Vec3d;multiply(DDD)Lnet/minecraft/util/math/Vec3d;",
                    ordinal = 0
            )
    )
    private Vec3d redirect(Vec3d vec, double x, double y, double z) {
        return Vec3d.ZERO;
    }
}
