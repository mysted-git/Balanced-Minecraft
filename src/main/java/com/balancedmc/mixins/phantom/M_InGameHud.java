package com.balancedmc.mixins.phantom;

import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.PhantomEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Remove riding hearts when carried by phantom
 */
@Mixin(InGameHud.class)
public abstract class M_InGameHud {

    @Shadow
    protected abstract int getHeartCount(LivingEntity entity);

    @Shadow
    protected abstract LivingEntity getRiddenEntity();

    @Shadow
    protected abstract void renderMountHealth(MatrixStack matrices);

    @Redirect(
            method = "renderStatusBars(Lnet/minecraft/client/util/math/MatrixStack;)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;getHeartCount(Lnet/minecraft/entity/LivingEntity;)I"
            )
    )
    private int redirect(InGameHud hud, LivingEntity entity) {
        if (entity instanceof PhantomEntity) {
            return 0;
        }
        return getHeartCount(entity);
    }

    @Redirect(
            method = "render(Lnet/minecraft/client/util/math/MatrixStack;F)V",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/hud/InGameHud;renderMountHealth(Lnet/minecraft/client/util/math/MatrixStack;)V"
            )
    )
    private void redirect(InGameHud hud, MatrixStack matrices) {
        if (!(getRiddenEntity() instanceof PhantomEntity)) {
            renderMountHealth(matrices);
        }
    }
}
