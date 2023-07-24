package com.balancedmc.mixins.trident;

import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class M_TridentEntity {

    /**
     * Loyalty tridents return to player when thrown into the void
     */
    @Inject(
            method = "tick()V",
            at = @At("HEAD")
    )
    private void inject(CallbackInfo ci) {
        TridentEntity trident = (TridentEntity) (Object) this;
        int yPos = trident.getBlockY();
        int minY = trident.getWorld().getBottomY();

        if (yPos < minY) {
            trident.setNoClip(true);
        }
    }

    /**
     * Channeling works in the rain
     */
    @Redirect(
            method = "onEntityHit(Lnet/minecraft/util/hit/EntityHitResult;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/World;isThundering()Z"
            )
    )
    private boolean redirect(World world) {
        return world.isRaining();
    }
}
