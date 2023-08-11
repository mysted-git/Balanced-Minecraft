package com.balancedmc.mixins.inventory.spyglass;

import com.balancedmc.MainClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class M_AbstractClientPlayerEntity {

    @Redirect(
            method = "getFovMultiplier",
            at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F")
    )
    private float redirect(float delta, float start, float end) {
        if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson()) {
            float spyglassScale = MainClient.instance.isUsingSpyglass ? start * 0.1F : start;
            return MathHelper.lerp(delta, spyglassScale, end * spyglassScale);
        }
        return MathHelper.lerp(delta, start, end);
    }
}
