package com.balancedmc.mixins.trident;

import net.minecraft.block.LightningRodBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LightningRodBlock.class)
public abstract class M_LightningRodBlock {

    /**
     * Channeling works in the rain
     */
    @Redirect(
            method = "onProjectileHit(Lnet/minecraft/world/World;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/hit/BlockHitResult;Lnet/minecraft/entity/projectile/ProjectileEntity;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;isThundering()Z"
            )
    )
    private boolean redirect(World world) {
        return world.isRaining();
    }
}
