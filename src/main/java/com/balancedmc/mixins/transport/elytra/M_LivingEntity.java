package com.balancedmc.mixins.transport.elytra;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntity.class)
public abstract class M_LivingEntity {

    /**
     * Campfires boost elytra
     */
    @Redirect(
            method = "travel(Lnet/minecraft/util/math/Vec3d;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/LivingEntity;setVelocity(Lnet/minecraft/util/math/Vec3d;)V",
                    ordinal = 6
            )
    )
    private void redirect(LivingEntity entity, Vec3d vec3d) {
        BlockHitResult result = entity.getWorld().raycast(new RaycastContext(
                entity.getPos(),
                entity.getPos().subtract(0, 24, 0),
                RaycastContext.ShapeType.COLLIDER,
                RaycastContext.FluidHandling.NONE,
                entity
        ));
        BlockState state = entity.getWorld().getBlockState(result.getBlockPos());
        if (state.isOf(Blocks.CAMPFIRE)) {
            double max = state.get(CampfireBlock.SIGNAL_FIRE) ? 24 : 10;
            double dist = Math.sqrt(result.getBlockPos().getSquaredDistance(entity.getPos()));
            if (dist <= max) {
                entity.setVelocity(vec3d.add(0, ((max - dist) * (max + 30)) / (400 * max), 0));
            }
            return;
        }
        entity.setVelocity(vec3d);
    }
}
