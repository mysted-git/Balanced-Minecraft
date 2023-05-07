package com.balancedmc.mixins.client.village;

import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Remove leatherworker
 */
@Mixin(VillagerProfession.class)
public abstract class M_VillagerProfession {

    @Inject(method = "register(Ljava/lang/String;Lnet/minecraft/registry/RegistryKey;Lnet/minecraft/sound/SoundEvent;)Lnet/minecraft/village/VillagerProfession;", at = @At("HEAD"), cancellable = true)
    private static void injected(String id, RegistryKey<PointOfInterestType> heldWorkstation, @Nullable SoundEvent workSound, CallbackInfoReturnable<VillagerProfession> cir) {
        if (id.equals("leatherworker")) {
            cir.cancel();
        }
    }
}
